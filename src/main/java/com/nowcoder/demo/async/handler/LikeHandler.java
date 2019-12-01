package com.nowcoder.demo.async.handler;

import com.nowcoder.demo.async.EventHandler;
import com.nowcoder.demo.async.EventModel;
import com.nowcoder.demo.async.EventType;
import com.nowcoder.demo.model.Message;
import com.nowcoder.demo.model.User;
import com.nowcoder.demo.service.MessageService;
import com.nowcoder.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.resources.Messages;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author pxk
 * @date 2019/11/28 - 16:07
 */
@Component//通过控制反转，在Consumer中实现的Eventhandler对象，找到能处理的方法,少了这个component导致控制反转的时候没有生成相应的handler方法
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(3);
//        message.setToId(model.getEntityOwnerId());
        message.setToId(model.getActorId());
        message.setConversationId( String.format("%d_%d", model.getActorId(), 3));
        User user = userService.getUser(model.getActorId());
        message.setContent("用户" + user.getName() + "赞了你的咨询， http://127.0.0.1:8080/news/" + model.getEntityId());
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
        System.out.println("Liked");
    }

    @Override
    public List<EventType> getSupportEventTypes() {

        return Arrays.asList(EventType.LIKE);
    }
}