package com.nowcoder.demo.async;

/**
 * @author pxk
 * @date 2019/11/28 - 15:54
 */
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);

    private int value;
    EventType(int value) {this.value = value;}

    public int getValue() { return value;}

}

