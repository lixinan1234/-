package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:lixinan
 * @email:2489460735@qq.com
 * @desc:
 * @datetime: 2024/8/27 15:58
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;


    //用户下单
    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        //处理各种业务异常（地址薄为空，购物车数据为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            //抛出异常
            throw new SetmealEnableFailedException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //查看当前购物车数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart =  new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if(shoppingCartList == null || shoppingCartList.size() == 0){
            throw new SetmealEnableFailedException(MessageConstant.SHOPPING_CART_IS_NULL);
        }


        //向订单表插入1条数据
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,order);//对象属性拷贝
        order.setOrderTime(LocalDateTime.now());//设置下单时间
        order.setPayStatus(Orders.UN_PAID);//设置支付状态
        order.setStatus(Orders.PENDING_PAYMENT);//设置订单状态
        order.setNumber(String.valueOf(System.currentTimeMillis()));//转字符串，然后设置时间戳
        order.setPhone(addressBook.getPhone());//获取并插入手机号
        order.setConsignee(addressBook.getConsignee());//获取并插入收货人
        order.setAddress(addressBook.getDetail());
        order.setUserId(userId);//设置用户ID
        orderMapper.insert(order);//插入数据


        List<OrderDetail> orderDetailList = new ArrayList<>();
        //向订单明细表插入n条数据
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();//订单明细
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(order.getId());//设置当前订单明细关联的订单ID
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);

        //清空当前用户购物车数据
        shoppingCartMapper.deleteByUserId(userId);

        //封装VO返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderTime(order.getOrderTime())
                .orderAmount(order.getAmount())
                .build();

        return orderSubmitVO;
    }
}
