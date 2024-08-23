package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author:lixinan
 * @email:2489460735@qq.com
 * @desc:
 * @datetime: 2024/8/21 17:19
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    //添加购物车
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断当前加入到购物车中的商品是否已经存在
        ShoppingCart shoppingCaet = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCaet);
        Long userId = BaseContext.getCurrentId();
        shoppingCaet.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCaet);

        //如果已经存在了，只需要将数量加一
        if(list != null && list.size() >0){
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() +1);
            shoppingCartMapper.updateNumberById(cart);
        }else{
            //如果不存在，需要插入一条购物车数据

            //判断本次添加购物车的是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            if(dishId != null){
                //本次添加到购物车的是菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCaet.setName(dish.getName());
                shoppingCaet.setImage(dish.getImage());
                shoppingCaet.setAmount(dish.getPrice());
            }else{
                //本次添加到购物车的是套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);

                shoppingCaet.setName(setmeal.getName());
                shoppingCaet.setImage(setmeal.getImage());
                shoppingCaet.setAmount(setmeal.getPrice());
            }
            shoppingCaet.setNumber(1);
            shoppingCaet.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCaet);
        }
    }


    //查看购物车
    @Override
    public List<ShoppingCart> showShoppingCart() {

        Long userId = BaseContext.getCurrentId();//获取当前微信用户id
        ShoppingCart shoppingCart = ShoppingCart
                .builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list =shoppingCartMapper.list(shoppingCart);
        return list;
    }

    //清空购物车
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }
}
