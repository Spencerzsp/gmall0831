package com.bigdata.dw.gmall.canal

import java.util

import com.alibaba.fastjson.JSONObject
import com.alibaba.otter.canal.protocol.CanalEntry
import com.alibaba.otter.canal.protocol.CanalEntry.EventType
import com.bigdata.dw.gamll.common.Constant

/**
  * @ description: 
  * @ author: spencer
  * @ date: 2020/9/8 15:00
  */
object CanalHandler {

  import scala.collection.JavaConversions._
  def handle(tableName: String, rowDatas: util.List[CanalEntry.RowData], eventType: CanalEntry.EventType) = {

    if ("order_info" == tableName && rowDatas != null && !rowDatas.isEmpty && eventType == EventType.INSERT)
      sendRowDataToKafka(rowDatas, Constant.ORDER_TOPIC)
    else if ("order_info" == tableName && rowDatas != null && !rowDatas.isEmpty && eventType == EventType.DELETE){
      for (rowData <- rowDatas) {
        val columns: util.List[CanalEntry.Column] = rowData.getBeforeColumnsList
        val jsonObj = new JSONObject()
        for (column <- columns) {
          val key: String = column.getName
          val value: String = column.getValue
          jsonObj.put(key, value)
        }
        println(jsonObj.toJSONString)
        //使用工具类，创建kafka生产者发送数据
        MyKafkaUtil.send(Constant.ORDER_TOPIC, jsonObj.toJSONString)
      }
    }
  }

  /**
    * 把数据发送到指定的topic
    * @param rowDatas
    * @param topic
    */
  private def sendRowDataToKafka(rowDatas: util.List[CanalEntry.RowData], topic: String) = {

    for (rowData <- rowDatas) {
      val columns: util.List[CanalEntry.Column] = rowData.getAfterColumnsList
      val jsonObj = new JSONObject()
      for (column <- columns) {
        val key: String = column.getName
        val value: String = column.getValue
        jsonObj.put(key, value)
      }
      println(jsonObj.toJSONString)
      //使用工具类，创建kafka生产者发送数据
      MyKafkaUtil.send(topic, jsonObj.toJSONString)
    }

  }
}
