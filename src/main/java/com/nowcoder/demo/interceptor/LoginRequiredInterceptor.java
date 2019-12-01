package com.nowcoder.demo.interceptor;

import com.nowcoder.demo.model.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author pxk
 * @date 2019/11/9 - 16:06
 * <p>
 * 每次访问之间拦截器插进来，找这个用户到底是谁，展现出来 postHandle
 */

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    //如果返回false 直接没有。如果返回true，才有后面的处理
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if (hostHolder.getUser() == null) {
            httpServletResponse.sendRedirect("/?pop=1");
            return false;//没有登录的用户的用户，直接拦住了，后面不继续了
        }
        return true;//没拦住，后面是否有继续的处理，看后面
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}