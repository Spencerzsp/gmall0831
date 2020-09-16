package com.bigdata.dw.gmall.realtime.util

import java.util

import redis.clients.jedis.{JedisPool, JedisPoolConfig, JedisShardInfo, ShardedJedisPool}

/**
  * @ description: 
  * @ author: spencer
  * @ date: 2020/9/3 16:31
  */
object RedisUtil {

  private val host: String = PropertyUtil.getProperty("redis.host")
  private val port: Int = PropertyUtil.getProperty("redis.port").toInt
  private val passwod: String = PropertyUtil.getProperty("redis.password")

  private val jedisPoolConfig = new JedisPoolConfig()
  jedisPoolConfig.setMaxTotal(100)
  jedisPoolConfig.setMaxIdle(40)
  jedisPoolConfig.setMinIdle(10)
  jedisPoolConfig.setBlockWhenExhausted(true)
  jedisPoolConfig.setMaxWaitMillis(1000 * 60 * 2)
  jedisPoolConfig.setTestOnBorrow(true)
  jedisPoolConfig.setTestOnReturn(true)

  jedisPoolConfig

  private val jedisPool = new JedisPool(jedisPoolConfig, host, port)

//  private val jedisShardInfo = new JedisShardInfo(host, port)
//  jedisShardInfo.setPassword(passwod)
//
//  import redis.clients.jedis.JedisShardInfo
//
//  val list = new util.LinkedList
//  list.add(jedisShardInfo)
//
//  new ShardedJedisPool(jedisPoolConfig, list)

  /**
    * 获取一个redis连接
    * @return
    */
  def getJedisClient() = {
    jedisPool.getResource
  }

}
