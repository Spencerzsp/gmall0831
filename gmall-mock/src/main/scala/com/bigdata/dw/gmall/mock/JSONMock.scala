package com.bigdata.dw.gmall.mock

import java.util.Date

import com.alibaba.fastjson.{JSON, JSONObject}
import com.bigdata.dw.gmall.mock.util.{LogUploader, RandomNumUtil, RandomOptions}

/**
  * @ description: 模拟生成JSON数据
  * @ author: spencer
  * @ date: 2020/9/1 17:29
  */
object JSONMock {

  // 生成启动日志的记录数
  var startupNum: Int = 100000
  // 生成的事件日志的记录数
  var eventNum: Int = 200000

  // 操作系统的分布
  private val osOpts = RandomOptions(("ios", 3), ("android", 7), ("pc", 2))

  var startDate: Date = _
  var endDate: Date = _

  // 地理位置分布
  private val areaOpts = RandomOptions(
    ("beijing", 20), ("shanghai", 20), ("guangdong", 20),
    ("hebei", 5), ("heilongjiang", 5), ("shandong", 5),
    ("tianjin", 5), ("guizhou", 5), ("shanxi", 5),
    ("sichuan", 5), ("xinjiang", 5)
  )

  val appId: String = "gmall"

  // app的版本
  private val versionOpts = RandomOptions(
    ("1.2.0", 50), ("1.1.2", 15),
    ("1.1.3", 30), ("1.1.1", 5)
  )

  // 用户行为的分布
  private val eventOpts = RandomOptions(
    ("addFavor", 10), ("addComment", 30),
    ("addCart", 20), ("clickItem", 10), ("coupon", 90)
  )

  // app的渠道发布
  private val channelOpts = RandomOptions(
    ("xiaomi", 10), ("huawei", 20), ("wandoujia", 30),
    ("360", 20), ("tencent", 20), ("baidu", 10), ("website", 10)
  )

  // 生成模拟数据的时候是否结束退出
  private val quitOpts = RandomOptions((true, 5), (false, 95))

  /**
    * 模拟启动日志
    * @return
    */
  def initOneStartupLog() = {

    val mid: String = "mid_" + RandomNumUtil.randomInt(1, 10)
    val uid: String = RandomNumUtil.randomInt(1, 20) + ""
    val os: String = osOpts.getRandomOption()
    val appId: String = this.appId
    val area: String = areaOpts.getRandomOption()
    val version: String = versionOpts.getRandomOption()
    val channel: String = channelOpts.getRandomOption()

    val obj = new JSONObject()
    obj.put("logType", "startup")
    obj.put("mid", mid)
    obj.put("uid", uid)
    obj.put("os", os)
    obj.put("appId", appId)
    obj.put("area", area)
    obj.put("version", version)
    obj.put("channel", channel)

    // 返回json格式字符串
    obj.toJSONString
  }

  /**
    * 模拟事件日志
    * @param startupLogJson
    */
  def initOneEventLog(startupLogJson: String) = {

    val startupLogObj: JSONObject = JSON.parseObject(startupLogJson)
    val obj = new JSONObject()

    obj.put("logType", "event")
    obj.put("mid", startupLogObj.getString("mid"))
    obj.put("uid", startupLogObj.getString("uid"))
    obj.put("os", startupLogObj.getString("os"))
    obj.put("appId", this.appId)
    obj.put("area", startupLogObj.getString("area"))
    obj.put("eventId", eventOpts.getRandomOption())
    obj.put("pageId", RandomNumUtil.randomInt(1, 50))
    obj.put("nextPageId", RandomNumUtil.randomInt(1, 50))
    obj.put("itemId", RandomNumUtil.randomInt(1, 50))

    obj.toJSONString
  }

  /**
    * 生成日志
    */
  def generateLog() = {
    (0 to startupNum).foreach(_ => {
      val oneStartupLog: String = initOneStartupLog()
      println(oneStartupLog)
      LogUploader.sendLog(oneStartupLog)

      while (!quitOpts.getRandomOption()){
        val oneEventLog: String = initOneEventLog(oneStartupLog)
        LogUploader.sendLog(oneEventLog)
        println(oneEventLog)
        Thread.sleep(100)
      }
      Thread.sleep(1000)
    })
  }

  def main(args: Array[String]): Unit = {
    generateLog()
  }

}
