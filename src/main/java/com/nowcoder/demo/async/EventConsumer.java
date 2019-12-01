package com.nowcoder.demo.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.demo.util.JedisApdater;
import com.nowcoder.demo.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pxk
 * @date 2019/11/28 - 16:17
 */

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisApdater jedisApdater;

    /**
     * 遍历所有实现eventhandler的类，类里面每一个eventhandler里面
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);//所有实现eventhandler的接口的类全部找出来
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();//看每一个handler对应的是哪个事件
                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    // 注册每个事件的处理函数
                    config.get(type).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> messages = jedisApdater.brpop(0, key);//等待从redis取事件
                    for (String message: messages) {
                        if(message.equals(key)) {
                            continue;
                        }
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别事件");
                            continue;
                        }

                        for (EventHandler handler: config.get(eventModel.getType())){
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}