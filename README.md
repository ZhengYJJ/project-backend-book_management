# 图书管理系统（Book Management System）— 纯后端服务

基于 **Spring + SpringMVC + MyBatis + MySQL** 的图书管理系统**纯后端服务**：对外仅提供 JSON REST API，**不包含任何前端页面**（无 HTML/JSP/Vue 等），可对接任意前端框架或供其他系统直接调用。配套 Docker 一键部署。需求依据见《图书管理系统PRD.md》。

## 技术栈

| 层 | 技术 | 版本 |
| --- | --- | --- |
| Web 层 | Spring MVC | 5.3.39 |
| 业务层 | Spring（IoC / TX / Task） | 5.3.39 |
| 持久层 | MyBatis + mybatis-spring + Druid | 3.5.16 / 2.1.2 / 1.2.23 |
| 数据库 | MySQL | 8.0 |
| 构建 | Maven（WAR，Java 8） | — |
| 运行 | Tomcat 9（Docker 镜像 `tomcat:9.0-jdk8-temurin`） | 9.0.x |
| 部署 | Docker + Docker Compose（多阶段构建） | — |

## 功能覆盖（对照 PRD）

| 模块 | 已实现（对应 PRD 编号） | 后续迭代 |
| --- | --- | --- |
| 认证 | 会话登录/登出、修改密码、BCrypt、登录拦截（FR-SYS-006） | 双因素认证、登录失败锁定 |
| 书目管理 | 录入/编目/ISBN 查重/软删除（FR-BOOK-001/002/007） | ISBN 联网自动补全 |
| 馆藏副本 | 批量登记、自动条码、状态/位置管理（FR-BOOK-003~006） | 条码标签打印、RFID |
| 读者管理 | 注册/证号自动生成/挂失停借注销/分级规则（FR-READER-001~005） | 自助申请审核流 |
| 流通 | 借/还/续借/借阅上限与逾期拦截/滞纳金/每日逾期扫描（FR-CIRC-001~005） | 预约、赔偿单、减免审批 |
| 检索 | 书目/副本/借阅组合分页检索（FR-SEARCH-001/002） | 读者 OPAC 门户 |
| 统计 | 借阅排行、月度借还统计（FR-RPT-001/002） | 馆藏结构、导出 Excel |
| 系统 | 统一返回结构、全局异常、日志（logback） | 细粒度 RBAC、参数在线配置、备份 |

## 目录结构

```
book_management/
├── pom.xml                          # Maven 配置（WAR 打包）
├── Dockerfile                       # 多阶段构建：maven 编译 -> tomcat 运行
├── docker-compose.yml               # app + mysql 编排
├── settings.xml                     # 阿里云 Maven 镜像（构建加速）
├── .env.example                     # 环境变量模板（数据库密码）
├── docker/mysql/init/
│   ├── 01-schema.sql                # 建库建表（首次启动自动执行）
│   └── 02-data.sql                  # 演示数据 + 默认账号
└── src/main/
    ├── java/com/library/
    │   ├── common/                  # Result / PageResult / 异常 / 常量
    │   ├── entity/                  # Book / BookCopy / Reader / ReaderType / Loan / SysUser
    │   ├── mapper/                  # MyBatis Mapper 接口
    │   ├── service/ (+impl/)        # 业务逻辑（事务边界）
    │   ├── controller/              # REST 接口
    │   ├── interceptor/             # 登录拦截器
    │   └── task/                    # 逾期扫描定时任务
    ├── resources/
    │   ├── spring/                  # applicationContext.xml / spring-mvc.xml
    │   ├── mybatis/mapper/          # Mapper XML
    │   ├── jdbc.properties          # 数据源（可被 -D 系统属性覆盖）
    │   └── logback.xml
    └── webapp/WEB-INF/web.xml
```

## 快速部署（阿里云服务器）

### 1. 准备服务器

```bash
# 安装 Docker（以 CentOS/Alibaba Cloud Linux 为例）
curl -fsSL https://get.docker.com | bash
systemctl enable --now docker

# 安装 compose 插件（新版 docker 已内置；若无则单独安装）
# 并建议配置镜像加速：阿里云控制台 -> 容器镜像服务 -> 镜像加速器
# /etc/docker/daemon.json 中添加 registry-mirrors 后 systemctl restart docker
```

### 2. 上传代码并启动

```bash
# 方式一：git（建议先推到自己的私有仓库）
git clone <你的仓库地址> book_management && cd book_management

# 方式二：scp 上传整个目录
# scp -r D:/zyj/backend/book_management root@<服务器IP>:/opt/

# 配置数据库密码（可选，默认 Book@123456）
cp .env.example .env && vi .env

# 一键构建并启动（首次构建需下载依赖，约 3~10 分钟）
docker compose up -d --build

# 查看状态与日志
docker compose ps
docker compose logs -f app
```

### 3. 开放端口

阿里云控制台 → ECS → 安全组 → 添加入方向规则：放行 **TCP 8080**（来源建议限定你的 IP）。

MySQL 端口仅绑定在服务器本机（127.0.0.1:3306），未暴露公网；如需远程管理数据库，修改 docker-compose.yml 中的 ports 后重启。

### 4. 验证

```bash
curl http://<服务器IP>:8080/
# {"code":200,"msg":"success","data":{"app":"图书管理系统...","status":"UP",...}}
```

默认账号（首次启动初始化，密码均为 `admin123`）：

| 账号 | 角色 |
| --- | --- |
| admin | 系统管理员 |
| zhangcm | 编目馆员 |
| limin | 流通馆员 |

> 上线后请立即调用 `PUT /api/auth/password` 修改默认密码。

## API 一览

统一返回 `{code, msg, data}`：200 成功 / 400 业务失败 / 401 未登录 / 500 系统异常。除登录外均需 Session（Cookie 记录会话）。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录 `{"username":"admin","password":"admin123"}` |
| POST | `/api/auth/logout` | 登出 |
| GET | `/api/auth/me` | 当前用户 |
| PUT | `/api/auth/password` | 修改密码 `{"oldPassword":"...","newPassword":"..."}` |
| GET | `/api/books?keyword=&category=&pageNum=&pageSize=` | 书目分页检索（含在馆数统计） |
| GET | `/api/books/{id}` | 书目详情 |
| POST | `/api/books` | 新建书目 |
| PUT | `/api/books/{id}` | 更新书目 |
| DELETE | `/api/books/{id}` | 删除书目（有副本时拒绝） |
| GET | `/api/books/{bookId}/copies?status=` | 某书目的副本列表 |
| POST | `/api/books/{bookId}/copies` | 批量登记副本 `{"count":3,"location":"A区-01架"}` |
| GET | `/api/copies?bookId=&status=&barcode=` | 全馆副本检索 |
| PUT | `/api/copies/{id}` | 更新副本状态/位置/备注 |
| GET | `/api/copies/barcode/{barcode}` | 按条码查副本 |
| GET | `/api/readers?keyword=&status=` | 读者分页 |
| GET | `/api/readers/{id}` | 读者详情（含在借数） |
| POST | `/api/readers` | 新建读者（自动生成证号） |
| PUT | `/api/readers/{id}` | 更新读者 |
| PUT | `/api/readers/{id}/status` | 挂失/停借/注销 `{"status":1}` |
| GET | `/api/readers/types` | 读者类型（借阅规则） |
| POST | `/api/loans/borrow` | 借书 `{"readerNo":"R000001","barcode":"C000001"}` |
| POST | `/api/loans/return` | 还书 `{"barcode":"C000001"}`（自动算滞纳金） |
| POST | `/api/loans/{id}/renew` | 续借 |
| GET | `/api/loans?readerId=&status=&keyword=` | 借阅记录分页 |
| GET | `/api/loans/active?readerId=` | 读者在借列表 |
| POST | `/api/loans/scan-overdue` | 手动触发逾期扫描 |
| GET | `/api/stats/top-borrowed?days=&limit=` | 借阅排行 |
| GET | `/api/stats/monthly?months=` | 月度借还统计 |

### 快速体验（curl）

```bash
# 1. 登录（保存 Cookie）
curl -c cookie.txt -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  http://localhost:8080/api/auth/login

# 2. 借书：读者 R000001 借 C000001（应还日期按读者类型借期计算）
curl -b cookie.txt -H "Content-Type: application/json" \
  -d '{"readerNo":"R000001","barcode":"C000001"}' \
  http://localhost:8080/api/loans/borrow

# 3. 还书（如逾期自动计算滞纳金）
curl -b cookie.txt -H "Content-Type: application/json" \
  -d '{"barcode":"C000001"}' \
  http://localhost:8080/api/loans/return

# 4. 借阅排行
curl -b cookie.txt "http://localhost:8080/api/stats/top-borrowed?limit=5"
```

## 本地开发（不经 Docker）

1. 本地 MySQL 8 导入 `docker/mysql/init/` 下两个 SQL 脚本；
2. 修改 `src/main/resources/jdbc.properties` 为本地连接；
3. IDE 中配置 Tomcat 9（Java 8），部署 war 或直接用 maven 插件运行；
4. 日志级别可在 `logback.xml` 调整（默认 `com.library.mapper` 为 DEBUG，可看到 SQL）。

## 运维备忘

- **数据持久化**：MySQL 数据在 docker 卷 `mysql-data` 中，`docker compose down` 不会丢数据（`down -v` 会）；
- **重新初始化库**：`docker compose down -v && docker compose up -d --build`；
- **改数据库密码**：改 `.env` 后需 `docker compose down -v` 重建（或进容器 ALTER 用户密码并同步 `.env`）；
- **日志**：`docker compose logs -f app`，容器内 `/usr/local/tomcat/logs/` 亦有滚动日志；
- **逾期任务**：每日 01:00 自动执行（时区 Asia/Shanghai），可用 `POST /api/loans/scan-overdue` 手动触发。

## 已知边界 / TODO

- 预约（FR-CIRC-004）、赔偿单（FR-CIRC-006）、通知（FR-SYS-005）未实现；
- 角色目前仅登录校验，未做接口级细粒度 RBAC（FR-SYS-001 的操作粒度授权）；
- 借阅规则改动直接改 `reader_type` 表即可实时生效（PRD FR-SYS-002 部分覆盖）；
- 滞纳金上限（不超过书价）未实现，逾期后按天数累计；
- 本项目定位为**纯后端服务**（by design）：所有接口返回 JSON，无前端页面，可直接对接 Vue/React/小程序等前端，或作为独立 API 服务供其他系统调用。
