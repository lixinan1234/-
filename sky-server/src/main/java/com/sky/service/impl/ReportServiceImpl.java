package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:lixinan
 * @email:2489460735@qq.com
 * @desc:
 * @datetime: 2024/9/5 15:04
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    //营业额统计接口
    @Override
    public TurnoverReportVO getTurnover(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        //存放每天营业额
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);//把日期精确到时分秒
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);//把日期精确到时分秒

            //select sum(amount) from orders where order_time < beginTime and order_time > endTime and status = 5
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }


    //用户数据统计
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //存放每天的新增用户数量
        List<Integer> newUserList = new ArrayList<>();
        //存放每天的总用户数量
        List<Integer> totalUserList = new ArrayList<>();


        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);//把日期精确到时分秒
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);//把日期精确到时分秒
            //新增用户数量 select count(id) from orders where create_time > ? and create_time < ?
            Integer newUser = getUserCount(beginTime,endTime);
            //总用户数量 select count(id) from user where  create_time < ?
            Integer totalUser = getUserCount(null,endTime);

            newUserList.add(newUser);
            totalUserList.add(totalUser);

        }

        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }




    //根据时间区间统计用户数量
    private Integer getUserCount(LocalDateTime beginTime, LocalDateTime endTime) {
        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("end",endTime);
        return userMapper.countByMap(map);
    }

}
