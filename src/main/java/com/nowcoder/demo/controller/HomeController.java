package com.nowcoder.demo.controller;

import com.nowcoder.demo.model.EntityType;
import com.nowcoder.demo.model.HostHolder;
import com.nowcoder.demo.model.News;
import com.nowcoder.demo.model.ViewObject;
import com.nowcoder.demo.service.LikeService;
import com.nowcoder.demo.service.NewsService;
import com.nowcoder.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pxk
 * @date 2019/10/18 - 15:10
 */

@Controller
public class HomeController {
    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;

    @Autowired
    HostHolder hostHolder;//设置过了可以直接使用，传到这个位置，不是应该选择最新的十条，根据个人关注情况出个性化首页

    @Autowired
    LikeService likeService;
    private List<ViewObject> getNews(int userId, int offset, int limit) {
        List<News> newsList = newsService.getLatesetNews(userId, offset, limit);
        int localUserId = hostHolder.getUser()!=null? hostHolder.getUser().getId(): 0;
        List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            if (localUserId != 0) {
                vo.set("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                vo.set("like", 0);
            }
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})//path或者value
    public String index(Model model) {

        System.out.print(getNews(0, 0, 10).get(0).get("news"));//vos 是一个list.get(i)length 是10
        model.addAttribute("vos", getNews(0, 0, 10));
        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})//path或者value
//    根据userId筛选出信息
    public String userIndex(Model model, @PathVariable("userId") int userId,
                            @RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("vos", getNews(userId, 0, 10));
        model.addAttribute("pop", pop);//从前端解析出来的参数
        return "home";
    }
}