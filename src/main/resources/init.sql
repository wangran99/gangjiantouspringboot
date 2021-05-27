CREATE TABLE `department`
(
    `dept_code`      VARCHAR(20)  NOT NULL COMMENT '部门id',
    `parent_code`    VARCHAR(20)  NOT NULL COMMENT '父部门id',
    `dept_name_cn`   VARCHAR(100) NOT NULL COMMENT '部门名称',
    `order_no`       INT          DEFAULT 10000 COMMENT '部门排序',
    `dept_level`     INT          DEFAULT NULL COMMENT '部门层次',
    `manager_id`     VARCHAR(100) DEFAULT NULL COMMENT '部门管理人员列表',
    `has_child_dept` TINYINT      NOT NULL COMMENT '是否有子部门',
    PRIMARY KEY (`dept_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='部门表';

CREATE TABLE `user`
(
    `user_id`        VARCHAR(50)  NOT NULL COMMENT '用户id',
    `user_name_cn`   VARCHAR(20)  NOT NULL COMMENT '姓名',
    `sex`            VARCHAR(1)  DEFAULT NULL COMMENT '性别.M:男，F：女，其他：未知',
    `mobile_number`  VARCHAR(20)  NOT NULL COMMENT '手机号',
    `main_dept_code` VARCHAR(20) DEFAULT NULL COMMENT '主管部门',
    `dept_code`      VARCHAR(100) NOT NULL COMMENT '所在部门',
    `user_email`     VARCHAR(50)  NOT NULL COMMENT '电子邮件',
    `avatar`         VARCHAR(300) NOT NULL COMMENT '头像url',
    `position`       VARCHAR(50)  NOT NULL COMMENT '职位',
    `is_admin`       TINYINT      NOT NULL COMMENT '是否是企业管理员',
    `status`         VARCHAR(1)   NOT NULL COMMENT '用户状态。1：未开户，2：开户中，3：已开户，4：已销户',
    PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';


CREATE TABLE `position`
(
    `id`            bigint      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `position_name` VARCHAR(20) NOT NULL UNIQUE COMMENT '岗位名称',
    `position_code` VARCHAR(20) NOT NULL UNIQUE COMMENT '岗位编码',
    `order`         INT         NOT NULL DEFAULT 1000 COMMENT '岗位排序',
    `note`          VARCHAR(100)         DEFAULT NULL COMMENT '岗位备注',
    `status`        TINYINT     NOT NULL DEFAULT 1 COMMENT '岗位状态.0:停用。1：正常',
    `create_time`   datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '设置时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='岗位定义表';
insert into `position` (`position_name`, `position_code`)
values ('董事长', 'CEO');
insert into `position` (`position_name`, `position_code`)
values ('总经理', 'manager');
insert into `position` (`position_name`, `position_code`)
values ('副总经理', 'vice manager');
insert into `position` (`position_name`, `position_code`)
values ('职员', 'employee');

CREATE TABLE `user_position`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `dept_code`   VARCHAR(20) NOT NULL COMMENT '部门id',
    `user_id`     VARCHAR(20) NOT NULL COMMENT '用户id',
    `user_name`   VARCHAR(20) NOT NULL COMMENT '用户姓名',
    `position_id` INT         NOT NULL COMMENT '岗位ID',
    `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '设置时间',
    PRIMARY KEY (`id`),
    UNIQUE (`dept_code`, `user_id`, `position_id`),
    INDEX (`dept_code`, `position_id`),
    INDEX (`user_id`),
    INDEX (`position_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户岗位表';

CREATE TABLE `file`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `file_name`   VARCHAR(100) NOT NULL COMMENT '文件名',
    `path`        VARCHAR(200) NOT NULL COMMENT '相对路径',
    `user_id`     VARCHAR(20)  NOT NULL COMMENT '用户id',
    `user_name`   VARCHAR(20)  NOT NULL COMMENT '用户姓名',
    `approval_id` bigint       NOT NULL COMMENT '审批ID',
    `upload_time` datetime(0)  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    PRIMARY KEY (`id`),
    INDEX (`approval_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='上传文件信息表';


CREATE TABLE `role`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `role_name`   VARCHAR(20) NOT NULL UNIQUE COMMENT '角色名称',
    `note`        VARCHAR(100)         DEFAULT NULL COMMENT '角色备注',
    `status`      TINYINT     NOT NULL DEFAULT 1 COMMENT '角色状态.0:不可删除。1：可删除',
    `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '设置时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色定义表';
INSERT INTO `role` (`id`, `role_name`, `note`, `status`)
values (1, '管理员', '', 0),
       (2, '普通用户', '', 0);

CREATE TABLE `menu`
(
    `id`        bigint      NOT NULL COMMENT 'id',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `parent_id` bigint      DEFAULT NULL COMMENT '父目录id',
    `router`    VARCHAR(50) DEFAULT NULL COMMENT '路由',
    `order`     INT         DEFAULT 1000 COMMENT '排序',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='菜单表';
INSERT INTO `menu` (`id`, `menu_name`, `parent_id`, `router`, `order`)
values (1000, "组织管理", null, null, 1000),
       (1100, "用户管理", 1000, null, 1000),
       (1200, "部门管理", 1000, null, 1000),
       (1300, "角色管理", 1000, null, 1000),
       (1400, "岗位管理", 1000, null, 1000),

       (2000, "审批管理", null, null, 1000),
       (2100, "审批流程", 2000, null, 1000),
       (2200, "我的申请", 2000, null, 1000),
       (2300, "我的审批", 2000, null, 1000),
       (2400, "抄送我的", 2000, null, 1000);

CREATE TABLE `role_menu`
(
    `id`      bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
    `role_id` bigint NOT NULL COMMENT '角色id',
    `menu_id` bigint NOT NULL COMMENT '菜单id',
    PRIMARY KEY (`id`),
    UNIQUE (`role_id`, `menu_id`),
    INDEX (`role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色菜单对应表';
INSERT INTO `role_menu` (`role_id`, `menu_id`)
values (1, 1000),
       (1, 1100),
       (1, 1200),
       (1, 1300),
       (1, 1400),
       (1, 2000),
       (1, 2100),
       (1, 2200),
       (1, 2300),
       (1, 2400),
       (2, 2000),
       (2, 2100),
       (2, 2200),
       (2, 2300),
       (2, 2400);
CREATE TABLE `apply`
(
    `id`                  bigint       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `applicant`           varchar(20)  NOT NULL COMMENT '申请人',
    `applicant_id`        varchar(100) NOT NULL COMMENT '申请人id',
    `subject`             varchar(100) NOT NULL COMMENT '审批单名称',
    `serial_number`       varchar(100) NOT NULL COMMENT '流水号',
    `flow_id`             bigint       NOT NULL COMMENT '流程定义id',
    `note`                varchar(200) NOT NULL COMMENT '申请原因说明',
    `current_approver_id` varchar(100) NOT NULL COMMENT '当前审批人id',
    `current_approver`    varchar(20)  NOT NULL COMMENT '当前审批人',
    `status`              TINYINT      NOT NULL DEFAULT 0 COMMENT '状态（0：待审核 1：已撤回 2：已拒绝 3：审批通过）',
    `apply_time`          datetime(0)  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `recall_time`         datetime(0)           DEFAULT NULL COMMENT '撤回时间',
    `end_time`            datetime(0)           DEFAULT NULL COMMENT '审批完成时间',
    PRIMARY KEY (`id`),
    INDEX (`applicant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='申请表';

CREATE TABLE `approver`
(
    `id`                 bigint       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `apply_id`           bigint       NOT NULL COMMENT '审批请求id',
    `approver_id`        VARCHAR(100) NOT NULL COMMENT '审批人id',
    `approver_name`      VARCHAR(20)  NOT NULL COMMENT '审批人姓名',
    `next_approver_id`   VARCHAR(100)          DEFAULT NULL COMMENT '下一个审批人id',
    `next_approver_name` VARCHAR(20)           DEFAULT NULL COMMENT '下一个审批人姓名',
    `status`             TINYINT      NOT NULL DEFAULT 0 COMMENT '状态（0：待审核 1：审批通过 2：已拒绝 3：转移审批给别人）',
    `comment`            VARCHAR(200)          DEFAULT NULL COMMENT '审批意见',
    `approval_time`      datetime(0)  NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '审批时间',
    PRIMARY KEY (`id`),
    UNIQUE (`apply_id`, `approver_id`),
    INDEX (`apply_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='审批人表';

CREATE TABLE `todo_task`
(
    `id`        bigint      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `apply_id`  bigint(20)  NOT NULL COMMENT '申请id',
    `task_id`   VARCHAR(50) NOT NULL COMMENT '关联的待办事件ID',
    `user_id`   VARCHAR(50) NOT NULL COMMENT '收到待办消息的用户id',
    `user_name` VARCHAR(20) NOT NULL COMMENT '收到待办消息的用户姓名',
    PRIMARY KEY (`id`),
    INDEX (`apply_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='待办消息表';

CREATE TABLE `carbon_copy`
(
    `id`               bigint      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `apply_id`         bigint(20)  NOT NULL COMMENT '申请id',
    `user_id`          VARCHAR(50) NOT NULL COMMENT '抄送的用户id',
    `user_name`        VARCHAR(20) NOT NULL COMMENT '抄送用户姓名',
    `create_user_id`   VARCHAR(50) NOT NULL COMMENT '创建者的用户id',
    `create_user_name` VARCHAR(20) NOT NULL COMMENT '创建者用户姓名',
    `create_time`      datetime(0) NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '添加抄送用户的时间',
    PRIMARY KEY (`id`),
    UNIQUE (`apply_id`, `user_id`),
    INDEX (`apply_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='审批抄送表';


CREATE TABLE `approval_flow`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `flow_name`     VARCHAR(100) NOT NULL COMMENT '流程名称',
    `dept_code`     VARCHAR(20)  NOT NULL COMMENT '流程适用部门id',
    `position_id`   bigint       NOT NULL COMMENT '适用申请的岗位id',
    `max_file`      INT          NOT NULL COMMENT '最大文件数',
    `file_editable` TINYINT      NOT NULL COMMENT '上传的文件能否编辑：0：不能编辑，1：可以编辑',
    `status`        TINYINT      NOT NULL COMMENT '流程定义状态：0：不生效，1：生效',
    PRIMARY KEY (`id`),
    UNIQUE (`flow_name`, `dept_code`),
    INDEX (`dept_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='审批流程定义表';