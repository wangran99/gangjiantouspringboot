/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.security.listener;

import io.renren.common.utils.HttpContextUtils;
import io.renren.common.utils.IpUtils;
import io.renren.modules.log.entity.SysLogLoginEntity;
import io.renren.modules.log.enums.LoginOperationEnum;
import io.renren.modules.log.service.SysLogLoginService;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 用户登录的监听事件
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component
@AllArgsConstructor
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private SysLogLoginService sysLogLoginService;
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();

        SysLogLoginEntity log = new SysLogLoginEntity();
		log.setOperation(LoginOperationEnum.LOGIN.value());
		log.setCreateDate(new Date());
		log.setIp(IpUtils.getIpAddr(request));
		log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
		log.setIp(IpUtils.getIpAddr(request));
        log.setCreatorName(request.getParameter("username"));
        sysLogLoginService.save(log);
    }

}