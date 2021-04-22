/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.security.constant;

/**
 * OAuth 常量
 *
 * @author Mark sunlightcs@gmail.com
 */
public class OauthConstant {
    private static final String CLIENT_FIELDS_FOR_UPDATE = "resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information, autoapprove";

    private static final String CLIENT_FIELDS = "client_secret, " + CLIENT_FIELDS_FOR_UPDATE;

    private static final String BASE_FIND_STATEMENT = "select client_id, " + CLIENT_FIELDS
            + " from sys_oauth_client_details";

    public static final String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where client_id = ?";

    public static final String DEFAULT_FIND_STATEMENT = BASE_FIND_STATEMENT + " order by client_id";

}
