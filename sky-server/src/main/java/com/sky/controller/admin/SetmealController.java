package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:lixinan
 * @email:2489460735@qq.com
 * @desc:
 * @datetime: 2024/8/2 16:49
 */
@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关的接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    //新增套餐
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增的套餐是：{}",setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }
}
