package com.nowcoder.demo.service;

import com.nowcoder.demo.dao.MessageDao;
import com.nowcoder.demo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author pxk
 * @date 2019/11/20 - 22:14
 */
@Service
public class MessageService {
    @Autowired
    MessageDao messageDao;
    public int addMessage(Message message){

        return messageDao.addMessage(message);
    }


    public int getConversationUnreadCount (int userId, String conversationId) {
        return messageDao.getConversationUnreadCount(userId, conversationId);
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit){
        return messageDao.getConversationDetail(conversationId, offset, limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDao.getConversationList(userId, offset, limit);
    }


}