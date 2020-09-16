package com.bigdata.dw.controller;

import com.alibaba.fastjson.JSON;
import com.bigdata.dw.service.PublisherService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ description:
 * @ author: spencer
 * @ date: 2020/9/8 9:52
 */
@RestController
public class PublisherController {

    @Autowired
    PublisherService publisherService;

    /**
     * http://localhost:8070/realtime-total?date=2020-09-07
     * [{"id":"dau", "name":"新增日活","value":1200},
     * {"id":"new_mid","name":"新增设备","value":233}
     * ]
     * 想得到json数组，可以在代码中创建一个java的数组(集合)，然后通过fastjson直接转换
     */
    @GetMapping("/realtime-total")
    public String getDau(@RequestParam("date") String date){

        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();
        map1.put("id", "dau");
        map1.put("name", "新增日活");
        map1.put("value", publisherService.getDau(date) + "");
        result.add(map1);

        map2.put("id", "new_mid");
        map2.put("name", "新增设备");
        map2.put("value", "233");
        result.add(map2);

        return JSON.toJSONString(result);
    }

//    @GetMapping("/realtime-hour")
//    public List<Map> getHourDau(@RequestParam("date") String date){
//        return publisherService.getHourDau(date);
//    }

    /**
     * http://localhost:8070/realtime-hour?id=dau&date=2020-09-07
     * {"yesterday":{"11":383, "12":123},
     *  "today":{"12":38, "13":1233}}
     * @param id
     * @param date
     * @return
     */

    @GetMapping("/realtime-hour")
    public String getHourDau(
            @RequestParam("id") String id,
            @RequestParam("date") String date){
        if ("dau".equals(id)){
            Map<String, Long> today = publisherService.getHourDau(date);
            Map<String, Long> yesterDay = publisherService.getHourDau(getYesterday(date));

            Map<String, Map> result = new HashMap<>();
            result.put("yesterday", yesterDay);
            result.put("today", today);

            return JSON.toJSONString(result);
        } else if ("".equals(id)){

        }
        return null;

    }

    /**
     * 计算昨天的年月日
     * @param date
     * @return
     */
    private String getYesterday(String date) {
        return LocalDate.parse(date).minusDays(1).toString();
    }
}
