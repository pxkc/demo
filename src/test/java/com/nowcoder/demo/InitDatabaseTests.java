
package com.nowcoder.demo;

import com.nowcoder.demo.dao.NewsDao;
import com.nowcoder.demo.dao.UserDao;
import com.nowcoder.demo.model.News;
import com.nowcoder.demo.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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
    @Test
    public void initData() {

        for(int i = 0; i < 11; i ++){
            User user = new User();

            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("User%d",i));
            user.setPassword("");
            user.setSalt("");
            userDao.addUser(user);

            News news = new News();
            news.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 *3600*5 * i);
            news.setCreateDate(date);
            news.setImage(String.format("http://images.nowcoder.com/head/%dm.png",random.nextInt(1000)));
            news.setLikeCount(i + 1);
            news.setUserId(i + 1);
            news.setTitle(String.format("Title{%d}", i ));
            news.setLink(String.format("https://www.nowcoder.com/%d.html", i));

            user.setPassword("newwpassword");
            userDao.updatePassword(user);
            newsDao.addNews(news);

        }
        Assert.assertEquals("newwpassword",userDao.selcetById(1).getPassword());

        userDao.deleteById(1);

        Assert.assertNull(userDao.selcetById(1));


    }

}