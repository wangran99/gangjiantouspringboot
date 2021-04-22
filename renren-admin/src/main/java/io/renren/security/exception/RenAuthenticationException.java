/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 认证异常类
 *
 * @author Mark sunlightcs@gmail.com
 */
public class RenAuthenticationException extends AuthenticationException {
    public RenAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public RenAuthenticationException(String msg) {
        super(msg);
    }
}
