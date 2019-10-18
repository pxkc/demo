package com.nowcoder.demo.service;

import com.nowcoder.demo.dao.NewsDao;
import com.nowcoder.demo.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author pxk
 * @date 2019/10/18 - 15:11
 */
@Service
public class NewsService {
    @Autowired
    private NewsDao newsDao;

    public List<News> getLatesetNews(int userId, int offset, int limit){
        return newsDao.selectByUserIdAndOffset(userId, offset, limit);

    }
}