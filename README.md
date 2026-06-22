# go-camp-management

围棋训练营管理系统，包含后端服务与多端前端应用。

## 项目结构

| 目录 | 说明 |
|------|------|
| `go-camp-server` | Spring Boot 后端服务 |
| `go-camp-admin-pc` | PC 管理端 |
| `go-camp-admin-mobile` | 移动端管理端 |
| `go-camp-coach` | 教练端 |
| `go-camp-parent` | 家长端 |
| `go-camp-db` | 数据库脚本 |

## 快速开始

### 后端

```bash
cd go-camp-server
# 配置 application-dev.yml 中的数据库连接
mvn spring-boot:run
```

### 前端

各前端子项目目录下：

```bash
npm install
npm run dev
```
