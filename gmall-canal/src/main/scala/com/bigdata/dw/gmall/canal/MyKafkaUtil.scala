package com.bigdata.dw.gmall.canal

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * @ description: 
  * @ author: spencer
  * @ date: 2020/9/9 14:09
  */
object MyKafkaUtil {

  private val props = new Properties()
  props.put("bootstrap.servers", "wbbigdata00:9092,wbbigdata01:9092,wbbigdata02:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer = new KafkaProducer[String, String](props)
  def send(topic: String, content: String) = {
    producer.send(new ProducerRecord[String, String](topic, content))
  }

}
























