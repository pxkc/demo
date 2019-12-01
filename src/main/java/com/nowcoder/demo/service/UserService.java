package com.nowcoder.demo.service;

import com.nowcoder.demo.dao.LoginTicketDao;
import com.nowcoder.demo.dao.UserDao;
import com.nowcoder.demo.model.LoginTicket;
import com.nowcoder.demo.model.User;
import com.nowcoder.demo.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author pxk
 * @date 2019/10/18 - 14:14
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }

    @Autowired
    private LoginTicketDao loginTicketDao;

    //因为要返回验证信息给register的结果，用一个map返回数据
    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("password", "密码不能为空");
            return map;
        }

        User user = userDao.selcetByName(username);
        if (user != null) {
            map.put("msgname", "用户名已经被占用");
            return map;
        }

        //密码强度
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));//或者random = new Random()
        user.setPassword(ToutiaoUtil.MD5(password + user.getSalt()));
        userDao.addUser(user);

        String ticket = addLoginTicket(user.getId());//登录上面之后，知道了userid
        map.put("ticket", ticket);
        return map;
    }

    public User getUser(int id) {
        return userDao.selcetById(id);
    }


    public void addUser(User user) {
        userDao.addUser(user);
    }

    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
        }

        if (StringUtils.isBlank(password)) {
            map.put("password", "密码不能为空");
            return map;
        }

        User user = userDao.selcetByName(username);
        if (user == null) {
            map.put("msgname", "用户名不存在");
            return map;
        }
        if (!ToutiaoUtil.MD5(password + user.getSalt()).equals(user.getPassword())) {//不等于服务器存储的密码
            map.put("msgpassword", "密码不正确");
            return map;
        }

        map.put("userId", user.getId());

        //下发ticket
        //字段，id,user_id,ticket,expired,status
        String ticket = addLoginTicket(user.getId());//登录上面之后，知道了userid
        map.put("ticket", ticket);
        return map;
    }

    /**
     * 对应上，userid对应ticke
     *
     * @param userId
     * @return
     */
    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 24);//毫秒数，24小时的有效期
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDao.addTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket) {
        loginTicketDao.updateStatus(ticket, 1);//过期
    }
}