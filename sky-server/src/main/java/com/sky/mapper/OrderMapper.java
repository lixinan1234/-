package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    //向订单表插入一条数据
    void insert(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


    /**

     * 获取orderid的mapper层方法，写在OrderMapper.java文件下

     * @param orderNumber

     * @return

     */

    @Select("select * from orders where number=#{orderNumber}")
    Long getorderId(String orderNumber);



    /**

     * 用于替换微信支付更新数据库状态的问题
     * @param orderStatus
     * @param orderPaidStatus
     */
    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where id = #{id}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);


    //分页条件查询并按下单时间排序
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);


    //根据id查询订单
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    //根据状态统计订单数量
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    //跟据订单状态和下单时间查询订单
    //select * from order where status = ? and order_time < (当前时间 - 15分钟)
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status,LocalDateTime orderTime);

    //跟据动态条件统计营业额数据
    Double sumByMap(Map map);

    //根据动态条件统计订单数量
    Integer countByMap(Map map);


    //统计指定时间区间内的销量排名前十
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin,LocalDateTime end);



}
