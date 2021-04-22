/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.security.exception;

import com.alibaba.fastjson.JSON;
import io.renren.common.exception.ErrorCode;
import io.renren.common.utils.HttpContextUtils;
import io.renren.common.utils.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * 匿名用户(token不存在、错误)，异常处理器
 */
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
       response.setContentType("application/json; charset=utf-8");
       response.setHeader("Access-Control-Allow-Credentials", "true");
       response.setHeader("Access-Control-Allow-Origin", HttpContextUtils.getOrigin());

       response.getWriter().print(JSON.toJSONString(new Result<>().error(ErrorCode.UNAUTHORIZED)));
    }
}