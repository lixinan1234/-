package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//自定义的注解，用于标识某个方法需要进行功能字段自动填充处理
@Target(ElementType.METHOD)//@Target：当前这个注解会加在什么位置。
                           //ElementType.METHOD：加在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {

    //数据库 操作类型：UPDATE INSERT
    OperationType value();
}
