package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author:lixinan
 * @email:2489460735@qq.com
 * @desc:
 * @datetime: 2024/7/21 18:15
 */

@RestController
@RequestMapping("/admin/category")
@Api(tags = "菜品分类相关的接口")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    //新增菜品信息
    @PostMapping
    @ApiOperation("新增分类")
    public Result<String> save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类信息：{}",categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    //分页查询
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询：{}",categoryPageQueryDTO);
        PageResult pageResult =  categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    //根据ID删除信息，删除分类
    @ApiOperation("删除操作")
    @DeleteMapping
    public Result<String> deleteById(Long id){
        log.info("删除的分类：{}",id);
        categoryService.deleteById(id);
        return Result.success();
    }

    //修改分类
    @ApiOperation("修改分类")
    @PutMapping
    public Result<String> update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类：{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }


    //启用禁用分类
    @ApiOperation("启用禁用分类")
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable Integer status,Long id){
        log.info("启用禁用分页是：{},{}",status,id);
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    //根据类型查询分类
    @ApiOperation("根据类型查询分类")
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }



}
