package com.nowcoder.demo.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.demo.util.JedisApdater;
import com.nowcoder.demo.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author pxk
 * @date 2019/11/28 - 16:10
 * 将所做的事情放到队列，发一个事情event
 */
@Service
public class EventProducer {
    @Autowired
    JedisApdater jedisApdater;

    public boolean fireEvent(EventModel model) {
        try {
            String json = JSONObject.toJSONString(model);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisApdater.lpush(key, json);// 事件序列化，推进去放到redis数据库中event队列留异步处理
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}