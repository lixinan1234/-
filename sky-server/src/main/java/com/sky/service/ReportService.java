package com.sky.service;

import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {


    //营业额统计接口
    TurnoverReportVO getTurnover(LocalDate begin, LocalDate end);


    //用户数据统计
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);
}
