package com.nowcoder.demo.async.handler;

import com.nowcoder.demo.async.EventHandler;
import com.nowcoder.demo.async.EventModel;
import com.nowcoder.demo.async.EventType;
import com.nowcoder.demo.model.Message;
import com.nowcoder.demo.service.MessageService;
import com.nowcoder.demo.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.resources.Messages;

import java.util.*;

/**
 * @author pxk
 * @date 2019/11/30 - 17:00
 */
@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        //判断是否有异常登录，发送邮件，或者站内信
        Message message = new Message();
        message.setToId(model.getActorId());
        message.setContent("你zzz这次登录的ip异常");
        message.setFromId(3);
        message.setCreatedDate(new Date());
        message.setConversationId( String.format("%d_%d", 3, model.getActorId()));
        messageService.addMessage(message);
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("email"), "登录异常", "mails/welcome.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}