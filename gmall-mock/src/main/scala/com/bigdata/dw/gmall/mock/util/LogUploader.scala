package com.bigdata.dw.gmall.mock.util

import java.io.OutputStream
import java.net.{HttpURLConnection, URL}

/**
  * @ description: 把数据发送到后台服务器,通过nginx发送到三台springboot服务器
  * @ author: spencer
  * @ date: 2020/9/1 21:55
  */
object LogUploader {

  def sendLog(log: String) = {
//    System.setProperty("HADOOP_USER_NAME", "root")
    try {
      // 日志服务器地址
//      val logUrl = new URL("http://localhost:5901/log")
      val logUrl = new URL("http://wbbigdata01:80/log")

      // 获取HttpURLConnection对象
      val connection: HttpURLConnection = logUrl.openConnection().asInstanceOf[HttpURLConnection]

      // 设置请求方法(上传数据一般使用POST请求)
      connection.setRequestMethod("POST")
      connection.setRequestProperty("clientTime", System.currentTimeMillis() + "")

      // 允许上传数据
      connection.setDoOutput(true)
      // 设置请求头信息,POST请求必须设置成这样
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
      // 获取上传数据的输出流
      val outputStream: OutputStream = connection.getOutputStream
      // 写出数据
      outputStream.write(("log=" + log).getBytes())

      outputStream.flush()

      //关闭资源
      outputStream.close()
      // 获取响应码，否则不会发送请求到服务器
      val responseCode: Int = connection.getResponseCode
      println(responseCode)

    } catch {
      case e => e.printStackTrace()
    }
  }
}
