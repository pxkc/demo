package com.nowcoder.demo.service;

import com.nowcoder.demo.util.JedisApdater;
import com.nowcoder.demo.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

/**
 * 从jedis里面直接读，不直接去数据库了，dao用jedisApdater了，现在就是service
 * @author pxk
 * @date 2019/11/22 - 22:27
 * 插入，删除，在不在
 */
@Service
public class LikeService {
    @Autowired
    JedisApdater jedisApdater;

    /**
     * 这是得到当前的状态，通用的，对某一个元素是否喜欢（咨询，评论），如果喜欢返回1，如果不喜欢返-1
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);//对这个元素喜欢的所有集合
        if (jedisApdater.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDISLikeKey(entityId, entityType);
        return jedisApdater.sismember(disLikeKey, String.valueOf(userId))? -1: 0;
    }

    /**
     * 如果喜欢，逻辑是加到喜欢里面，不喜欢里面删掉，所以是两个操作
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public long like(int userId, int entityType, int entityId) {
        //加到喜欢里面
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisApdater.sadd(likeKey, String.valueOf(userId));

        //从不喜欢里面删掉
        String disLikeKey = RedisKeyUtil.getDISLikeKey(entityId, entityType);
        jedisApdater.srem(disLikeKey, String.valueOf(userId));

        return jedisApdater.scard(likeKey);
    }

    public long disLike(int userId, int entityType, int entityId) {
        //加到不喜欢里面
        String disLikeKey = RedisKeyUtil.getDISLikeKey(entityId, entityType);
        jedisApdater.sadd(disLikeKey, String.valueOf(userId));

        //从喜欢里面删掉
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisApdater.srem(likeKey, String.valueOf(userId));

        return jedisApdater.scard(likeKey);//还剩多少个喜欢
    }

}