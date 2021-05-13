package com.chinasoft.gangjiantou.controller;

import com.chinasoft.gangjiantou.constant.Constants;
import com.chinasoft.gangjiantou.redis.RedisService;
import com.github.wangran99.welink.api.client.openapi.OpenAPI;
import com.github.wangran99.welink.api.client.openapi.OpenManagerApi;
import com.github.wangran99.welink.api.client.openapi.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * <p>
 *  认证鉴权请求控制器
 * </p>
 *
 * @author WangRan
 * @since 2021-03-05
 */
@RestController
@RequestMapping("/authorization")
@Slf4j
public class AuthorizationController {

    //welink轻应用扫码鉴权地址前缀
    private static final String managerAuthUrl = "https://login.welink.huaweicloud.com/sso/oauth2/authorize?";

    @Value("${welink.openapi.client-id}")
    String clientId;

    @Value("${welink.openapi.client-secret}")
    String clientSecret;

    //welink鉴权成功后跳转到的地址
    @Value("${welink.authorization.callback.url}")
    String redirectUri;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OpenAPI openAPI;

    @Autowired
    private OpenManagerApi openManagerApi;

    /**
     * 手机We码认证
     *
     * @param authCode 前端we码获取的authCode
     * @param response
     * @return
     */
    @GetMapping(value = "/wecode")
    public UserBasicInfoRes wecodeAuthorization(String authCode, HttpServletResponse response) {
        //获取用户id和租户id
        UserIdInfo userInfo = openAPI.getUserBasicIdInfo( authCode);
        //获取个人信息。
        UserBasicInfoRes userBasicInfoRes = openAPI.getUserInfoById( userInfo.getUserId());
        redisService.saveUserInfo(authCode, userBasicInfoRes);
        response.setHeader(Constants.AUTH_CODE, authCode);
        return userBasicInfoRes;
    }


    /**
     * H5轻应用鉴权登录 开放平台文档：https://open.welink.huaweicloud.com/docs/#/qdmtm8/tj778t/wk8q1m?type=third
     * @param requestUrl 前端页面的url
     * @param request request
     * @return
     */
    @GetMapping(value = "jsauth")
    public JsAuthDomain jsAuthorization(@RequestParam(value = "requestUrl") String requestUrl, HttpServletRequest request) {
        log.info("requesturl:::::::::::" + requestUrl);
        boolean fromMobile = true;
        String value = request.getHeader("user-agent");
        if (value.contains("Electron")) {
            fromMobile = false;
        }

        String url = null;
        if (requestUrl.contains("#"))
            url = requestUrl.substring(0, requestUrl.indexOf("#"));
        else
            url = requestUrl;
//            if (fromMobile)     //从手机welink鉴权
//                url = url;
//            else    //从pc端的welink访问鉴权
//                url = url.substring(0, url.lastIndexOf("/"));
        log.info("url::::::::" + url);
        JsticketsRes jsticketsRes = openAPI.jsAuth();
        log.error("jsticket:" + jsticketsRes.getJstickets());
        Date date = new Date();
        long time = date.getTime() / 1000;
        log.info("time:" + time);
        String noncestr = "abcdefg"; //随机字符串
        String signature = sign(jsticketsRes.getJstickets(), noncestr, time, url);
        JsAuthDomain jsAuthDomain = new JsAuthDomain();
        jsAuthDomain.setAppId(clientId);
        jsAuthDomain.setUrl(url);
        jsAuthDomain.setJstickets(jsticketsRes.getJstickets());
        jsAuthDomain.setNoncestr(noncestr);
        jsAuthDomain.setTimestamp(time);
        jsAuthDomain.setSignature(signature);
        return jsAuthDomain;

    }

    /**
     * 后台web管理页面免登
     *
     * @param code welink跳转后传过来的code
     * @param state
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/web")
    public UserBasicInfoRes managerAuthorization(String code, String state, HttpServletRequest request, HttpServletResponse response) {
        log.info("web login code:" + code);
        String authCode = request.getHeader(Constants.AUTH_CODE);
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        if (userBasicInfoRes != null)
            return userBasicInfoRes;
        String welinkAuthUrl = null;
        try {
            welinkAuthUrl = managerAuthUrl + "client_id=" + clientId +
                    "&response_type=code&redirect_uri=" + URLEncoder.encode(redirectUri, "utf-8") + "&scope=backendlogin&state=" + state + "&hello=123";
        } catch (UnsupportedEncodingException e) {
            log.error("encode url error.");
            e.printStackTrace();
        }
        if (code == null || code.trim().isEmpty()) {
            response.setHeader(Constants.REDIRECT_URL_HEADER, welinkAuthUrl);
            AuthFailOrExpiredException authFailOrExpiredException = new AuthFailOrExpiredException();
            authFailOrExpiredException.setAuthUrl(welinkAuthUrl);
            throw authFailOrExpiredException;
        }
        //获取token
        ManagerAuthRes managerAuthRes = openManagerApi.managerAuthorization(code, "authorization_code", clientId, clientSecret, redirectUri, state);
        log.info("managerauthres:" + managerAuthRes.toString());
        //获取用户id
        UserIdInfo userIdInfo = openManagerApi.getUserId(managerAuthRes.getAccess_token());
        log.info("magegerauth userinfo:" + userIdInfo);

        //根据租户token，获取用户信息和管理员权限
        UserBasicInfoRes userBasicInfo = openAPI.getUserInfoById(userIdInfo.getUserId());
        IsAdminRes isAdminRes = openAPI.isAdminAndRoles( userIdInfo.getUserId());
        userBasicInfo.setIsAdminRes(isAdminRes);
        redisService.saveUserInfo(code, userBasicInfo);
        response.setHeader(Constants.AUTH_CODE, code);
        response.setHeader("weburl", redirectUri);
        return userBasicInfo;
    }

    /**
     * 用户退出(删除token)
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/logout")
    public boolean logout(HttpServletRequest request) {
        redisService.delUserInfo(request.getHeader(Constants.AUTH_CODE));
        return true;
    }

    private String sign(String jsticket, String nonceStr, long timeStamp, String url) {

        String urldecode = null;
        try {
            urldecode = java.net.URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String plain = "jsapi_ticket=" + jsticket + "&noncestr=" + nonceStr + "&timestamp=" + String.valueOf(timeStamp)
                + "&url=" + urldecode;
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.reset();
            sha256.update(plain.getBytes("UTF-8"));
            return bytesToHexString(sha256.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
