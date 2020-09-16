package com.bigdata.dw.gmall.realtime.util

import com.bigdata.dw.gmall.realtime.bean.AlertInfo
import io.searchbox.client.config.HttpClientConfig
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.core.{Bulk, Index}
import org.apache.spark.rdd.RDD

/**
  * @ description: 
  * @ author: spencer
  * @ date: 2020/9/14 15:31
  */
object ESUtil {

  val esUrl = "http://wbbigdata00:9200"
//  val esUrl = "http://dafa1:60010"
  // 1.创建客户端连接工厂
  val factory = new JestClientFactory
  val conf: HttpClientConfig = new HttpClientConfig.Builder(esUrl)
    .maxTotalConnection(100)
    .connTimeout(1000 * 100)
    .readTimeout(1000 * 100)
    .multiThreaded(true)
    .build()
  factory.setHttpClientConfig(conf)

  def getClient = factory.getObject

  def main(args: Array[String]): Unit = {

    // 2.得到一个es的客户端
//    val client: JestClient = factory.getObject

    // 3.插入数据
    // 3.1数据源：json格式
//    val source =
//      """
//        |{
//        |  "name": "张一天"
//        |}
//      """.stripMargin
    // 3.2数据源：样例类
//    val source = User(10, "张一天")
//    val action: Index = new Index.Builder(source)
//      .index("user")
//      .`type`("_doc")
//      .id("1")
//      .build()
//    client.execute(action)

//    val source =
//      """
//        |{
//        | "name": "zhangsan",
//        | "age": 20
//        |}
//      """.stripMargin
//    insertSingle(source, "user", "2")

/*    insertBulk("user", List((User(10, "张三"), "1"), (User(20, "李四"), "2")))
    insertBulk("user", List((User(10, "张三")), (User(20, "李四"))))*/

  }

  /**
    * 向es中插入单条数据
    * @param source
    * @param index
    * @param id
    * @return
    */
  def insertSingle(source: Any, index: String, id: String = null) = {
    val client: JestClient = getClient
    val action: Index = new Index.Builder(source)
      .index(index)
      .`type`("_doc")
      .id(id)
      .build()
    client.execute(action)

    client.shutdownClient()
  }

  /**
    * 向es中批量插入数据
    * @param index
    * @param sources
    */
  def insertBulk(index: String, sources: Iterator[Any]) = {
    val client: JestClient = getClient
    val bulkBuilder: Bulk.Builder = new Bulk.Builder()
      .defaultIndex(index)
      .defaultType("_doc")

    sources.foreach {
      case (s, id: String) =>
        val action: Index = new Index.Builder(s).id(id).build()
        bulkBuilder.addAction(action)
      case (s) =>
        val action: Index = new Index.Builder(s).build()
        bulkBuilder.addAction(action)
      case _ =>
    }

    client.execute(bulkBuilder.build())
    client.shutdownClient()
  }
  case class User(age: Int, name: String)

  implicit class ESFunction(rdd: RDD[AlertInfo]){

    def saveToEs(indexName: String) = {
      rdd.foreachPartition(
        alertInfoIt => {
          val result: Iterator[(AlertInfo, String)] = alertInfoIt.map(info => {
            (info, info.mid + "_" + info.ts / 1000 / 60)
          })
          ESUtil.insertBulk("gmall_coupon_alert", result)
        }
      )
    }
  }
}

