/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.security.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * TokenStore
 *
 * @author Mark sunlightcs@gmail.com
 */
@Configuration
@AllArgsConstructor
public class TokenStoreConfig {
    private DataSource dataSource;
    //private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public TokenStore tokenStore() {
        //基于redis存储token
        //return new RedisTokenStore(redisConnectionFactory);

        //基于jdbc存储token
        return new JdbcTokenStore(dataSource);
    }
}
