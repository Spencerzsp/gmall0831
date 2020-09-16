package com.bigdata.dw.gmall.realtime.util

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.internals.Topic
import org.apache.spark.streaming.kafka010.OffsetRange

import scala.collection.mutable

/**
  * @ description: 
  * @ author: spencer
  * @ date: 2020/9/9 10:38
  */
object OffsetUtil {

  /**
    * 保存偏移量至mysql
    * @param groupId
    * @param offsetRanges
    */
  def saveOffsetRanges(groupId: String, offsetRanges: Array[OffsetRange]) = {
    val connection: Connection = DriverManager
      .getConnection("jdbc:mysql://wbbigdata01:3306/gmall?characterEncoding=utf-8", "root", "bigdata")

    val sql = "replace into kafka_offset(group_id, topic, partition_id, fromOffset, untilOffset) values(?, ?, ?, ?, ?)"
    val pstmt: PreparedStatement = connection.prepareStatement(sql)

    for (o <- offsetRanges) {
      pstmt.setString(1, groupId)
      pstmt.setString(2, o.topic)
      pstmt.setInt(3, o.partition)
      pstmt.setLong(4, o.fromOffset)
      pstmt.setLong(5, o.untilOffset)
      pstmt.executeUpdate()
    }
    pstmt.close()
    connection.close()
  }

  /**
    * 获取mysql中的偏移量
    * @param topic
    * @param groupId
    * @return
    */
  def getOffsetMap(topic: String, groupId: String) = {

    // 1.获取mysql连接
    val connection: Connection = DriverManager.getConnection("jdbc:mysql://wbbigdata01:3306/gmall", "root", "bigdata")
    // 2.查询mysql中偏移量
    val sql = "select * from kafka_offset where group_id = ? and topic = ?"
    val psmt: PreparedStatement = connection.prepareStatement(sql)

    psmt.setString(1, groupId)
    psmt.setString(2, topic)

    val resultSet: ResultSet = psmt.executeQuery()
    val offsetMap = mutable.Map[TopicPartition, Long]()
    while (resultSet.next()){
      offsetMap += new TopicPartition(resultSet.getString("topic"), resultSet.getInt("partition_id")) -> resultSet.getLong("untilOffset")
    }

    resultSet.close()
    psmt.close()
    connection.close()

    offsetMap
  }

}
