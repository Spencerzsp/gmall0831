package com.bigdata.dw.service;

import java.util.List;
import java.util.Map;

/**
 * @ description:
 * @ author: spencer
 * @ date: 2020/9/8 9:46
 */
public interface PublisherService {

    // 获取日活的接口
    Long getDau(String date);

//    List<Map> getHourDau(String date);
    // 获取日活明细
    Map<String, Long> getHourDau(String date);
}
