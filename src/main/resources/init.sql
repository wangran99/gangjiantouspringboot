CREATE TABLE `department`
(
    `dept_code`      VARCHAR(20)  NOT NULL COMMENT '部门id',
    `parent_code`    VARCHAR(20)  NOT NULL COMMENT '父部门id',
    `dept_name_cn`   VARCHAR(100) NOT NULL COMMENT '部门名称',
    `order_no`       VARCHAR(20)  NOT NULL DEFAULT '10000' COMMENT '部门排序',
    `manager_id`     VARCHAR(100)          DEFAULT NULL COMMENT '部门管理人员列表',
    `has_child_dept` TINYINT      NOT NULL COMMENT '是否有子部门',
    PRIMARY KEY (`dept_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='部门表';

CREATE TABLE `user`
(
    `user_id`        VARCHAR(50)  NOT NULL COMMENT '用户id',
    `user_name_cn`   VARCHAR(20)  NOT NULL COMMENT '姓名',
    `sex`            VARCHAR(1)  DEFAULT NULL COMMENT '性别',
    `mobile_number`  VARCHAR(20)  NOT NULL COMMENT '手机号',
    `main_dept_code` VARCHAR(20) DEFAULT NULL COMMENT '主管部门',
    `user_email`     VARCHAR(50)  NOT NULL COMMENT '电子邮件',
    `avatar`         VARCHAR(300) NOT NULL COMMENT '头像url',
    `position`       VARCHAR(50)  NOT NULL COMMENT '职位',
    `is_admin`       TINYINT      NOT NULL COMMENT '是否是企业管理员',
    PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `role`
(
    `id`        bigint      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `role_name` VARCHAR(20) NOT NULL UNIQUE COMMENT '角色名称',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色定义表';
insert into `role` (`role_name`)
values ('董事长');
insert into `role` (`role_name`)
values ('系统管理员');
insert into `role` (`role_name`)
values ('总经理');
insert into `role` (`role_name`)
values ('副总经理');
insert into `role` (`role_name`)
values ('职员');

CREATE TABLE `user_role`
(
    `id`        bigint      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `dept_code` VARCHAR(20) NOT NULL COMMENT '部门id',
    `user_id`   VARCHAR(20) NOT NULL COMMENT '用户id',
    `role_id`   INT         NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    UNIQUE (`dept_code`, `user_id`, `role_id`),
    INDEX (`dept_code`),
    INDEX (`user_id`),
    INDEX (`role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户角色表';