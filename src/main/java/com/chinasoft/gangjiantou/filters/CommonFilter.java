package com.chinasoft.gangjiantou.filters;

import com.chinasoft.gangjiantou.constant.Constants;
import com.chinasoft.gangjiantou.redis.RedisService;
import com.github.wangran99.welink.api.client.openapi.OpenAPI;
import com.github.wangran99.welink.api.client.openapi.model.AuthFailOrExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class CommonFilter implements Filter {
    private final static Set<String> excludeUri = new HashSet<>(
            Arrays.asList("/authorization/*", "/file/*", "/test/*","/static/*"));
    final AntPathMatcher matcher = new AntPathMatcher();
    @Autowired
    private OpenAPI openAPI;
    @Autowired
    private RedisService redisService;
    //解决Filter中不能抛出异常的问题
    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver resolver;

    public CommonFilter() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("request url in commonfilter:" + request.getRequestURL().toString());

        // 探针和认证接口不需要拦截
        if (isExclusion(request)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (openAPI == null) { //获取spring bean
            BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
            openAPI = factory.getBean(OpenAPI.class);
            redisService= factory.getBean(RedisService.class);
            resolver = (HandlerExceptionResolver) factory.getBean("handlerExceptionResolver");
        }


        String authCode = request.getHeader(Constants.AUTH_CODE);
        if (authCode == null || redisService.getUserInfo(authCode) == null) {
            resolver.resolveException(request, response, null, new AuthFailOrExpiredException());
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    //排除过滤的url
    private boolean isExclusion(HttpServletRequest request) {
        if ( excludeUri.isEmpty()) {
            return false;
        }
        String requestURI = request.getRequestURI();
        String requestContext = request.getContextPath();
        requestURI = requestURI.substring(requestContext.length());
        if (!requestURI.startsWith("/")) {
            requestURI = "/" + requestURI;
        }

        Iterator i$ = excludeUri.iterator();
        String pattern;
        do {
            if (!i$.hasNext()) {
                return false;
            }

            pattern = (String) i$.next();
        } while (!matcher.match(pattern, requestURI));
        return true;
    }
}
