package com.chinasoft.gangjiantou.filters;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    /**
     * 需要检测管理员权限的接口url
     */
    @Value("${admin.role.filter.url:/service/mat/cardRecordService/v1/records,/service/mat/cardRecordService/v2/records}")
    private String adminRoleCheckUrlPatterns;

    @Bean
    public FilterRegistrationBean commonFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new CommonFilter());
        registration.setName("CommonFilter");
        registration.addUrlPatterns("/hello/*");
        registration.setOrder(0);//数字越小，优先级越高
        return registration;
    }

//    @Bean
//    public FilterRegistrationBean permissionCheckFilter() {
//        FilterRegistrationBean registration = new FilterRegistrationBean(new PermissionCheckFilter());
//        String[] urlPatternArray = adminRoleCheckUrlPatterns.split(",");
//        registration.addUrlPatterns(urlPatternArray);
//        registration.setName("PermissionCheckFilter");
//        registration.setOrder(3);
//        return registration;
//    }
}
