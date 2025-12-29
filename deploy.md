# 毕业设计选题系统部署步骤

## 1. 环境准备

### 1.1 硬件需求

| 组件 | 最低配置 | 推荐配置 |
|------|---------|---------|
| CPU | 2核 | 4核 |
| 内存 | 4GB | 8GB |
| 硬盘 | 50GB | 100GB |
| 网络 | 100Mbps | 1Gbps |

### 1.2 软件需求

| 软件 | 版本 | 用途 |
|------|------|------|
| JDK | 1.8 | 运行后端Spring Boot应用 |
| MySQL | 8.0+ | 数据库服务 |
| Node.js | 16.0+ | 运行前端Vue应用和构建 |
| Maven | 3.6+ | 构建后端Spring Boot应用 |
| Git | 2.0+ | 版本控制（可选） |

### 1.3 系统支持

- Windows Server 2016/2019/2022
- Ubuntu 18.04/20.04/22.04
- CentOS 7/8/9
- macOS 10.15+

## 2. 数据库部署

### 2.1 安装MySQL

#### Windows系统
1. 下载MySQL安装包：[MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
2. 运行安装程序，选择"Custom"安装类型
3. 选择需要安装的组件，建议包括MySQL Server、MySQL Workbench
4. 设置root密码，添加MySQL到系统环境变量
5. 完成安装后，启动MySQL服务

#### Linux系统（以Ubuntu为例）
```bash
# 更新包列表
sudo apt update

# 安装MySQL服务器
sudo apt install mysql-server

# 启动MySQL服务
sudo systemctl start mysql

# 设置MySQL服务开机自启
sudo systemctl enable mysql

# 安全配置（设置root密码、移除匿名用户等）
sudo mysql_secure_installation
```

### 2.2 创建数据库和用户

1. 登录MySQL
```bash
mysql -u root -p
```

2. 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS graduation_topic_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 创建用户并授权
```sql
-- 创建用户（替换your_username和your_password为实际用户名和密码）
CREATE USER 'your_username'@'localhost' IDENTIFIED BY 'your_password';

-- 授权用户访问数据库
GRANT ALL PRIVILEGES ON graduation_topic_system.* TO 'your_username'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;
```

### 2.3 导入数据库表结构

1. 将项目中的`database.sql`文件复制到服务器
2. 执行SQL文件导入表结构
```bash
mysql -u your_username -p graduation_topic_system < database.sql
```

## 3. 后端部署

### 3.1 配置文件修改

1. 进入后端项目目录
```bash
cd backend
```

2. 修改`src/main/resources/application.yml`文件
```yaml
# 数据库连接配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/graduation_topic_system?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username  # 替换为实际用户名
    password: your_password  # 替换为实际密码

# 服务器配置（可选，根据需要修改端口）
server:
  port: 8080

# JWT配置（可选，建议修改密钥）
jwt:
  secret: your_jwt_secret_key  # 替换为实际密钥
```

### 3.2 构建项目

```bash
# 使用Maven构建项目
mvn clean package -DskipTests
```

构建成功后，会在`target`目录下生成`topic-system-backend-1.0.0.jar`文件。

### 3.3 运行后端服务

#### 直接运行（测试环境）
```bash
java -jar target/topic-system-backend-1.0.0.jar
```

#### 使用systemd管理（生产环境，Linux系统）

1. 创建systemd服务文件
```bash
sudo vim /etc/systemd/system/topic-system-backend.service
```

2. 添加以下内容
```ini
[Unit]
Description=Graduation Topic System Backend
After=syslog.target network.target

[Service]
Type=simple
User=root
WorkingDirectory=/path/to/backend  # 替换为实际后端项目目录
ExecStart=/usr/bin/java -jar /path/to/backend/target/topic-system-backend-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

3. 启动服务
```bash
# 重新加载systemd配置
sudo systemctl daemon-reload

# 启动服务
sudo systemctl start topic-system-backend

# 设置开机自启
sudo systemctl enable topic-system-backend

# 查看服务状态
sudo systemctl status topic-system-backend
```

#### 使用Docker部署（推荐，跨平台）

1. 创建Dockerfile
```dockerfile
FROM openjdk:8-jre-alpine
WORKDIR /app
COPY target/topic-system-backend-1.0.0.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

2. 构建Docker镜像
```bash
docker build -t topic-system-backend:1.0.0 .
```

3. 运行Docker容器
```bash
docker run -d -p 8080:8080 --name topic-system-backend \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/graduation_topic_system?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai" \
  -e SPRING_DATASOURCE_USERNAME="your_username" \
  -e SPRING_DATASOURCE_PASSWORD="your_password" \
  topic-system-backend:1.0.0
```

## 4. 前端部署

### 4.1 配置文件修改

1. 进入前端项目目录
```bash
cd frontend
```

2. 修改`.env.production`文件（如果不存在则创建）
```env
# 生产环境配置
VITE_API_BASE_URL=http://your-backend-server:8080/api  # 替换为实际后端服务器地址
```

### 4.2 安装依赖

```bash
npm install
```

### 4.3 构建项目

```bash
npm run build
```

构建成功后，会在`dist`目录下生成静态文件。

### 4.4 部署前端静态文件

#### 使用Nginx部署（推荐）

1. 安装Nginx
```bash
# Ubuntu
sudo apt install nginx

# CentOS 7
sudo yum install nginx

# CentOS 8
sudo dnf install nginx
```

2. 配置Nginx
```bash
sudo vim /etc/nginx/conf.d/topic-system-frontend.conf
```

3. 添加以下内容
```nginx
server {
    listen 80;
    server_name your-domain.com;  # 替换为实际域名或IP地址
    root /path/to/frontend/dist;  # 替换为实际前端dist目录
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # 代理API请求到后端
    location /api {
        proxy_pass http://localhost:8080/api;  # 替换为实际后端地址
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 30d;
        add_header Cache-Control "public, no-transform";
    }
}
```

4. 检查Nginx配置
```bash
sudo nginx -t
```

5. 重启Nginx服务
```bash
# Ubuntu/CentOS 8
sudo systemctl restart nginx

# CentOS 7
sudo systemctl restart nginx
```

6. 设置Nginx开机自启
```bash
sudo systemctl enable nginx
```

#### 使用Docker部署（可选）

1. 创建Nginx配置文件`nginx.conf`
```nginx
worker_processes 1;

events {
    worker_connections 1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;

    server {
        listen       80;
        server_name  localhost;
        root   /usr/share/nginx/html;
        index  index.html;

        location / {
            try_files $uri $uri/ /index.html;
        }

        location /api {
            proxy_pass http://your-backend-server:8080/api;  # 替换为实际后端地址
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

2. 创建Dockerfile
```dockerfile
FROM nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY dist/ /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

3. 构建Docker镜像
```bash
docker build -t topic-system-frontend:1.0.0 .
```

4. 运行Docker容器
```bash
docker run -d -p 80:80 --name topic-system-frontend topic-system-frontend:1.0.0
```

## 5. 系统启动与验证

### 5.1 启动顺序

1. 启动MySQL数据库服务
2. 启动后端Spring Boot应用
3. 启动前端Nginx服务

### 5.2 验证部署

1. **验证数据库连接**：检查后端应用日志，确认是否成功连接到数据库
2. **验证后端API**：访问`http://your-backend-server:8080/api/swagger-ui/`，查看Swagger文档是否正常显示
3. **验证前端访问**：在浏览器中访问`http://your-frontend-server`，确认登录页面是否正常显示
4. **验证功能完整性**：
   - 使用管理员账号登录（默认账号：admin，密码：123456）
   - 测试用户管理、选题管理、进度管理等功能
   - 退出登录，使用普通用户账号登录，测试选题和进度管理功能

## 6. 系统监控与维护

### 6.1 日志管理

#### 后端日志
- 直接运行：日志输出到控制台
- systemd管理：使用`journalctl -u topic-system-backend -f`查看实时日志
- Docker部署：使用`docker logs -f topic-system-backend`查看实时日志

#### 前端日志
- Nginx访问日志：`/var/log/nginx/access.log`
- Nginx错误日志：`/var/log/nginx/error.log`

### 6.2 数据库维护

1. **定期备份数据库**
```bash
# 备份整个数据库
mysqldump -u your_username -p graduation_topic_system > backup_$(date +%Y%m%d).sql

# 压缩备份文件
gzip backup_$(date +%Y%m%d).sql
```

2. **定期优化数据库**
```sql
-- 优化表
OPTIMIZE TABLE user, topic, user_topic, progress;
```

3. **清理过期数据**（根据实际需求）
```sql
-- 删除30天前的进度记录（示例）
DELETE FROM progress WHERE progress_date < DATE_SUB(NOW(), INTERVAL 30 DAY);
```

### 6.3 常见问题排查

| 问题 | 可能原因 | 解决方案 |
|------|---------|---------|
| 前端无法访问后端API | 网络防火墙限制、后端服务未启动、API地址配置错误 | 检查防火墙设置、检查后端服务状态、检查前端API地址配置 |
| 数据库连接失败 | 数据库服务未启动、连接配置错误、用户权限问题 | 启动数据库服务、检查连接配置、检查用户权限 |
| 后端启动失败 | 端口被占用、依赖缺失、配置错误 | 检查端口占用情况、检查依赖、检查配置文件 |
| 前端页面显示异常 | 静态资源未正确部署、Nginx配置错误、浏览器缓存问题 | 检查静态资源部署、检查Nginx配置、清除浏览器缓存 |

## 7. 升级与更新

### 7.1 后端升级

1. 停止当前后端服务
2. 备份数据库
3. 更新代码并重新构建
4. 替换旧的jar包并启动服务
5. 验证功能是否正常

### 7.2 前端升级

1. 备份当前前端静态文件
2. 更新代码并重新构建
3. 替换旧的静态文件
4. 清除浏览器缓存或强制刷新页面
5. 验证功能是否正常

## 8. 安全加固

### 8.1 数据库安全

1. 定期更新MySQL版本
2. 使用强密码
3. 限制数据库用户的访问主机
4. 关闭不必要的MySQL服务和端口
5. 定期审计数据库权限

### 8.2 后端安全

1. 定期更新依赖库版本
2. 启用HTTPS（生产环境）
3. 配置合适的CORS策略
4. 实现请求限流和熔断机制
5. 定期审计API访问日志

### 8.3 前端安全

1. 启用HTTPS（生产环境）
2. 实现内容安全策略（CSP）
3. 避免在前端存储敏感信息
4. 对用户输入进行严格验证
5. 定期更新前端依赖库

### 8.4 服务器安全

1. 定期更新操作系统和软件
2. 配置防火墙规则，只开放必要端口
3. 使用SSH密钥登录，禁用密码登录
4. 配置入侵检测系统（IDS）
5. 定期进行安全扫描和渗透测试

## 9. 常见命令参考

### 9.1 MySQL命令

| 命令 | 用途 |
|------|------|
| `mysql -u root -p` | 登录MySQL |
| `show databases;` | 查看所有数据库 |
| `use graduation_topic_system;` | 切换到目标数据库 |
| `show tables;` | 查看数据库中的表 |
| `desc table_name;` | 查看表结构 |
| `select * from table_name;` | 查询表中所有数据 |

### 9.2 后端命令

| 命令 | 用途 |
|------|------|
| `mvn clean package -DskipTests` | 构建后端项目 |
| `java -jar target/topic-system-backend-1.0.0.jar` | 运行后端服务 |
| `systemctl start topic-system-backend` | 启动后端服务（systemd） |
| `systemctl stop topic-system-backend` | 停止后端服务（systemd） |
| `systemctl status topic-system-backend` | 查看后端服务状态（systemd） |

### 9.3 前端命令

| 命令 | 用途 |
|------|------|
| `npm install` | 安装前端依赖 |
| `npm run dev` | 开发模式运行前端 |
| `npm run build` | 构建前端项目 |
| `systemctl start nginx` | 启动Nginx服务 |
| `systemctl stop nginx` | 停止Nginx服务 |
| `systemctl status nginx` | 查看Nginx服务状态 |

## 10. 联系方式

如有部署问题或系统故障，请联系系统管理员或开发人员。

---

**部署完成后，请及时修改默认账号密码，确保系统安全！** 
