# syntax=docker/dockerfile:1

# ---------- 构建阶段：Maven 编译打包 ----------
FROM maven:3.8.6-openjdk-8 AS builder
WORKDIR /build

# 先复制 settings.xml（阿里云 Maven 镜像）和 pom.xml，充分利用 Docker 层缓存
COPY settings.xml /root/.m2/settings.xml
COPY pom.xml ./
RUN mvn -B dependency:go-offline || true

# 复制源码并打包
COPY src ./src
RUN mvn -B package -DskipTests

# ---------- 运行阶段：Tomcat 9 + JDK8 ----------
FROM tomcat:9.0-jdk8-temurin
LABEL maintainer="jalen.zheng@quectel.com" project="book-management" version="1.0.0"

# 清理 Tomcat 自带示例应用，将本系统部署为 ROOT（根路径访问）
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=builder /build/target/book-management.war /usr/local/tomcat/webapps/ROOT.war

ENV TZ=Asia/Shanghai
EXPOSE 8080
CMD ["catalina.sh", "run"]
