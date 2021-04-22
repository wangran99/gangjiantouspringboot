/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.security.exception;


import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * 自定义异常
 *
 * @author Mark sunlightcs@gmail.com
 */
public class RenOAuth2Exception extends OAuth2Exception {
	private String msg;

	public RenOAuth2Exception(String msg) {
		super(msg);
		this.msg = msg;
	}

	public RenOAuth2Exception(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}