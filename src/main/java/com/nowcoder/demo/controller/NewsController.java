package com.nowcoder.demo.controller;

import com.nowcoder.demo.model.*;
import com.nowcoder.demo.service.*;
import com.nowcoder.demo.util.ToutiaoUtil;
import com.sun.tools.javac.comp.Todo;
import org.apache.catalina.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author pxk
 * @date 2019/11/10 - 22:23
 */
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    NewsService newsService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        // requestparam 是前端带进来的参数直接访问应该也可以带景来参数，pathvariable是路径中的变量
        //这里面的参数都是前端传进来的，
        //<input type="hidden" name="newsId" value="$!{news.id}"/>
        //<textarea rows="5" class="text required comment-content form-control" name="content" id="content"></textarea>
        try {
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);

            //更新news 里面的评论数量,// 变成异步之后会是什么样，
            int count = commentService.getCommentsCount(comment.getEntityId(), comment.getEntityType());
            //到下面这一行的时候，到exception里面了
            newsService.updateCommentCount(comment.getEntityId(), count);
        } catch (Exception e) {
            logger.error("增加评论失败", e.getMessage());
        }
        return "redirect:/news/" + String.valueOf(newsId);//刷新到当前页面
    }


    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model){
        News news = newsService.getById(newsId);

        if(news != null){

            int localUserId = hostHolder.getUser()!=null? hostHolder.getUser().getId(): 0;
            if (localUserId != 0) {
                model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                model.addAttribute("like", 0);
            }

            //多条评论
            List<Comment> comments = commentService.getCommentsByEntity(newsId, EntityType.ENTITY_NEWS);
            List<ViewObject> commentVos = new ArrayList<ViewObject>();
            for(Comment comment: comments){
                ViewObject vo = new ViewObject();
                vo.set("comment", comment);
                vo.set("user", userService.getUser(comment.getUserId()));
                commentVos.add(vo);
            }
            model.addAttribute("comments", commentVos);
        }
        model.addAttribute("news", news);//写一步调一步
        model.addAttribute("owner", userService.getUser(news.getUserId()));
        //model.addAttribute当做类传到页面
        return "detail";
    }

    @RequestMapping(path = {"/image"}, method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName,
                         HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR
                    + imageName)), response.getOutputStream());//图片要放到静态服务器中，后端分离
        } catch (Exception e) {
            logger.error("读取文件错误" + e.getMessage());
        }

    }



    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})//post才有post data
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link) {

        try {
            News news = new News();
            if(hostHolder.getUser() != null){
                news.setUserId(hostHolder.getUser().getId());
            } else {
                // 匿名
                news.setUserId(3);
            }
            news.setImage(image);
            news.setTitle(title);
            news.setLink(link);
            news.setCreateDate(new Date());
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("添加咨询错误" + e.getMessage());
            return ToutiaoUtil.getJSONString(1,"发布失败");
        }

    }


    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})//post才有post data
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
//            String fileUrl = newsService.saveImage(file);
            String fileUrl =  qiniuService.saveImage(file);
            if (fileUrl == null) {
                return ToutiaoUtil.getJSONString(1, "上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        } catch (Exception e) {
            logger.error("上传图片失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传失败");
        }
    }
}