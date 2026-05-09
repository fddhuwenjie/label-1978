# 学生管理系统

## How to Run

### Docker 启动（推荐）

```bash
# 在项目根目录执行
docker-compose up --build -d

# 查看运行状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

启动后访问：
- 前端：http://localhost:8081
- 后端API：http://localhost:8978/api

### 本地启动

#### 后端
1. 确保已安装 JDK 8+ 和 Maven
2. 确保 MySQL 运行在 localhost:3306
3. 创建数据库并导入初始数据：
```bash
mysql -u root -p < backend/src/main/resources/init.sql
```
4. 编译运行：
```bash
cd backend
mvn clean package
# 部署 war 包到 Tomcat
```

#### 前端
1. 确保已安装 Node.js 16+
2. 安装依赖并运行：
```bash
cd frontend
npm install
npm run dev
```

## Services

| 服务 | 端口 | 说明 |
|------|------|------|
| Frontend | 8081 | Vue.js 前端应用 |
| Backend | 8978 | Java Servlet 后端 API |
| MySQL | 3307 | 数据库服务 |

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | user | user123 |

## 题目内容

请基前后端分离架构模型开发一款学生管理系统。 
•后端：使用Java Web技术（Servlet + Filter + 数据库连接池），提供前端需要的接口。 
•前端：使用Vue.js作为前端框架，通过Axios等方式调用后端接口。 
要求至少包含以下功能： 
1）系统具有用户注册与登录功能。 
2 ）使用过滤器方法实现登录拦截，用户自动保存登录，超过20秒登录过期 
3 ）页面需实现：注册页面、登录页面、用户信息显示页面、学生列表、学生信息显示、修改密码页面等。 
4 ）管理员可以添加学生信息到学生列表，并可以对学生的信息进行增删改查。

功能要求：
1. 系统具有用户注册与登录功能
2. 使用过滤器方法实现登录拦截，用户自动保存登录，超过20秒登录过期
3. 页面需实现：注册页面、登录页面、用户信息显示页面、学生列表、学生信息显示、修改密码页面等
4. 管理员可以添加学生信息到学生列表，并可以对学生的信息进行增删改查

## 项目结构

```
├── backend/                 # 后端 Java Web 项目
│   ├── src/
│   │   └── main/
│   │       ├── java/       # Java 源代码
│   │       ├── resources/  # 配置文件和 SQL
│   │       └── webapp/     # Web 配置
│   ├── pom.xml
│   └── Dockerfile
├── frontend/               # 前端 Vue.js 项目
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

## 技术栈

- 后端：Java Servlet + Filter + HikariCP 连接池 + MySQL
- 前端：Vue 3 + Vue Router + Axios + Element Plus
- 部署：Docker + Docker Compose
