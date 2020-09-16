package com.bigdata.gmalllogger.controller;

import com.alibaba.fastjson.JSONObject;
import com.bigdata.dw.gamll.common.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @ description:
 * @ author: spencer
 * @ date: 2020/9/1 16:41
 */
@RestController
public class LoggerController {

    Logger logger = LoggerFactory.getLogger(LoggerController.class);

    @PostMapping("/log")
    public void getLogger(@RequestParam("log") String log){

        // 1.给日志添加时间戳，返回新的日志
        log = addTs(log);
        System.out.println(log);

        // 2.将日志落盘
        save2File(log);

        // 3.将数据写入kafka
        save2Kafka(log);

    }

    @GetMapping("/test")
    public String testConnect() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        String hostName = address.getHostName();
        System.out.println("connected " + hostName + " success!");

        return "connected " + hostName + " success!";
    }

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;


    /**
     * 把日志写到kafka
     * @param log
     */
    private void save2Kafka(String log) {
        if (log.contains("startup")){
            kafkaTemplate.send(Constant.STARTUP_TOPIC, log);
        } else {
            kafkaTemplate.send(Constant.EVENT_TOPIC, log);
        }
    }

    /**
     * 把日志写到本地文件
     * @param log
     */
    private void save2File(String log) {
        logger.info(log);
    }

    /**
     * 给日志添加时间戳
     * @param log
     * @return
     */
    private String addTs(String log) {
        JSONObject jsonObject = JSONObject.parseObject(log);
        long ts = System.currentTimeMillis();
        jsonObject.put("ts", ts);
        log = jsonObject.toJSONString();
        return log;
    }
}
