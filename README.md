# 智慧校园管理平台

## 项目简介

智慧校园管理平台是一个集教务管理、信息发布、在线学习于一体的综合性校园管理系统。系统支持学生、教师、管理员三种角色，提供选课、成绩管理、课表查询、通知公告、作业管理等核心功能。

## 技术栈

### 后端技术
- **Spring Boot 2.7.18** - 基础框架
- **MyBatis Plus 3.5.3** - ORM框架
- **MySQL 8.0** - 数据库
- **Redis** - 缓存
- **JWT** - 身份认证
- **Lombok** - 简化代码
- **Hutool** - 工具类库

### 前端技术
- **Vue 3** - 前端框架
- **Vue Router 4** - 路由管理
- **Pinia** - 状态管理
- **Element Plus** - UI组件库
- **Axios** - HTTP客户端
- **Vite** - 构建工具
- **Sass** - CSS预处理器

## 项目结构

```
bishe/
├── smart-campus-backend/      # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/smartcampus/
│   │   │   │   ├── common/           # 公共类
│   │   │   │   ├── config/           # 配置类
│   │   │   │   ├── controller/       # 控制器
│   │   │   │   ├── service/          # 服务层
│   │   │   │   ├── mapper/           # 数据访问层
│   │   │   │   ├── entity/           # 实体类
│   │   │   │   ├── dto/              # 数据传输对象
│   │   │   │   ├── vo/               # 视图对象
│   │   │   │   ├── utils/            # 工具类
│   │   │   │   └── exception/        # 异常处理
│   │   │   └── resources/
│   │   │       ├── mapper/           # MyBatis XML
│   │   │       └── application.yml   # 配置文件
│   │   └── test/                     # 测试代码
│   └── pom.xml                       # Maven配置
│
├── smart-campus-frontend/     # 前端项目
│   ├── src/
│   │   ├── api/                      # API接口
│   │   ├── assets/                   # 静态资源
│   │   ├── components/               # 公共组件
│   │   ├── views/                    # 页面视图
│   │   ├── router/                   # 路由配置
│   │   ├── store/                    # 状态管理
│   │   ├── utils/                    # 工具类
│   │   ├── styles/                   # 样式文件
│   │   ├── App.vue                   # 根组件
│   │   └── main.js                   # 入口文件
│   ├── public/                       # 公共资源
│   ├── package.json                  # 依赖配置
│   └── vite.config.js                # Vite配置
│
├── database.sql                # 数据库建表脚本
├── API接口文档.md              # API接口文档
├── 智慧校园平台需求文档.md     # 需求文档
└── README.md                   # 项目说明
```

## 环境要求

### 必需环境
- **JDK 1.8+** - Java开发环境
- **Maven 3.6+** - 项目构建工具
- **MySQL 8.0+** - 数据库
- **Node.js 16+** - 前端运行环境
- **npm 或 yarn** - 前端包管理工具

### 可选环境
- **Redis** - 缓存服务（可选，不配置也能运行）
- **Git** - 版本控制工具

## 快速开始

### 1. 克隆项目

```bash
# 如果使用Git
git clone <repository-url>
cd bishe

# 或者直接解压项目文件到本地
```

### 2. 数据库配置

#### 2.1 创建数据库

```bash
# 登录MySQL
mysql -u root -p

# 执行建表脚本
source database.sql
```

或者使用MySQL客户端工具（如Navicat、DBeaver）导入 `database.sql` 文件。

#### 2.2 默认账号

数据库初始化后会自动创建以下测试账号：

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 系统管理员 |
| 学生 | 2024001 | 123456 | 测试学生账号 |
| 教师 | T001 | 123456 | 测试教师账号 |

### 3. 后端配置与运行

#### 3.1 修改配置文件

编辑 `smart-campus-backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_campus?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root        # 修改为你的MySQL用户名
    password: 123456      # 修改为你的MySQL密码

  redis:
    host: localhost       # Redis地址（如未安装Redis可暂时忽略）
    port: 6379
    password:             # Redis密码（如无密码留空）
```

#### 3.2 安装依赖

```bash
cd smart-campus-backend
mvn clean install
```

#### 3.3 运行后端

**方式一：使用Maven命令**
```bash
mvn spring-boot:run
```

**方式二：使用IDE运行**
- 使用IDEA或Eclipse打开后端项目
- 找到 `SmartCampusApplication.java`
- 右键运行主方法

启动成功后，后端服务将运行在 `http://localhost:8080`

### 4. 前端配置与运行

#### 4.1 安装依赖

```bash
cd smart-campus-frontend
npm install
# 或使用 yarn
yarn install
```

#### 4.2 运行前端

```bash
npm run dev
# 或使用 yarn
yarn dev
```

启动成功后，前端将运行在 `http://localhost:3000`，浏览器会自动打开。

#### 4.3 构建生产版本

```bash
npm run build
# 或使用 yarn
yarn build
```

构建完成后，生成的文件在 `dist` 目录下。

## 功能模块

### 用户管理
- ✅ 用户登录/退出
- ✅ 个人信息管理
- ✅ 密码修改
- ✅ 用户管理（管理员）

### 教务管理
- ✅ 课程管理（CRUD）
- ✅ 选课系统（时间冲突检测、容量限制）
- ✅ 成绩管理（录入、查询、统计）
- ✅ 课表查询（周视图、日视图）

### 信息发布
- ✅ 通知公告（发布、查看、已读标记）
- ✅ 新闻管理（分类、浏览、搜索）

### 在线学习
- ✅ 课程资源管理（上传、下载、分类）
- ✅ 作业管理（发布、提交、批改）

## 访问系统

1. 确保后端服务已启动（端口8080）
2. 确保前端服务已启动（端口3000）
3. 浏览器访问：`http://localhost:3000`
4. 使用默认账号登录系统

## 注意事项

### 数据库相关
- 确保MySQL服务已启动
- 数据库字符集建议使用 `utf8mb4`
- 首次运行前必须执行 `database.sql` 建表脚本
- 默认密码已使用BCrypt加密，请勿直接修改数据库密码字段

### Redis相关
- Redis为可选配置，如未安装可在配置文件中注释相关配置
- 如不使用Redis，部分缓存功能将不可用，但不影响核心功能

### 端口占用
- 后端默认端口：8080
- 前端默认端口：3000
- 如端口被占用，可在配置文件中修改

### 跨域问题
- 前端已配置代理，开发环境下无需额外配置
- 生产环境部署时需配置Nginx反向代理

## 常见问题

### 1. 后端启动失败

**问题：** 数据库连接失败
```
Error: Could not connect to database
```

**解决方案：**
- 检查MySQL服务是否启动
- 确认 `application.yml` 中的数据库配置是否正确
- 确认数据库 `smart_campus` 是否已创建

### 2. 前端启动失败

**问题：** 依赖安装失败
```
npm ERR! code ECONNREFUSED
```

**解决方案：**
- 切换npm镜像源：`npm config set registry https://registry.npmmirror.com`
- 清除缓存：`npm cache clean --force`
- 重新安装：`npm install`

### 3. 登录失败

**问题：** 用户名或密码错误

**解决方案：**
- 确认是否已执行数据库初始化脚本
- 使用默认账号：admin / admin123
- 检查后端控制台是否有错误日志

### 4. 接口请求失败

**问题：** 前端无法访问后端接口

**解决方案：**
- 确认后端服务是否正常启动（端口8080）
- 检查浏览器控制台Network标签查看请求详情
- 确认前端代理配置是否正确（vite.config.js）

## 开发说明

### API接口文档
详细的API接口文档请查看项目根目录下的 `API接口文档.md`

### 需求文档
完整的需求文档请查看项目根目录下的 `智慧校园平台需求文档.md`

### 代码规范
- 后端遵循阿里巴巴Java开发规范
- 前端遵循Vue3官方风格指南
- 使用统一的代码格式化工具

## 项目文档

- 📄 [需求文档](./智慧校园平台需求文档.md)
- 📄 [API接口文档](./API接口文档.md)
- 📄 [项目结构说明](./项目结构说明.md)
- 📄 [数据库脚本](./database.sql)

## 联系方式

如有问题或建议，欢迎联系。

---

**版本：** v1.0.0
**更新日期：** 2026-02-09
