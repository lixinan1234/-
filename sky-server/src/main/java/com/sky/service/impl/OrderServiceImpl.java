package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private UserMapper userMapper;




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

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
   public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
       // 当前登录用户id
       Long userId = BaseContext.getCurrentId();
       User user = userMapper.getById(userId);

       paySuccess(ordersPaymentDTO.getOrderNumber());

       String orderNumber = ordersPaymentDTO.getOrderNumber(); //订单号
       Long id = orderMapper.getorderId(orderNumber);//根据订单号查主键

       JSONObject jsonObject = new JSONObject();//本来没有2
       jsonObject.put("code", "ORDERPAID"); //本来没有3
       OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
       vo.setPackageStr(jsonObject.getString("package"));
       //为替代微信支付成功后的数据库订单状态更新，多定义一个方法进行修改
       Integer OrderPaidStatus = Orders.PAID; //支付状态，已支付
       Integer OrderStatus = Orders.TO_BE_CONFIRMED; //订单状态，待接单
       //发现没有将支付时间 check_out属性赋值，所以在这里更新
       LocalDateTime check_out_time = LocalDateTime.now();

       orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, id);
       return vo;  //  修改支付方法中的代码
   }

       /**
        * 支付成功，修改订单状态
        *
        * @param outTradeNo
        */
       public void paySuccess(String outTradeNo) {

           // 根据订单号查询订单
           Orders ordersDB = orderMapper.getByNumber(outTradeNo);

           // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
           Orders orders = Orders.builder()
                   .id(ordersDB.getId())
                   .status(Orders.TO_BE_CONFIRMED)
                   .payStatus(Orders.PAID)
                   .checkoutTime(LocalDateTime.now())
                   .build();

           orderMapper.update(orders);
       }


   }
