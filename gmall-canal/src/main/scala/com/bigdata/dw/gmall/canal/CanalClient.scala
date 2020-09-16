package com.bigdata.dw.gmall.canal

import java.net.{InetSocketAddress, SocketAddress}
import java.util

import com.alibaba.otter.canal.client.{CanalConnector, CanalConnectors}
import com.alibaba.otter.canal.protocol.CanalEntry.{EntryType, RowChange}
import com.alibaba.otter.canal.protocol.{CanalEntry, Message}
import com.google.protobuf.ByteString

/**
  * @ description: 
  * @ author: spencer
  * @ date: 2020/9/8 14:23
  */
object CanalClient {

  def main(args: Array[String]): Unit = {

    // 创建一个canal的客户端连接器
    val address: SocketAddress = new InetSocketAddress("wbbigdata01", 11111)
    val connector: CanalConnector = CanalConnectors.newSingleConnector(address, "example", "", "")

    // 连接到canal服务器
    connector.connect()

    // 订阅数据，example实例中监控了很多数据
    connector.subscribe("gmall.*")

    // 可以把java的集合转换成scala的集合
    import scala.collection.JavaConversions._
    // 获取数据
    while (true) {
      val msg: Message = connector.get(100)
//      println(msg.getEntries)
      val entries: util.List[CanalEntry.Entry] = if (msg != null) msg.getEntries else null

      if (entries != null && !entries.isEmpty){
        for (entry <- entries) {
          if (entry != null && entry.getEntryType == EntryType.ROWDATA){
            // 一个sql，多行数据变化
            val storeValue: ByteString = entry.getStoreValue
            val rowChange: RowChange = RowChange.parseFrom(storeValue)
            val rowDatas: util.List[CanalEntry.RowData] = rowChange.getRowDatasList

            // 表名 每行数据 事件类型
            CanalHandler.handle(entry.getHeader.getTableName, rowDatas, rowChange.getEventType)
          }
        }
      } else {
        println("没有拉取到数据，2s之后继续")
        Thread.sleep(2000)
      }
    }

  }

}
