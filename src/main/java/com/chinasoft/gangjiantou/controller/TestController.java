package com.chinasoft.gangjiantou.controller;

//import com.chinasoft.example.service.ILoginUserService;
import com.chinasoft.gangjiantou.annotation.RequestLimit;
import com.chinasoft.gangjiantou.mapper.LoginUserMapper;
import com.chinasoft.gangjiantou.redis.RedisService;
import com.chinasoft.gangjiantou.service.ILoginUserService;
import com.github.wangran99.welink.api.client.openapi.OpenAPI;
import com.github.wangran99.welink.api.client.openapi.model.AuthRes;
import com.github.wangran99.welink.api.client.openapi.model.TenantInfoRes;
import com.github.wangran99.welink.api.client.openapi.model.UserBasicInfoRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试服务是否正常启动
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    OpenAPI openAPI;

    @Autowired
    LoginUserMapper loginUserMapper;

    @Autowired
    ILoginUserService iLoginUserService;

    @Autowired
    RedisService redisService;

    /**
     * 测试返回字符串
     * @return
     */
    @GetMapping("hello")
    public String hello(){
        return "hello world.";
    }

    /**
     * 测试后台welink调用是否成功
     * @return
     */
    @GetMapping("welink")
    @RequestLimit
    public TenantInfoRes welink(){
        TenantInfoRes tenantInfoRes = openAPI.getTenantInfo();
        return tenantInfoRes;
    }


    /**
     * 测试数据库读取是否正常
     * @return
     */
    @GetMapping("time")
    public Object time(){
       return loginUserMapper.selectList(null);
//        return null;
    }

    @GetMapping("null")
    public Integer testnull(){
        return null;
    }

    @GetMapping("exception")
    public Object testException(String test){
       throw new NullPointerException("test null exception.");
    }

    @GetMapping("redis")
    public UserBasicInfoRes testRedisSave(){
        String userId ="zhoulipeng@49c415a8500";
        UserBasicInfoRes userBasicInfoRes = openAPI.getUserInfoById( userId);
        redisService.saveUserInfo(userId,userBasicInfoRes);
        return userBasicInfoRes;
    }

    @GetMapping("redisget")
    public UserBasicInfoRes testRedisGet(){
        String userId ="zhoulipeng@49c415a8500";
       return redisService.getUserInfo(userId);
    }
}
