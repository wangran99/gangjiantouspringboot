package com.chinasoft.gangjiantou.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 允许跨域。（方便前端人员在本地开发调试，对后端开发无影响）
 * 实现跨域请求的几种方法：https://cloud.tencent.com/developer/article/1513473
 * @author WangRan
 */
@Configuration
public class CorsConfig {
	private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*"); // 1 允许任何域名使用
        corsConfiguration.addAllowedHeader("*"); // 2 允许任何头
        corsConfiguration.addAllowedMethod("*"); // 3 允许任何方法（post、get等）
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig()); // 4
        return new CorsFilter(source);
    }
	

}
