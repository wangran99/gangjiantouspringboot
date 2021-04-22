/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.security.handler;

import com.alibaba.fastjson.JSON;
import io.renren.common.utils.HttpContextUtils;
import io.renren.common.utils.Result;
import lombok.SneakyThrows;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证失败处理器
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component
public class UserAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @SneakyThrows
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)  {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", HttpContextUtils.getOrigin());
        response.getWriter().write(JSON.toJSONString(new Result<>().error(e.getMessage())));
    }
}
