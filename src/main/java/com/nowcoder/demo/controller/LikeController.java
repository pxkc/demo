package com.nowcoder.demo.controller;

import com.nowcoder.demo.async.EventModel;
import com.nowcoder.demo.async.EventProducer;
import com.nowcoder.demo.async.EventType;
import com.nowcoder.demo.model.EntityType;
import com.nowcoder.demo.model.HostHolder;
import com.nowcoder.demo.model.News;
import com.nowcoder.demo.service.LikeService;
import com.nowcoder.demo.service.NewsService;
import com.nowcoder.demo.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author pxk
 * @date 2019/11/23 - 10:15
 */
@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("newsId") int newsId) {
        int userId = hostHolder.getUser().getId();
        System.out.println("userId:" + userId);
        long likeCount = likeService.like(userId, EntityType.ENTITY_NEWS, newsId);

        News news = newsService.getById(newsId);
        newsService.updateLikeCount(newsId, (int) likeCount);

        eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId())
                .setEntityId(newsId).setEntityType(EntityType.ENTITY_NEWS).setEntityOwnerId(news.getUserId()));

        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/disLike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String disLike(@RequestParam("newsId") int newsId) {
        int userId = hostHolder.getUser().getId();
        long likeCount = likeService.disLike(userId, EntityType.ENTITY_NEWS, newsId);

        newsService.updateLikeCount(newsId, (int) likeCount);
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }
}