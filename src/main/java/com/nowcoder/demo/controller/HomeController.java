package com.nowcoder.demo.controller;

import com.nowcoder.demo.model.News;
import com.nowcoder.demo.model.ViewObject;
import com.nowcoder.demo.service.NewsService;
import com.nowcoder.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
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

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})//path或者value
//    @ResponseBody这个是返回字符串的意思
    public String index(Model model) {
        List<News> newsList = newsService.getLatesetNews(0,0,10);

        List<ViewObject> vos = new ArrayList<>();
        for(News news: newsList){
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("vos", vos);
        return "home";
    }

}