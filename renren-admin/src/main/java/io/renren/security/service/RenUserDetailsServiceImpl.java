/**
 * Copyright (c) 2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */
package io.renren.security.service;

import com.github.wangran99.welink.api.client.openapi.OpenAPI;
import com.github.wangran99.welink.api.client.openapi.model.AuthRes;
import com.github.wangran99.welink.api.client.openapi.model.TenantInfoRes;
import com.github.wangran99.welink.api.client.openapi.model.UserBasicInfoRes;
import io.renren.common.exception.ErrorCode;
import io.renren.common.exception.RenException;
import io.renren.common.utils.ConvertUtils;
import io.renren.common.utils.HttpContextUtils;
import io.renren.modules.sys.dao.SysRoleDataScopeDao;
import io.renren.modules.sys.dao.SysUserDao;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.enums.UserStatusEnum;
import io.renren.modules.sys.service.SysMenuService;
import io.renren.security.user.UserDetail;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserDetailsService
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
@AllArgsConstructor
public class RenUserDetailsServiceImpl implements UserDetailsService {
    private SysUserDao sysUserDao;
    private SysMenuService sysMenuService;
    private SysRoleDataScopeDao sysRoleDataScopeDao;

    private AuthRes authRes;
    private OpenAPI openAPI;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest httpServletRequest= HttpContextUtils.getHttpServletRequest();
        Map<String, String> map = HttpContextUtils.getParameterMap(httpServletRequest);
        TenantInfoRes tenantInfoRes = openAPI.getTenantInfo(authRes.getAccess_token());
        String userId="wangran@49c415a8500";
        UserBasicInfoRes userBasicInfoRes=openAPI.getUserInfoById(authRes.getAccess_token(), userId);
//        SysUserEntity userEntity = sysUserDao.getByUsername(username);
//        if(userEntity == null) {
//            throw new RenException(ErrorCode.ACCOUNT_NOT_EXIST);
//        }
//
//        //转换成UserDetail对象
//        UserDetail userDetail = ConvertUtils.sourceToTarget(userEntity, UserDetail.class);
//
//        //账号不可用
//        if(userEntity.getStatus() == UserStatusEnum.DISABLE.value()){
//            userDetail.setEnabled(false);
//        }
//
//        //获取用户对应的部门数据权限
//        List<Long> deptIdList = sysRoleDataScopeDao.getDataScopeList(userDetail.getId());
//        userDetail.setDeptIdList(deptIdList);
//
//        //用户权限列表
//        Set<GrantedAuthority> authorities = getUserPermissions(userDetail);
//        userDetail.setAuthorities(authorities);
        UserDetail userDetail =new UserDetail();
        userDetail.setRealName(userBasicInfoRes.getUserNameCn());
        userDetail.setDeptId(userDetail.getDeptId());
        userDetail.setSuperAdmin(1);
        userDetail.setPassword("admin");
        userDetail.setTenantCode(1001L);
        userDetail.setAccountNonExpired(true);
        userDetail.setAccountNonLocked(true);
        userDetail.setCredentialsNonExpired(true);

        return userDetail;
    }

    private Set<GrantedAuthority> getUserPermissions(UserDetail user) {
        //获取用户权限标识
        Set<String> permsSet = sysMenuService.getUserPermissions(user);

        //封装权限标识
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.addAll(permsSet.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));

        return authorities;
    }
}
