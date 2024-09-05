package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService {


    //营业额统计接口
    TurnoverReportVO getTurnover(LocalDate begin, LocalDate end);
}
