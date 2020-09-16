package com.bigdata.dw.gmall.realtime.app

import java.util

import com.alibaba.fastjson.JSON
import com.bigdata.dw.gamll.common.Constant
import com.bigdata.dw.gmall.realtime.bean.{AlertInfo, EventLog}
import com.bigdata.dw.gmall.realtime.util.{ESUtil, MyKafkaUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}

/**
  * @ description: 预警信息
  * @ author: spencer
  * @ date: 2020/9/10 16:17
  */
object AlertApp {

  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setAppName("AlterApp").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    ssc.checkpoint("check_point")
    // 1.从kafka读取数据，并添加窗口。
    val rawStream: DStream[ConsumerRecord[String, String]] = MyKafkaUtil.getKafkaStream(ssc, Constant.EVENT_TOPIC)

    // 2.封装数据
    val eventLogStream: DStream[(String, EventLog)] = rawStream
      .map(_.value())
      .map {
        jsonString => {
          val eventLog: EventLog = JSON.parseObject(jsonString, classOf[EventLog])
          (eventLog.mid, eventLog)
        }
      }.window(Minutes(5))

    // 3.处理数据
    // 3.1按照mid分组
    val midEventLogGrouped: DStream[(String, Iterable[EventLog])] = eventLogStream.groupByKey()

//    midEventLogGrouped.print()
    // 3.2产生预警信息
    val alertInfoStream: DStream[(Boolean, AlertInfo)] = midEventLogGrouped.map {
      case (mid, logIt) =>
        // 在这最近的5min内，同一个设备上所发生的事件日志
        // 记录：a. 5min内当前设备有几个用户登录 b. 领取的优惠券的个数 c. 有没有点击(浏览商品)
        // 返回: 预警信息
        // 1.记录领取过优惠券的用户(使用java的set集合，scala的set集合存储到es中看不到数据)
        val uidSet: util.HashSet[String] = new util.HashSet[String]()
        // 2.记录5min内该设备的所有事件
        val eventList = new util.ArrayList[String]()
        // 3.记录领取过优惠券对应的商品的id
        val itemSet: util.HashSet[String] = new util.HashSet[String]()

        var isClickItem = false // 表示5min内没有点击商品的用户

        import scala.util.control.Breaks._
        breakable {
          logIt.foreach(
            log => {
              eventList.add(log.eventId) // 保存所有事件
              // 如果事件类型是优惠券，说明领取了优惠券，将该用户保存下来
              log.eventId match {
                case "coupon" =>
                  uidSet.add(log.uid) // 把领取优惠券的用户存储
                  itemSet.add(log.itemId) // 把优惠券对应的商品id存储
                case "clickItem" =>
                  isClickItem = true // 表示用户点击了商品
                  break
                case _ =>
              }
            }
          )
        }

        (uidSet.size() >= 2 && !isClickItem, AlertInfo(mid, uidSet, itemSet, eventList, System.currentTimeMillis()))

    }

//    alertInfoStream.filter(_._1 == false).print()
    import ESUtil._
//    alertInfoStream.filter(_._1).map(_._2).foreachRDD(rdd => {
/*      rdd.foreachPartition(
        alertInfoIt => {
          val result: Iterator[(AlertInfo, String)] = alertInfoIt.map(info => {
            (info, info.mid + "_" + info.ts / 1000 / 60)
          })
          ESUtil.insertBulk("gmall_coupon_alert", result)
        }
      )*/
//      rdd.saveToEs("gmall_coupon_alert")
//    })

    alertInfoStream.filter(_._1).map(_._2).foreachRDD(_.saveToEs("gmall_coupon_alert"))

    alertInfoStream.print(1000)

    ssc.start()
    ssc.awaitTermination()

  }
}
