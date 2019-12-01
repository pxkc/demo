package com.nowcoder.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author pxk
 * @date 2019/10/6 - 15:52
 * 面向所有服务的切面
 * 面向切面编程AOP技术就是为解决问题而诞生的。
 * 切面就是横切面，如图6-5所看到的，代表的是一个普遍存在的共同拥有功能，
 * 比如。日志切面、权限切面及事务切面等。
 */


@Aspect//定义一个切面。
@Component//定义一个Component，要不然不会被初始化@component把普通pojo实例化到spring容器中，相当于配置文件中的<bean id="" class=""/>
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.nowcoder.demo.controller.*Controller.*(..))")//对专门的controller的切面编程
    public void beforeMethod(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            sb.append("args:" + arg.toString() + "| ");
        }

        logger.info("before method:" + sb.toString());//打印出参数
        logger.info("before ,time" + new Date());//分析服务器的状态

    }

    @After("execution(* com.nowcoder.demo.controller.IndexController.*(..))")
    public void afterMethod(JoinPoint joinPoint) {
        logger.info("after method:");
    }
}