package com.nowcoder.demo.model;

import org.springframework.stereotype.Component;

/**
 * @author pxk
 * @date 2019/11/9 - 16:18
 * 用来存用户的，set get 存取
 * 专门存储这一条访问里面的用户是谁
 */

@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<User>();//线程本地变量，自己的

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}