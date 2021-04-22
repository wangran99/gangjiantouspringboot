/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.security.controller;

import io.renren.common.utils.IpUtils;
import io.renren.common.utils.Result;
import io.renren.modules.log.entity.SysLogLoginEntity;
import io.renren.modules.log.enums.LoginOperationEnum;
import io.renren.modules.log.service.SysLogLoginService;
import io.renren.security.user.UserDetail;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 认证管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@AllArgsConstructor
@Api(tags="认证管理")
public class OAuth2Controller {
    private TokenStore tokenStore;
    private SysLogLoginService sysLogLoginService;

    /**
     * 退出
     */
    @PostMapping("/oauth/logout")
    public Result logout(HttpServletRequest request) {
        String access_token = request.getHeader(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(access_token);
        if(oAuth2AccessToken != null){
            //用户信息
            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(oAuth2AccessToken);
            UserDetail user = (UserDetail) oAuth2Authentication.getPrincipal();

            tokenStore.removeAccessToken(oAuth2AccessToken);
            OAuth2RefreshToken oAuth2RefreshToken = oAuth2AccessToken.getRefreshToken();
            tokenStore.removeRefreshToken(oAuth2RefreshToken);
            tokenStore.removeAccessTokenUsingRefreshToken(oAuth2RefreshToken);

            //用户退出日志
            SysLogLoginEntity log = new SysLogLoginEntity();
            log.setOperation(LoginOperationEnum.LOGOUT.value());
            log.setIp(IpUtils.getIpAddr(request));
            log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
            log.setIp(IpUtils.getIpAddr(request));
            log.setCreator(user.getId());
            log.setCreatorName(user.getUsername());
            log.setCreateDate(new Date());
            sysLogLoginService.save(log);
        }
        return new Result();
    }

}