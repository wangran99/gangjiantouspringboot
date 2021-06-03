package com.chinasoft.gangjiantou.redis;

import com.chinasoft.gangjiantou.constant.Constants;
import com.github.wangran99.welink.api.client.openapi.model.UserBasicInfoRes;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Gson gson;


    private void set(String key, Object value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.MINUTES);
    }


    private Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }


    private Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除键值对
     *
     * @param keys
     * @return 删除的个数
     */

    private Long del(List<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param time
     * @return
     */

    private Boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }


    private Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断是否存在key
     *
     * @param key
     * @return
     */

    private Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 加
     *
     * @param key
     * @param delta 步长
     * @return
     */

    private Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }


    private Long decr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 获取缓存的用户信息
     *
     * @param authCode
     * @return
     */
    public UserBasicInfoRes getUserInfo(String authCode) {
        Object userInfo = get(Constants.AUTH_CODE + ":" + authCode);
        return (UserBasicInfoRes) userInfo;
    }

    /**
     * 根据用户的authCode，缓存用户基本信息
     *
     * @param authCode
     * @param userBasicInfoRes
     */
    public void saveUserInfo(String authCode, UserBasicInfoRes userBasicInfoRes) {
        set(Constants.AUTH_CODE + ":" + authCode, userBasicInfoRes, 20*3*24*30);
    }

    /**
     * 删除用户缓存
     *
     * @param authCode
     */
    public void delUserInfo(String authCode) {
        redisTemplate.delete(Constants.AUTH_CODE + ":" + authCode);
    }
}
