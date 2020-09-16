package com.bigdata.dw.gmall.mock.util

import scala.collection.mutable.ListBuffer

/**
  * @ description: 根据提供的值和比重，创建RandomOptions对象
  * @ author: spencer
  * @ date: 2020/9/1 18:00
  */
object RandomOptions {

  def apply[T](opts: (T, Int)*) = {
    val randmonOptions = new RandomOptions[T]()
    randmonOptions.totalWeight = (0 /: opts) (_ + _._2) //计算出的总的比重
    opts.foreach{
      case (value, weight) => randmonOptions.options ++= (1 to weight).map(_ => value)
    }
    randmonOptions
  }

  class RandomOptions[T] {
    var totalWeight: Int = _
    var options = ListBuffer[T]()

    /**
      * 获取随机的option值
      * @return
      */
    def getRandomOption() = {
      options(RandomNumUtil.randomInt(0, totalWeight - 1))
    }
  }

}
