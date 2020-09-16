package com.bigdata.dw.gmall.realtime.bean

import java.text.SimpleDateFormat
import java.util.Date

/**
  * @ description: 
  * @ author: spencer
  * @ date: 2020/9/3 16:00
  *
  * {"logType":"startup","area":"shanghai","uid":"8406",
  * "os":"ios","appId":"gmall","channel":"xiaomi",
  * "mid":"mid_409","version":"1.1.2","ts":1599119948075}
  */

case class StartupLog(
                     mid: String,
                     uid: String,
                     appId: String,
                     area: String,
                     os: String,
                     channel: String,
                     logType: String,
                     version: String,
                     ts: Long,
                     var logDate: String = null,
                     var logHour: String = null
                     ){
  private val f1 = new SimpleDateFormat("yyyy-MM-dd")
  private val f2 = new SimpleDateFormat("HH")
  private val d = new Date(ts)
  logDate = f1.format(d)
  logHour = f2.format(d)
}
