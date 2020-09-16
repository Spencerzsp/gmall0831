package com.bigdata.dw.service.imp;

import com.bigdata.dw.mapper.DauMapper;
import com.bigdata.dw.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ description:
 * @ author: spencer
 * @ date: 2020/9/8 9:48
 */
@Service
public class PublisherServiceImp implements PublisherService {

    @Autowired
    DauMapper dauMapper;

    @Override
    public Long getDau(String date) {
        return dauMapper.getDau(date);
    }

    @Override
    public Map<String, Long> getHourDau(String date) {
        List<Map> mapList = dauMapper.getHourDau(date);
        Map<String, Long> result = new HashMap<>();
        // 将mapList中的数据取出来，作为map中的key-value
        for (Map map : mapList) {
            String loghour = (String) map.get("LOGHOUR");
            Long count = (Long) map.get("COUNT");
            result.put(loghour, count);
        }
        return result;
    }

//    @Override
//    public List<Map> getHourDau(String date) {
//        return dauMapper.getHourDau(date);
//    }
}
