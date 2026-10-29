-- ============================================================
-- 图书管理系统 初始化数据（示例/演示数据）
-- 由 MySQL 容器首次启动时自动执行
-- 默认账号：admin / admin123（密码为 BCrypt 加密存储）
-- ============================================================
USE book_management;

-- 读者类型（对应 PRD 5.4.1 默认借阅规则）
INSERT INTO reader_type (id, name, max_books, loan_days, renew_limit, fine_per_day, remark) VALUES
(1, '教师/主管',   10, 60, 1, 0.20, '教师及部门主管'),
(2, '普通员工/学生', 5, 30, 1, 0.20, '普通员工与学生'),
(3, '外部人员',     2, 15, 0, 0.50, '外部合作人员，不可续借');

-- 系统用户（密码统一为 admin123）
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin',  '$2b$10$/hSpnq48ba.sCNwwWirgQ.bsFXfuugcjPGCb.iDXGz9MbG81KDcz6', '系统管理员', 1, 0),
('zhangcm', '$2b$10$/hSpnq48ba.sCNwwWirgQ.bsFXfuugcjPGCb.iDXGz9MbG81KDcz6', '张编目', 2, 0),
('limin',   '$2b$10$/hSpnq48ba.sCNwwWirgQ.bsFXfuugcjPGCb.iDXGz9MbG81KDcz6', '李敏', 3, 0);

-- 书目
INSERT INTO book (id, isbn, title, author, publisher, publish_year, category, subject, price) VALUES
(1, '9787111213826', 'Java 编程思想（第4版）',   'Bruce Eckel',      '机械工业出版社', 2007, 'TP312', 'Java; 程序设计', 108.00),
(2, '9787111253430', '算法导论（原书第3版）',     'Thomas H. Cormen', '机械工业出版社', 2013, 'TP301', '算法; 数据结构', 128.00),
(3, '9787115417305', 'Spring 实战（第4版）',      'Craig Walls',      '人民邮电出版社', 2016, 'TP312', 'Spring; Java 框架', 89.00),
(4, '9787115428073', '深入理解 Java 虚拟机（第2版）', '周志明',       '机械工业出版社', 2013, 'TP312', 'JVM; 虚拟机', 79.00),
(5, '9787508647357', '人类简史：从动物到上帝',    '尤瓦尔·赫拉利',    '中信出版社',     2014, 'K02',  '历史; 人类学', 68.00),
(6, '9787536692930', '三体（全集）',              '刘慈欣',           '重庆出版社',     2012, 'I247', '科幻; 小说', 168.00),
(7, '9787115438416', '高性能 MySQL（第3版）',     'Baron Schwartz',   '人民邮电出版社', 2013, 'TP311', 'MySQL; 数据库', 129.00),
(8, '9787115257855', 'Linux 命令行与 Shell 脚本编程大全', 'Richard Blum', '人民邮电出版社', 2012, 'TP316', 'Linux; 运维', 89.00);

-- 馆藏副本（每本书 2~3 个副本）
INSERT INTO book_copy (id, book_id, barcode, location, status) VALUES
(1,  1, 'C000001', 'A区-01架', 0),
(2,  1, 'C000002', 'A区-01架', 0),
(3,  2, 'C000003', 'A区-02架', 0),
(4,  2, 'C000004', 'A区-02架', 0),
(5,  3, 'C000005', 'A区-03架', 0),
(6,  3, 'C000006', 'A区-03架', 0),
(7,  4, 'C000007', 'A区-03架', 0),
(8,  4, 'C000008', 'A区-03架', 0),
(9,  5, 'C000009', 'B区-01架', 0),
(10, 5, 'C000010', 'B区-01架', 0),
(11, 6, 'C000011', 'B区-02架', 0),
(12, 6, 'C000012', 'B区-02架', 0),
(13, 6, 'C000013', 'B区-02架', 0),
(14, 7, 'C000014', 'A区-04架', 0),
(15, 8, 'C000015', 'A区-04架', 0);

-- 读者
INSERT INTO reader (id, reader_no, name, id_card, phone, dept, type_id, status) VALUES
(1, 'R000001', '王强', '310101199001010011', '13800000001', '研发一部', 2, 0),
(2, 'R000002', '赵静', '310101199202020022', '13800000002', '研发二部', 2, 0),
(3, 'R000003', '陈晨', '310101198503030033', '13800000003', '综合管理部', 1, 0),
(4, 'R000004', '孙洁', '310101199604040044', '13800000004', '市场部', 3, 0);

-- 借阅记录（用于演示统计与逾期场景）
INSERT INTO loan (id, reader_id, copy_id, borrowed_at, due_at, returned_at, renew_count, overdue_days, fine, status) VALUES
-- 已归还的历史借阅
(1, 1, 1, DATE_SUB(NOW(), INTERVAL 70 DAY), DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 45 DAY), 0, 0, 0.00, 1),
-- 逾期归还，产生滞纳金：逾期 5 天 × 0.20 元 = 1.00 元
(2, 1, 14, DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 0, 5, 1.00, 1),
-- 正常在借：借期 30 天
(3, 2, 3, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 25 DAY), NULL, 0, 0, 0.00, 0),
-- 逾期在借（状态 2）：滞纳金 10 天 × 0.20 元 = 2.00 元
(4, 2, 5, DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), NULL, 0, 10, 2.00, 2),
-- 已归还的历史借阅
(5, 3, 7, DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY), 0, 0, 0.00, 1);

-- 同步在借副本的物理状态
UPDATE book_copy SET status = 1 WHERE barcode IN ('C000003', 'C000005');
