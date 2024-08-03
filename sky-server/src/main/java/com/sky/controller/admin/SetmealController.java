package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    @ApiOperation("新增套餐接口")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增的套餐是：{}",setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    //分页查询
    @GetMapping("/page")
    @ApiOperation("分页查询接口")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询：{}",setmealPageQueryDTO);
        PageResult pageResult =  setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }


}
