package com.nowcoder.demo;

import com.nowcoder.demo.dao.CommentDao;
import com.nowcoder.demo.dao.LoginTicketDao;
import com.nowcoder.demo.dao.NewsDao;
import com.nowcoder.demo.dao.UserDao;
import com.nowcoder.demo.model.*;
import com.nowcoder.demo.util.JedisApdater;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)

public class JedisTests {

    @Autowired
    JedisApdater jedisApdater;

    @Test
    public void testJedis() {
        jedisApdater.set("hello", "world");
        Assert.assertEquals("world", jedisApdater.get("hello"));
    }

    @Test
    public void testObject() {
        User user = new User();
        user.setHeadUrl("http://image/nowcoder.com/head/100t.png");
        user.setName("user1");
        user.setPassword("pwd");
        user.setSalt("salt");
        jedisApdater.setObject("user1XX", user);// 存到redis里面

        User u = jedisApdater.getObject("user1XX", User.class);// 反序列化的函数
        System.out.println(ToStringBuilder.reflectionToString(u));
    }

}