package com.nowcoder.demo.async;

import java.util.List;

/**
 * @author pxk
 * @date 2019/11/28 - 16:06
 */
public interface EventHandler {
    void doHandle(EventModel model);
    List<EventType> getSupportEventTypes();//要关注哪些接口，发生哪些event
}
