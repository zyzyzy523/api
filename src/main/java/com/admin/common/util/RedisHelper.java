package com.admin.common.util;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * redis string 类型操作工具类
 * </p>
 *
 * @author bin.xie
 * @date 2019/9/7
 */
@Component
@Slf4j
public class RedisHelper {

    //处理K、V均为String类型的数据
    private final StringRedisTemplate stringRedisTemplate;

    public RedisHelper(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public StringRedisTemplate getTemplate() {
        return stringRedisTemplate;
    }

    /**
     * String类型的键值写入redis,并设置失效时间
     *
     * @param key
     * @param value
     * @param timeout
     */
    public void setStringWithExpireTime(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 给key 设置过期时间
     *
     * @param key
     * @param timeout
     */
    public void setExpireTime(String key, long timeout) {
        stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * String类型的键值写入redis,不会失效
     *
     * @param key
     * @param value
     */
    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 根据key获取Redis里的value
     *
     * @param key
     * @return
     */
    public String getStringValueByKey(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 指定key的value增加delta
     *
     * @param key
     * @param delta 可为负数
     * @return 增加之后的结果
     */
    public Long increment(String key, long delta) {
        return stringRedisTemplate.boundValueOps(key).increment(delta);
    }

    /**
     * 指定key的value增加delta
     *
     * @param key
     * @param timeout 有效时间
     * @param delta   可为负数
     * @return 增加之后的结果
     */
    public Long increment(String key, long timeout, long delta) {
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(key);
        boundValueOps.expire(timeout, TimeUnit.SECONDS);
        return boundValueOps.increment(delta);
    }

    /**
     * 根据key删除缓存
     *
     * @param key
     */
    public void deleteByKey(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 根据key删除缓存
     *
     * @param keys
     */
    public void batchDeleteByKey(Collection<String> keys) {
        stringRedisTemplate.delete(keys);
    }

    /**
     * 查询key是否存在
     *
     * @param key
     * @return
     */
    public Boolean existKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 发布channel消息
     *
     * @param key
     * @param value
     */
    public void publishChannel(String key, String value) {
        stringRedisTemplate.convertAndSend(key, value);
    }

    /**
     * 扫描redis的key
     *
     * @param pattern
     * @return
     */
    public HashSet<String> scan(String pattern) {
        HashSet<String> keys = new HashSet<>();
        RedisCallback<Boolean> redisCallback = connection -> {
            try (Cursor<byte[]> cursor = connection
                    .scan(ScanOptions.scanOptions()
                            .count(Integer.MAX_VALUE)
                            .match(pattern)
                            .build())) {
                cursor.forEachRemaining(v -> {
                    String key = new String(v);
                    keys.add(key);
                });
                return null;
            } catch (Exception e) {
                log.warn("获取redis的key [{}] 发生异常！", pattern, e);
            }
            return true;
        };
        stringRedisTemplate.execute(redisCallback);
        return keys;
    }

    public void removeByPattern(String pattern) {
        HashSet<String> keys = scan(pattern);
        if (CollUtil.isNotEmpty(keys)) {
            stringRedisTemplate.delete(keys);
        }
    }
}
