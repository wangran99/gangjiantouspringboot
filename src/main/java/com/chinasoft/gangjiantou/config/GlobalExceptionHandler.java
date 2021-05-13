package com.chinasoft.gangjiantou.config;


import com.chinasoft.gangjiantou.exception.CommonException;
import com.chinasoft.gangjiantou.exception.RequestLimitException;
import com.github.wangran99.welink.api.client.openapi.model.AuthFailOrExpiredException;
import com.github.wangran99.welink.api.client.openapi.model.OpenApiException;
import com.github.wangran99.welink.api.client.openapi.model.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @author ：WangRan
 * @date ：Created in 2020/12/6 11:31
 * @description：全局异常管理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 一般Exception
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResultVO handlerExceptionHello(Exception e) {
        log.error("exception！" + e.getMessage(), e);
        return ResultVO.getError(e.getMessage());
    }


    /**
     * 接口请求次数超过限制次数异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RequestLimitException.class)
    public ResultVO handleRequestLimitException(RequestLimitException e) {
        log.error("RequestLimitException--{}", e.getMessage(), e);
        return ResultVO.getError(e.getMessage());
    }

    /**
     * 调用Welink开放平台时出现的异常处理逻辑
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = OpenApiException.class)
    public ResultVO handlerOpenApiException(OpenApiException e) {
        log.error("OpenApiException: errCode:" + e.getCode() + ",msg:" + e.getMsg(), e);
        return ResultVO.getError(e.getMessage());
    }

    /**
     * 用户认证失败或者认证已过期的处理逻辑
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = AuthFailOrExpiredException.class)
    public ResultVO handlerAuthFailOrExpiredException(AuthFailOrExpiredException e) {
        log.error("AuthFailOrExpiredException: errCode:" + e.getCode() + ",msg:" + e.getMsg(), e);
        return ResultVO.getAuthFailOrExpired(e.getMessage(), e.getAuthUrl());
    }

    /**
     * 空指针异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    public ResultVO handlerNullPointerException(NullPointerException e) {
        log.error("发生空指针异常！", e);
        return ResultVO.getError("空指针异常");
    }

    /**
     * 通用异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = CommonException.class)
    public ResultVO handlerCommonException(CommonException e) {
        log.error("发生通用异常！", e);
        return ResultVO.getError(e.getMessage());
    }

}