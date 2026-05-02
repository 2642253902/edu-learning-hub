# Study Project - 校园智慧学习社区

## 项目简介
Study Project 是一个前后端分离的校园学习平台，围绕课程学习、资源浏览、社区互动、消息通知和权限管理展开。项目支持基于角色的动态菜单、学习业务管理、管理员后台维护以及 AI 聊天能力，适合教学场景下的综合演示和毕业设计展示。

当前仓库包含两个子工程：
- `study-project-barkend`：Spring Boot 后端
- `study-project-fronrend`：Vue 3 前端

说明：目录名沿用了当前仓库中的实际命名，README 也按现状记录。

## 主要功能

### 账号与权限
- 用户注册、登录、退出登录
- 忘记密码与密码重置
- 获取当前登录用户信息
- 用户管理、角色管理、菜单/路由管理
- 基于角色的动态路由注入与菜单缓存
- 顶部菜单搜索与跳转

### 教学业务
- 课程分类管理
- 课程管理
- 课程资源管理
- 学习中心与课程详情页
- 学习记录管理
- 课程评价管理

### 社区互动
- 学习小组管理
- 公共讨论
- 帖子与评论管理

### 系统能力
- 消息中心与消息通知
- 文件上传
- AI 聊天
- 后端接口统一鉴权与错误处理

## 技术栈

### 后端
- Java 17
- Spring Boot 3.5
- Spring Security
- MyBatis
- MyBatis-Plus
- MySQL
- Redis
- Spring Mail
- Spring AI

### 前端
- Vue 3
- TypeScript
- Vite
- Vue Router
- Pinia
- Element Plus
- Axios
- ECharts
- Video.js
- Vue Office

## 项目结构

```text
edu-learning-hub/
├─ study-project-barkend/
│  ├─ src/main/java/com/exampe/
│  │  ├─ auth/        # 登录、注册、账号管理
│  │  ├─ study/       # 课程、资源、学习记录、社区
│  │  └─ sys/         # 角色、路由、消息、文件、AI 聊天
│  └─ src/main/resources/
│     ├─ application.yaml
│     ├─ application-dev.yml
│     ├─ application-prod.yml
│     └─ mapper/
└─ study-project-fronrend/
   └─ src/
      ├─ components/welcome/   # 登录、注册、忘记密码
      ├─ views/community/      # 社区、小组、帖子、评论
      ├─ views/index/          # 统计仪表盘
      ├─ views/study/          # 课程、资源、学习记录
      └─ views/sys/            # 个人信息、角色、用户、消息、AI 聊天
```

## 本地运行

### 环境要求
- JDK 17
- Maven 3.9+
- Node.js 20+
- MySQL 8.x
- Redis 6+

### 后端启动
进入后端目录后启动：

```bash
cd study-project-barkend
mvn spring-boot:run -Pdev
```

如果你在 IDE 里直接运行主类，请确保资源过滤和开发环境配置生效；否则 `application.yaml` 中的环境占位符可能不会按预期替换。

### 前端启动
进入前端目录后安装依赖并启动开发服务器：

```bash
cd study-project-fronrend
npm install
npm run dev
```

前端开发环境默认连接 `http://localhost:8080`，配置见 `.env.development`。

### 构建前端

```bash
cd study-project-fronrend
npm run build
```

## 部署说明
仓库根目录下提供了简要部署参考：
- [部署.txt](部署.txt)
- [nginx.conf](nginx.conf)

前端生产环境配置文件位于 `.env.production`，后端生产配置位于 `study-project-barkend/src/main/resources/application-prod.yml`。

## 接口概览
- 认证：`/api/auth/*`
- 用户：`/api/user/*`
- 角色：`/api/role/*`
- 路由菜单：`/api/routes/*`
- 消息：`/api/message/*`
- 文件上传：`/upload/*`
- AI 聊天：`/api/chat/*`
- 课程：`/study/cloudComputingCourse/*`
- 课程资源：`/study/cloudComputingCourseResource/*`
- 课程分类：`/study/cloudComputingCourseType/*`
- 学习记录：`/study/cloudComputingStudentLearningRecord/*`
- 社区：`/api/community/*`

## 说明与注意事项
- 前端路由会根据后端返回的角色菜单树动态注入，登录后才能访问主功能区。
- 前端和后端都包含开发与生产配置，切换环境时请同步检查接口地址和数据库连接。
- 配置文件中存在本地开发参数，正式部署前建议替换为自己的环境变量或服务器配置。
- 该项目的核心展示点是“权限驱动的动态菜单 + 学习业务 + 社区互动 + 消息/AI 能力”，适合作为完整毕业设计演示。
