package com.nowcoder.demo.util;

/**
 * @author pxk
 * @date 2019/11/22 - 22:31
 */
public class RedisKeyUtil {
    //根据规范生成key
    //业务 + 参数
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";//
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENT = "EVENT";

    public static String getEventQueueKey() {
        return BIZ_EVENT;
    }

    public static String getLikeKey (int entityId, int entityType) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT +  String.valueOf(entityId);
    }
    public static String getDISLikeKey (int entityId, int entityType) {
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }
}