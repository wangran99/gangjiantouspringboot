/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.security.controller;

import io.renren.common.exception.ErrorCode;
import io.renren.common.validator.AssertUtils;
import io.renren.security.service.CaptchaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码
 *
 * @author Mark sunlightcs@gmail.com
 */
@Controller
@AllArgsConstructor
@Api(tags="验证码")
public class CaptchaController {
    private CaptchaService captchaService;

    @GetMapping("/captcha")
    @ApiOperation(value = "验证码", produces="application/octet-stream")
    @ApiImplicitParam(paramType = "query", dataType="string", name = "uuid", required = true)
    public void captcha(HttpServletResponse response, String uuid)throws IOException {
        //uuid不能为空
        AssertUtils.isBlank(uuid, ErrorCode.IDENTIFIER_NOT_NULL);

        //生成验证码
        captchaService.create(response, uuid);
    }
}
