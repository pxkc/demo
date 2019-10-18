package com.nowcoder.demo.service;

import com.nowcoder.demo.dao.UserDao;
import com.nowcoder.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author pxk
 * @date 2019/10/18 - 14:14
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public User getUser(int id){
        return userDao.selcetById(id);
    }
}