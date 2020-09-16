package com.bigdata.dw.gmall.realtime.app

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.alibaba.fastjson.JSON
import com.bigdata.dw.gamll.common.Constant
import com.bigdata.dw.gmall.realtime.bean.StartupLog
import com.bigdata.dw.gmall.realtime.util.{MyKafkaUtil, OffsetUtil, PropertyUtil, RedisUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

import scala.collection.mutable

/**
  * @ description: 
  * @ author: spencer
  * @ date: 2020/9/3 15:16
  */
object DauApp {

  def main(args: Array[String]): Unit = {
    // 1.从kafka读取数据
    val sparkConf: SparkConf = new SparkConf().setAppName("DauAPP").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(3))

    val topic = Constant.STARTUP_TOPIC

//    val kafkaStream: InputDStream[ConsumerRecord[String, String]] = MyKafkaUtil.getKafkaStream(ssc, topic)

    // 消费数据之前先获取偏移量
    val offsetMap: mutable.Map[TopicPartition, Long] = OffsetUtil.getOffsetMap(Constant.STARTUP_TOPIC, PropertyUtil.getProperty("kafka.group"))

    val kafkaStream: InputDStream[ConsumerRecord[String, String]] = if (offsetMap.isEmpty) {
      println("mysql中没有记录偏移量，从最开始消费")
      val kafkaStream1: InputDStream[ConsumerRecord[String, String]] = MyKafkaUtil.getKafkaStream(ssc, topic)
      kafkaStream1

    } else {
      println("mysql中记录了偏移量，从该偏移量处开始消费")
      val kafkaStream2: InputDStream[ConsumerRecord[String, String]] = MyKafkaUtil.getKafkaStream(ssc, topic, offsetMap)
      kafkaStream2
    }

    // kafka消费完成数据之后手动更新偏移量到mysql
    kafkaStream.foreachRDD(
      rdd => {
        if (rdd.count() > 0){
          rdd.foreach(record => println("kafka中接收到的数据：" + record))

          // TODO 对数据进行处理
          // 维护offset
          val offsetRanges: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

          for (o <- offsetRanges){
            println(s"topic=${o.topic},partition=${o.partition},fromOffset=${o.fromOffset},untilOffset=${o.untilOffset}")
          }
          //手动提交offset,默认提交到Checkpoint中
          //recordDStream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
          //实际中偏移量可以提交到MySQL/Redis中
          OffsetUtil.saveOffsetRanges(PropertyUtil.getProperty("kafka.group"),offsetRanges)

        }
      }
    )

    // 2.把数据解析，封装到样例类中
    val startupLogStream: DStream[StartupLog] = kafkaStream
      .map(_.value())
      .map(data => JSON.parseObject(data, classOf[StartupLog]))
//    startupLogStream.print()

    // 3.redis去重
    // 3.1先从redis中读取启动的语句，过滤掉已经启动的
    val filteredStream: DStream[StartupLog] = startupLogStream.transform(
      rdd => {
        val client: Jedis = RedisUtil.getJedisClient()
        // redis中的key topic_startup: 2020-09-01
        val midSet: util.Set[String] = client.smembers(Constant.STARTUP_TOPIC + ":" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()))

        client.close()
        // 广播
        val bd: Broadcast[util.Set[String]] = ssc.sparkContext.broadcast(midSet)

        // 过滤掉那些已经启动过的设备
        rdd.filter {
          log => {
            !bd.value.contains(log.mid)
          }
            // 在一个时间周期内，一个设备可能启动了多次，只能取第一次启动
        }.map(log => (log.mid, log))
          .groupByKey
          .map{
            case (_, logIter) =>
              // sortBy(_.ts).head
              logIter.toList.minBy(_.ts)
          }
      }
    )
    // 把第一次启动的设备的mid写入redis
    filteredStream.foreachRDD(
      rdd => {
        rdd.foreachPartition{
          logIter => {
            // 获取redis连接
            val client: Jedis = RedisUtil.getJedisClient()

            // 写入数据
            logIter.foreach(
            log => {
              val redisKey: String = Constant.STARTUP_TOPIC + ":" + log.logDate
              client.sadd(redisKey, log.mid)
              }
            )
            // 关闭连接
            client.close()

          }
        }
      }
    )

    filteredStream.print()

    // 4.写入hbase,使用phoenix-spark，先导入包
    import org.apache.phoenix.spark._

    filteredStream.foreachRDD(rdd => {
      // 提前在phoenix中创建要保存数据的表
      // 直接保存,实时写入phoenix
      rdd.saveToPhoenix(
        "GMALL0831_DAU",
        Seq("MID", "UID", "APPID", "AREA", "OS", "CHANNEL", "LOGTYPE",
          "VERSION", "TS", "LOGDATE", "LOGHOUR"),
        zkUrl = Some("wbbigdata00,wbbigdata01,wbbigdata02:2181")
      )
    })

    ssc.start()
    ssc.awaitTermination()
  }

}
