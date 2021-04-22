/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.security.filter;

import io.renren.common.exception.ErrorCode;
import io.renren.common.utils.MessageUtils;
import io.renren.security.exception.RenAuthenticationException;
import io.renren.security.handler.UserAuthenticationFailureHandler;
import io.renren.security.service.CaptchaService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码过滤器
 *
 * @author Mark sunlightcs@gmail.com
 */
@AllArgsConstructor
@Component
public class ValidateCodeFilter extends OncePerRequestFilter {
    private final static String OAUTH_TOKEN_URL = "/oauth/token";
    private CaptchaService captchaService;
    private UserAuthenticationFailureHandler authenticationFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals(OAUTH_TOKEN_URL)
            && request.getMethod().equalsIgnoreCase("POST")
            && "password".equalsIgnoreCase(request.getParameter("grant_type"))) {
            try {
                //校验验证码
                validate(request);
            }catch (AuthenticationException e) {
                //失败处理器
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void validate(HttpServletRequest request) {
        String uuid = request.getParameter("uuid");
        String captcha = request.getParameter("captcha");

        boolean flag = captchaService.validate(uuid, captcha);

        if(!flag) {
            throw new RenAuthenticationException(MessageUtils.getMessage(ErrorCode.CAPTCHA_ERROR));
        }
    }
}
