package com.chinasoft.gangjiantou.dto;

import lombok.Data;

import java.util.List;

@Data
public class Callback {
    /**
     * 0 - 文档标识为空
     * 1 - 连接文档
     * 2 - 文档已准备好保存（一般是关闭页面后）
     * 3 - 文档保存错误
     * 4 - 文档关闭并且文档没有修改
     * 6 - 点击界面上的保存按钮（强制保存）
     * 7 - 强制保存文档时发生错误
     */
    Integer status;
    /**
     * 执行强制保存时，此参数有效。此参数存在二个种状态
     * 0 -使用编辑器后台交互接口保存文档
     * 1 - 单击保存按钮时，（仅当前端配置forcesave为true时，才可用）
     * 2- 服务器后台每5分钟定时执行保存（默认关闭）此时status值为6或7
     */
    Integer forcesavetype;
    /**
     * 当前需要保存的文档URL。仅当status为2或3时,业务系统拉取url指定的文档保存到系统中。
     * URL 15分钟过期
     */
    String url;
    /**
     * 文档标识
     */
    String key;
    List<Object> actions;
}
