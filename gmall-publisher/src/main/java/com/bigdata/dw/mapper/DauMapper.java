package com.bigdata.dw.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @ description:
 * @ author: spencer
 * @ date: 2020/9/8 9:08
 */
@Mapper
public interface DauMapper {

    // 获取日活
    Long getDau(String date);

    // 获取日活明细
    List<Map> getHourDau(String date);
}
