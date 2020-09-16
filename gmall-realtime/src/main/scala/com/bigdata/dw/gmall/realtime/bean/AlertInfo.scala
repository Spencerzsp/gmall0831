package com.bigdata.dw.gmall.realtime.bean

/**
  * @ description: 
  * @ author: spencer
  * @ date: 2020/9/11 9:16
  */
case class AlertInfo(
                    mid: String,
                    uids: java.util.HashSet[String],
                    itemIds: java.util.HashSet[String],
                    events: java.util.List[String],
                    ts: Long
                    )