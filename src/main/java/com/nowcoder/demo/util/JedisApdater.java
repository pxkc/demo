package com.nowcoder.demo.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;


/**
 * @author pxk
 * @date 2019/11/21 - 16:06
 */
//@Component 和下面一样
@Service
public class JedisApdater implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisApdater.class);
    private JedisPool pool = null;


    public static void print(int index, Object obj) {
        System.out.println(String.format("%d, %s", index, obj.toString()));
    }
    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushAll();//全删掉数据

//        Map<String, String> map = new Map<String, String>;

        jedis.set("hello", "world");
        print(1, jedis.get("hello"));

        jedis.rename("hello", "newHello");
        print(2,jedis.get("newHello"));
        jedis.setex("hello2", 13, "world");//设置过期时间
        jedis.set("pv", "100");
        jedis.incr("pv");
        print(2, jedis.get("pv"));
        jedis.incrBy("pv", 5);
        print(2, jedis.get("pv"));

        //列表操作
        String listName = "listA";
        for (int i = 0; i < 10; i ++){
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        print(3, jedis.lrange(listName, 0, 12));
        print(4, jedis.llen((listName)));
        print(5, jedis.lpop(listName));
        print(6, jedis.llen(listName));
        print(7, jedis.lindex(listName, 3));
        print(8, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "aaa"));
        print(9, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "bbb"));

        print(10, jedis.lrange(listName, 0, 12));

        String userKey = "user12";
        jedis.hset(userKey, "name", "jim");
        //不定的属性，hashset
        jedis.hset(userKey, "age", "12" );
        //记录最近来访，记录最近20过，超过20个之后，只保存最近是个
        print(12, jedis.hget(userKey, "name"));
        jedis.hdel(userKey,"phone");//底层是hash表
        print(16, jedis.hgetAll(userKey));
        print(17, jedis.hexists(userKey, "email"));

        jedis.hsetnx(userKey,"school", "zju");
        jedis.hsetnx(userKey, "name", "zzz");//有时候要判断存不存在，没有的话，加一个，有的话，更新

        //set
        String likeKeys1 = "newslike1";
        String likeKeys2 = "newsLike2";
        for(int i = 0; i < 10; i ++){
            jedis.sadd(likeKeys1, String.valueOf(i));
            jedis.sadd(likeKeys2, String.valueOf(i*2));
        }

        //共同好友，共同关注，
        print(20, jedis.smembers(likeKeys1));
        print(21, jedis.smembers(likeKeys2));
        print(22, jedis.sinter(likeKeys1, likeKeys2));

        print(22, jedis.sunion(likeKeys1, likeKeys2));
        print(23, jedis.sdiff(likeKeys1, likeKeys2));

        print(24, jedis.sismember(likeKeys1, "5"));//这个东西是否存在

        print(25, jedis.srem(likeKeys1, "5"));// sremove 删除set，lrem 删除list
        print(26, jedis.sismember(likeKeys1, "5"));

        jedis.smove(likeKeys2, likeKeys1, "14");
        print(28, jedis.scard(likeKeys1)); //现在有多少个元素

        // Zsort
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "jim");
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lee");
        jedis.zadd(rankKey, 60, "Mei");
        jedis.zadd(rankKey, 90, "Lucy");

        print(30, jedis.zcard(rankKey));
        print(31, jedis.zcount(rankKey, 61, 100));

        print(32, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 3,"lucy");//给lucy加两分
        print(33, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "luc");//写错了默认从0 加，现在为两分
        print(34, jedis.zcount(rankKey, 0, 1000));
        print(35, jedis.zrange(rankKey,1, 3));//排名人的名字list

        for (Tuple tuple: jedis.zrangeByScoreWithScores(rankKey, "0","100")) {
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));//排名打印分数
        }

        print(38, jedis.zrank(rankKey, "Ben"));//他排在多少名
        print(39, jedis.zrevrank(rankKey, "Ben"));//排在倒数多少

        //RedisPool
        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; i ++){
            Jedis j = pool.getResource();
            j.get("a");
            System.out.println("Pool " + i);
            j.close();//连接池打印100个，默认只能取出8个线程不放回去，关闭，资源没有复用
        }

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost", 6379);
    }

    private Jedis getJedis() {
        return pool.getResource();
    }

    /**
     * 我们这里用的是池的概念，不能直接调用，先从池里面取一个
     * @param key
     * @param value
     * @return
     */
    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e){
            logger.error("发生异常sadd", e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();//不管怎么样，要关掉redis
            }
        }
    }

    /**
     * 取消掉的功能 remove
     * @param key
     * @param value
     * @return
     */
    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e){
            logger.error("发生异常srem", e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();//不管怎么样，要关掉redis
            }
        }
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e){
            logger.error("发生异常sismember", e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();//不管怎么样，要关掉redis
            }
        }
    }

    /**
     * 集合里面有多少人
     * @param key
     * @return
     */
    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e){
            logger.error("发生异常scard", e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();//不管怎么样，要关掉redis
            }
        }
    }


    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return getJedis().get(key);
        } catch (Exception e) {
            logger.error("发生异常get" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("发生异常set" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public void setObject(String key, Object obj) {
        set(key, JSON.toJSONString(obj));
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String value = get(key);
        if (value != null) {
            return JSON.parseObject(value, clazz);
        }
        return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常lpush" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常brpop" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}