package com.nowcoder.demo.interceptor;

import com.nowcoder.demo.dao.LoginTicketDao;
import com.nowcoder.demo.dao.UserDao;
import com.nowcoder.demo.model.HostHolder;
import com.nowcoder.demo.model.LoginTicket;
import com.nowcoder.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author pxk
 * @date 2019/11/9 - 16:06
 * <p>
 * 每次访问之间拦截器插进来，找这个用户到底是谁，展现出来 postHandle
 */

@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTicketDao loginTicketDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private HostHolder hostHolder;

    @Override
    //如果返回false 直接没有。如果返回true，才有后面的处理
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if (ticket != null) {
            LoginTicket loginTicket = loginTicketDao.selectByTicket(ticket);
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
                return true;//直接过期，当什么没发生过
            }
            User user = userDao.selcetById(loginTicket.getUserId());//通过ticket和是否有效查用户
            hostHolder.setUser(user);//零时存在这个线程里面
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null) {
            modelAndView.addObject("user", hostHolder.getUser());//后端代码和前段用户交互，如$user.name   home.html中
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}