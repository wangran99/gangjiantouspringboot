package com.chinasoft.gangjiantou.exception;

import com.github.wangran99.welink.api.client.openapi.model.IException;

/**
 * 通用异常类
 */
public class CommonException extends RuntimeException{

    private int  code;
    private String msg;

    public CommonException(IException iException) {
        super(iException.getDesc());
        this.msg = iException.getDesc();
        this.code = iException.getCode();
    }
    public CommonException() {
        super("CommonException");
    }

}
