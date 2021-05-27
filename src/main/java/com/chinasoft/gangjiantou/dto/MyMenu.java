package com.chinasoft.gangjiantou.dto;

import com.chinasoft.gangjiantou.entity.Menu;
import lombok.Data;

import java.util.List;

@Data
public class MyMenu {
    Menu menu;
    List<Menu> subMenu;
}
