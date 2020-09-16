package com.bigdata.dw.gmall.realtime.util

import java.io.InputStream
import java.util.Properties

/**
  * @ description: 
  * @ author: spencer
  * @ date: 2020/9/3 15:05
  */
object PropertyUtil {

  // 使用类加载器加载配置文件
  private val is: InputStream = PropertyUtil.getClass.getClassLoader.getResourceAsStream("config.properties")
  private val properties = new Properties()
  properties.load(is)

  def getProperty(propertyName: String) = {
    properties.getProperty(propertyName)
  }

  def main(args: Array[String]): Unit = {
    val broker: String = PropertyUtil.getProperty("kafka.broker.list")
    println(broker)
    println(PropertyUtil.getProperty("redis.host"))
  }

}
