package com.bigdata.dw.gmall.realtime.util

import java.lang

import org.apache.kafka.clients.consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import scala.collection.mutable

/**
  * @ description: 
  * @ author: spencer
  * @ date: 2020/9/3 15:19
  */
object MyKafkaUtil {
  def getKafkaStream(ssc: StreamingContext, topic: String, offsetMap: mutable.Map[TopicPartition, Long]) = {
    val params = Map(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> PropertyUtil.getProperty("kafka.broker.list"),
      ConsumerConfig.GROUP_ID_CONFIG -> PropertyUtil.getProperty("kafka.group"),
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "latest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (false: lang.Boolean)
      //      "auto.offset.reset" -> "latest",
      //      "enable.auto.commit" -> (false: lang.Boolean)
    )

    KafkaUtils.createDirectStream(
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Set(topic), params, offsetMap)
    )
  }


  /**
    * 从kafka读取数据
    *
    * @param ssc
    * @param topic
    * @return
    */
  def getKafkaStream(ssc: StreamingContext, topic: String) = {
    val params = Map(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> PropertyUtil.getProperty("kafka.broker.list"),
      ConsumerConfig.GROUP_ID_CONFIG -> PropertyUtil.getProperty("kafka.group"),
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "latest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (false: lang.Boolean)
//      "auto.offset.reset" -> "latest",
//      "enable.auto.commit" -> (false: lang.Boolean)
    )
    KafkaUtils.createDirectStream(
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Set(topic), params)
    )
  }
}
