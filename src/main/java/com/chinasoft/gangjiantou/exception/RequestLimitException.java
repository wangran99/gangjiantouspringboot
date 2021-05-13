package com.chinasoft.gangjiantou.exception;

import com.chinasoft.gangjiantou.constant.ResultCode;

/**
 * 接口请求次数限制异常类
 */
public class RequestLimitException extends CommonException{

    public RequestLimitException() {
        super(ResultCode.RequestLimitException);
    }
}
