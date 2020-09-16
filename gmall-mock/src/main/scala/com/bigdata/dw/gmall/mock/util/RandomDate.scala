package com.bigdata.dw.gmall.mock.util

import java.util.Date

/**
  * @ description: 随机生成日期
  * @ author: spencer
  * @ date: 2020/9/1 17:49
  */
object RandomDate {

  def apply(startDate: Date, stopDate: Date, step: Int) = {
    val randomDate = new RandomDate()
    val avgStepTime: Long = (stopDate.getTime - startDate.getTime) / step

    randomDate.maxStepTime = avgStepTime * 4
    randomDate.lastDateTime = startDate.getTime
    randomDate
  }

}

class RandomDate {

  var lastDateTime:Long = _

  var maxStepTime: Long = _

  /**
    * 生成随机的时间
    */
  def getRandomDate = {
    val timeStep: Long = RandomNumUtil.randomLong(0, maxStepTime)
    lastDateTime += timeStep

    new Date(lastDateTime)
  }
}
