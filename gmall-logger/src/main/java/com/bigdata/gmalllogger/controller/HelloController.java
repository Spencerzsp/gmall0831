package com.bigdata.gmalllogger.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ description:
 * @ author: spencer
 * @ date: 2020/9/3 9:52
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(){
        System.out.println("服务器收到消息: hello world!");
        return "hello world!";
    }
}
