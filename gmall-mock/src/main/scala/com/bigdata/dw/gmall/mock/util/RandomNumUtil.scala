package com.bigdata.dw.gmall.mock.util

import scala.collection.mutable
import scala.util.Random

/**
  * @ description: 生成随机数据的工具
  * @ author: spencer
  * @ date: 2020/9/1 17:30
  */
object RandomNumUtil {

  // 随机数生成器对象
  val random = new Random()

  /**
    * 生成随机的整数，区间【from， to】
    * @param from
    * @param to
    */
  def randomInt(from: Int, to: Int) = {
    if (from > to)
      throw new IllegalArgumentException(s"$from 不能大于 $to")
    else
      random.nextInt(to - from + 1) + from
  }

  /**
    *
    * @param from
    * @param to
    * @param count
    * @param canRepeat 是否允许重复，使用set自动去重
    * @return
    */
  def randomMultiInt(from: Int, to: Int, count: Int, canRepeat: Boolean = true) = {
    if (canRepeat){
      (1 to count).toList.map(_ => randomInt(from, to))
    } else{
      val set: mutable.Set[Int] = mutable.Set[Int]()
      while (set.size < count){
        set += randomInt(from, to)
      }
      set.toList
    }
  }

  /**
    *
    * @param from
    * @param to
    * @return
    */
  def randomLong(from: Long, to: Long) = {
    if (from > to)
      throw new IllegalArgumentException(s"$from 不能大于 $to")
    else
      math.abs(random.nextLong) % (to - from + 1) + from
  }
}
