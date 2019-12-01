package com.nowcoder.demo.controller;

import antlr.debug.MessageAdapter;
import com.nowcoder.demo.dao.MessageDao;
import com.nowcoder.demo.model.*;
import com.nowcoder.demo.service.MessageService;
import com.nowcoder.demo.service.UserService;
import com.nowcoder.demo.util.ToutiaoUtil;
import javafx.scene.media.VideoTrack;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;

/**
 * @author pxk
 * @date 2019/11/20 - 21:49
 */
@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = {"msg/list"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model){//是不是要求比较浅，没有model带进来也行，待验证
        try {
            int localUserId = hostHolder.getUser().getId();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            for(Message msg: conversationList){
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId(): msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("target", user);//vo里面放两个实体
                vo.set("unread", messageService.getConversationUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }


    @RequestMapping(path = {"msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model, @Param("converstationId") String conversationId){//是不是要求比较浅，没有model带进来也行，待验证
        try {
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message msg : conversationList) { //定义一个message要加上类
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if(user == null) { //如果是null就continue
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                messages.add(vo);
            }
            model.addAttribute("messages", messages);

        } catch (Exception e) {
            logger.error("获取详情消息失败" + e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content) {
        try {
            Message msg = new Message();
            msg.setContent(content);
            msg.setFromId(fromId);
            msg.setToId(toId);
            msg.setCreatedDate(new Date());
            msg.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId): String.format("%d_%d", toId, fromId));
            messageService.addMessage(msg);//messageService的对象是 messageDao类new 的了
            //看直接报错，在这个地方debug有个错误，是数据库中忘建表了
            return ToutiaoUtil.getJSONString(msg.getId());
        } catch (Exception e) {
            logger.error("增加留言失败", e.getMessage());
            return ToutiaoUtil.getJSONString(1, "插入评论失败");
        }
    }
}