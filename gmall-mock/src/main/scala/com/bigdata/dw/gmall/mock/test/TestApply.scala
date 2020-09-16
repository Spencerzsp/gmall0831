package com.bigdata.dw.gmall.mock.test

/**
  * @ description: 
  * @ author: spencer
  * @ date: 2020/9/3 11:46
  */
object TestApply {

  def apply[T](opts: (T, Int)*) = {
    val testApply = new TestApply[T]
    testApply
  }

  class TestApply[T]{
    var first: String = _
    var second: Int = _

    def test() = {
      first = TestApply((first, second)).first
      second = TestApply((first, second)).second
      println(first + second)
    }
  }

}
