package com.nowcoder.demo.service;

import com.nowcoder.demo.dao.CommentDao;
import com.nowcoder.demo.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author pxk
 * @date 2019/11/20 - 14:54
 */
@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;

    public List<Comment> getCommentsByEntity(int entityId, int entityType){
        return commentDao.selectByEntity(entityId, entityType);
    }

    public int addComment(Comment comment){
        return commentDao.addComment(comment);
    }

    public int getCommentsCount(int entityId, int entityType){
        return commentDao.getCommentsCount(entityId, entityType);
    }
}