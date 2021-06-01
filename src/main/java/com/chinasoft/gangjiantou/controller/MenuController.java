package com.chinasoft.gangjiantou.controller;


import com.chinasoft.gangjiantou.dto.MyMenu;
import com.chinasoft.gangjiantou.entity.Menu;
import com.chinasoft.gangjiantou.entity.RoleMenu;
import com.chinasoft.gangjiantou.entity.UserRole;
import com.chinasoft.gangjiantou.redis.RedisService;
import com.chinasoft.gangjiantou.service.IMenuService;
import com.chinasoft.gangjiantou.service.IRoleMenuService;
import com.chinasoft.gangjiantou.service.IRoleService;
import com.chinasoft.gangjiantou.service.IUserRoleService;
import com.github.wangran99.welink.api.client.openapi.model.UserBasicInfoRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author WangRan
 * @since 2021-05-27
 */
@RestController
@Slf4j
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IRoleMenuService roleMenuService;
    @Autowired
    private IUserRoleService userRoleService;
    @Autowired
    private IMenuService menuService;

    private UserBasicInfoRes getUserBasicInfoRes(String authCode) {
        return redisService.getUserInfo(authCode);
    }

    /**
     * 获取所有的菜单
     * @return
     */
    @GetMapping("all")
    public List<MyMenu> all(){
        List<Menu> menuList = menuService.list();
        List<MyMenu> myMenuList = new ArrayList<>();
        for (Menu menu : menuList) //一级目录
            if (menu.getParentId() == null) {
                MyMenu myMenu = new MyMenu();
                myMenu.setMenu(menu);
                myMenuList.add(myMenu);
            }
        for (MyMenu myMenu : myMenuList) {//二级目录
            List<Menu> subMenuList = new ArrayList<>();
            myMenu.setSubMenu(subMenuList);
            for (Menu menu : menuList)
                if (myMenu.getMenu().getId().equals(menu.getParentId()))
                    subMenuList.add(menu);
        }
        return myMenuList;
    }

    /**
     * 获取我的菜单
     *
     * @param authCode
     * @return
     */
    @GetMapping("my")
    public List<MyMenu> getMyMenu(@RequestHeader("authCode") String authCode) {
        UserBasicInfoRes userBasicInfoRes = redisService.getUserInfo(authCode);
        log.error("user:"+userBasicInfoRes);
        List<UserRole> list = userRoleService.lambdaQuery().eq(UserRole::getUserId, userBasicInfoRes.getUserId()).list();
        List<RoleMenu> roleMenuList = roleMenuService.lambdaQuery().in(RoleMenu::getRoleId, list.stream().map(e -> e.getRoleId()).collect(Collectors.toList())).list();
        Set<Long> menuIdSet = new HashSet<>();
        for (RoleMenu roleMenu : roleMenuList)
            menuIdSet.add(roleMenu.getMenuId());
        List<Menu> menuList = menuService.listByIds(menuIdSet);
        List<MyMenu> myMenuList = new ArrayList<>();
        for (Menu menu : menuList) //一级目录
            if (menu.getParentId() == null) {
                MyMenu myMenu = new MyMenu();
                myMenu.setMenu(menu);
                myMenuList.add(myMenu);
            }
        for (MyMenu myMenu : myMenuList) {//二级目录
            List<Menu> subMenuList = new ArrayList<>();
            myMenu.setSubMenu(subMenuList);
            for (Menu menu : menuList)
                if (myMenu.getMenu().getId().equals(menu.getParentId()))
                    subMenuList.add(menu);
        }
        return myMenuList;
    }
}
