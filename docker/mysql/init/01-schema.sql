-- ============================================================
-- 图书管理系统 数据库结构初始化脚本
-- 由 MySQL 容器首次启动时自动执行（docker-entrypoint-initdb.d）
-- 对应 PRD 第 7 章 数据需求
-- ============================================================
CREATE DATABASE IF NOT EXISTS book_management
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE book_management;

-- 系统用户（FR-SYS-001 用户与角色权限）
CREATE TABLE sys_user (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  username        VARCHAR(50)  NOT NULL COMMENT '登录名',
  password        VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密密码',
  real_name       VARCHAR(50)  NULL     COMMENT '姓名',
  role            TINYINT      NOT NULL DEFAULT 2 COMMENT '角色：1系统管理员 2编目馆员 3流通馆员 4主管',
  status          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0启用 1停用',
  last_login_time DATETIME     NULL     COMMENT '最后登录时间',
  create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE = InnoDB COMMENT = '系统用户';

-- 读者类型（借阅规则载体，FR-READER-004 / 5.4.1 默认借阅规则）
CREATE TABLE reader_type (
  id           BIGINT        NOT NULL AUTO_INCREMENT,
  name         VARCHAR(50)   NOT NULL COMMENT '类型名称',
  max_books    INT           NOT NULL DEFAULT 5 COMMENT '可借数量上限',
  loan_days    INT           NOT NULL DEFAULT 30 COMMENT '借期（天）',
  renew_limit  INT           NOT NULL DEFAULT 1 COMMENT '可续借次数',
  fine_per_day DECIMAL(10,2) NOT NULL DEFAULT 0.20 COMMENT '滞纳金（元/册/天）',
  remark       VARCHAR(200)  NULL,
  PRIMARY KEY (id)
) ENGINE = InnoDB COMMENT = '读者类型（借阅规则）';

-- 读者档案（FR-READER-001 ~ 003）
CREATE TABLE reader (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  reader_no   VARCHAR(20)  NOT NULL COMMENT '读者证号，格式 R000001',
  name        VARCHAR(50)  NOT NULL COMMENT '姓名',
  id_card     VARCHAR(30)  NULL COMMENT '证件号',
  phone       VARCHAR(20)  NULL COMMENT '联系电话',
  dept        VARCHAR(100) NULL COMMENT '部门/单位',
  type_id     BIGINT       NOT NULL COMMENT '读者类型',
  status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0正常 1挂失 2停借 3注销',
  remark      VARCHAR(200) NULL,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_reader_no (reader_no),
  KEY idx_type (type_id),
  KEY idx_name (name)
) ENGINE = InnoDB COMMENT = '读者';

-- 书目（FR-BOOK-001 / 002 图书录入与编目）
CREATE TABLE book (
  id           BIGINT        NOT NULL AUTO_INCREMENT,
  isbn         VARCHAR(20)   NULL COMMENT '国际标准书号',
  title        VARCHAR(200)  NOT NULL COMMENT '书名',
  author       VARCHAR(100)  NULL COMMENT '作者',
  publisher    VARCHAR(100)  NULL COMMENT '出版社',
  publish_year INT           NULL COMMENT '出版年份',
  category     VARCHAR(30)   NULL COMMENT '中图法分类号',
  subject      VARCHAR(200)  NULL COMMENT '主题词',
  summary      VARCHAR(1000) NULL COMMENT '摘要',
  price        DECIMAL(10,2) NULL COMMENT '定价（元）',
  deleted      TINYINT       NOT NULL DEFAULT 0 COMMENT '软删除：0正常 1已删除',
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_title (title),
  KEY idx_isbn (isbn),
  KEY idx_category (category)
) ENGINE = InnoDB COMMENT = '书目';

-- 馆藏副本（FR-BOOK-003 / 005）
CREATE TABLE book_copy (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  book_id     BIGINT       NOT NULL COMMENT '所属书目',
  barcode     VARCHAR(30)  NOT NULL COMMENT '条码号，格式 C000001',
  location    VARCHAR(100) NULL COMMENT '馆藏位置',
  status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0在馆 1已借出 3遗失 4修复中 5已注销',
  remark      VARCHAR(200) NULL,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_barcode (barcode),
  KEY idx_book (book_id),
  KEY idx_status (status)
) ENGINE = InnoDB COMMENT = '馆藏副本';

-- 借阅记录（FR-CIRC-001 ~ 005，PRD 7.2 数据字典）
CREATE TABLE loan (
  id           BIGINT        NOT NULL AUTO_INCREMENT,
  reader_id    BIGINT        NOT NULL COMMENT '借阅读者',
  copy_id      BIGINT        NOT NULL COMMENT '借阅副本',
  borrowed_at  DATETIME      NOT NULL COMMENT '借出时间',
  due_at       DATETIME      NOT NULL COMMENT '应还时间',
  returned_at  DATETIME      NULL COMMENT '实际归还时间，NULL 表示在借',
  renew_count  INT           NOT NULL DEFAULT 0 COMMENT '已续借次数',
  overdue_days INT           NOT NULL DEFAULT 0 COMMENT '逾期天数',
  fine         DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '滞纳金（元）',
  status       TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0在借 1已还 2逾期（在借）',
  remark       VARCHAR(200)  NULL,
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_reader (reader_id),
  KEY idx_copy (copy_id),
  KEY idx_status (status),
  KEY idx_due (due_at)
) ENGINE = InnoDB COMMENT = '借阅记录';
