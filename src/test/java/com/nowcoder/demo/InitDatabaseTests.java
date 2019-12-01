package com.nowcoder.demo;

import com.nowcoder.demo.dao.CommentDao;
import com.nowcoder.demo.dao.LoginTicketDao;
import com.nowcoder.demo.dao.NewsDao;
import com.nowcoder.demo.dao.UserDao;
import com.nowcoder.demo.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.swing.text.html.parser.Entity;
import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {


    /////通过注解的方式直接操作数据库 @Mapper在Dao中，将公共的字段提取
    @Autowired
    UserDao userDao;
    Random random = new Random();

    @Autowired
    NewsDao newsDao;

    @Autowired
    LoginTicketDao loginTicketDao;

    @Autowired
    CommentDao commentDao;


    @Test
    public void initData() {

        for (int i = 0; i < 11; i++) {
            User user = new User();

            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("User%d", i));
            user.setPassword("");
            user.setSalt("");
            userDao.addUser(user);

            News news = new News();
            news.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
            news.setCreateDate(date);
            news.setImage(String.format("http://images.nowcoder.com/head/%dm.png", random.nextInt(1000)));
            news.setLikeCount(i + 1);
            news.setUserId(i + 1);
            news.setTitle(String.format("Title{%d}", i));
            news.setLink(String.format("https://www.nowcoder.com/%d.html", i));

            user.setPassword("newwpassword");
            userDao.updatePassword(user);
            newsDao.addNews(news);

            for(int j = 0; j < 3; j ++) {
                Comment comment =  new Comment();
                comment.setUserId(i + 1);
                comment.setEntityId(news.getId());
                comment.setEntityType(EntityType.ENTITY_NEWS);// 评论资讯是1，评论一条评论加2，
                comment.setCreatedDate(new Date());
                comment.setStatus(0);
                comment.setContent("Comment " +  String.valueOf(j));
                commentDao.addComment(comment);
            }

            LoginTicket ticket = new LoginTicket();
            ticket.setStatus(0);
            ticket.setExpired(date);
            ticket.setId(i + 1);
            ticket.setUserId(i + 1);
            ticket.setTicket(String.format("TICKET%d", i + 1));

            loginTicketDao.addTicket(ticket);
            loginTicketDao.updateStatus(ticket.getTicket(), 2);
        }
        Assert.assertEquals("newwpassword", userDao.selcetById(1).getPassword());

        userDao.deleteById(1);

        Assert.assertNull(userDao.selcetById(1));

        Assert.assertEquals(1, loginTicketDao.selectByTicket("TICKET1").getUserId());
        //验证expected，第一个参数和第二个参数是否一致，操作是不是对，一边写一边单元测试
        //测试数据是否过，过的话，idea会推送说过了，验证测试是成功。
        Assert.assertEquals(2, loginTicketDao.selectByTicket("TICKET1").getStatus());
        System.out.println(commentDao.selectByEntity(1, 1));
        Assert.assertNotNull(commentDao.selectByEntity(1, EntityType.ENTITY_NEWS));
//        Assert.assertNotNull(commentDao.selectByEntity(1,EntityType));
    }

}