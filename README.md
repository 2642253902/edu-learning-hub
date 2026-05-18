# Study Project - 校园智慧学习社区

## 项目简介
Study Project 是一个前后端分离的校园智慧学习社区，重点覆盖课程管理、课程资源、学习记录、课程评价、学习小组、公共讨论、消息通知和 AI 聊天等典型教学场景。项目将权限体系、动态菜单、业务后台和学习端页面整合在一起，适合做毕业设计展示或课程项目演示。

仓库包含两个子工程：
- [study-project-barkend](study-project-barkend)：Spring Boot 后端，提供认证授权、课程管理、社区互动、消息通知、文件上传和 AI 聊天接口。
- [study-project-fronrend](study-project-fronrend)：Vue 3 前端，负责登录注册、动态路由注入、业务页面渲染和管理后台交互。

## 功能概览

### 账号与权限
- 用户注册、登录、退出登录
- 忘记密码与密码重置
- 当前登录用户信息获取
- 用户管理、角色管理、角色-菜单路由关系管理
- 后端返回角色权限树，前端动态注入路由和菜单
- 登录状态失效后的统一拦截与自动回跳

### 教学业务
- 课程分类管理
- 课程管理
- 课程资源管理
- 学习中心、课程详情和准备学习页
- 学习记录管理
- 课程评价和资源评价

### 社区互动
- 学习小组管理
- 小组成员管理
- 公共讨论与帖子管理
- 帖子评论管理

### 系统能力
- 消息中心与消息通知
- 文件上传
- AI 聊天
- 统计看板

## 技术栈

### 后端
- Java 17
- Spring Boot 3.5.13
- Spring Security
- MyBatis
- MyBatis-Plus
- MySQL
- Redis
- Spring Mail
- Spring AI
- Lombok

### 前端
- Vue 3
- TypeScript
- Vite 8
- Vue Router
- Pinia
- Element Plus
- Axios
- ECharts
- Video.js
- Vue Office
- Less
- 自动导入与组件自动注册

## 项目结构

```text
edu-learning-hub/
├─ study-project-barkend/
│  ├─ src/main/java/com/exampe/
│  │  ├─ auth/        # 登录、注册、找回密码、账号管理
│  │  ├─ study/       # 课程、资源、学习记录、社区、评价
│  │  └─ sys/         # 角色、路由、消息、文件上传、AI 聊天
│  └─ src/main/resources/
│     ├─ application.yaml
│     ├─ application-dev.yml
│     ├─ application-prod.yml
│     └─ mapper/
└─ study-project-fronrend/
   └─ src/
      ├─ components/welcome/   # 登录、注册、忘记密码
      ├─ views/community/      # 小组、帖子、评论
      ├─ views/index/          # 统计看板
      ├─ views/study/          # 课程、资源、学习记录、评价
      └─ views/sys/            # 个人信息、角色、用户、消息、AI 聊天
```

## 前后端对应关系

### 后端接口
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

### 前端实现
- 登录页、注册页、忘记密码页位于 [study-project-fronrend/src/components/welcome](study-project-fronrend/src/components/welcome)
- 路由守卫和动态菜单注入位于 [study-project-fronrend/src/router/index.ts](study-project-fronrend/src/router/index.ts)
- 菜单缓存与本地恢复位于 [study-project-fronrend/src/stores/menu.ts](study-project-fronrend/src/stores/menu.ts)
- 统一请求与鉴权失效处理位于 [study-project-fronrend/src/net/index.ts](study-project-fronrend/src/net/index.ts)

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

如果直接在 IDE 中运行主类，请确认 Maven 资源过滤和 `dev` 配置已生效；项目主配置会根据环境占位符加载对应的 profile。

### 前端启动
进入前端目录后安装依赖并启动开发服务器：

```bash
cd study-project-fronrend
npm install
npm run dev
```

前端开发环境默认连接后端本地地址，配置见 [study-project-fronrend/.env.development](study-project-fronrend/.env.development)。

### 前端构建

```bash
cd study-project-fronrend
npm run build
```

## 部署说明
仓库根目录下提供了简要部署参考：
- [部署.txt](部署.txt)
- [nginx.conf](nginx.conf)

生产环境配置分别位于 [study-project-fronrend/.env.production](study-project-fronrend/.env.production) 和 [study-project-barkend/src/main/resources/application-prod.yml](study-project-barkend/src/main/resources/application-prod.yml)。

## 项目亮点
- 动态路由由后端权限树驱动，前端只负责注入和渲染。
- 课程、资源、学习记录、评价、小组、帖子、评论构成完整教学闭环。
- 消息中心、文件上传和 AI 聊天补足系统功能展示。
- 开发和生产配置分离，便于本地调试和上线部署。

## 说明与注意事项
- 后端开发环境会读取数据库、Redis、邮件和 AI 配置，正式部署前请替换为自己的参数。
- 前端会在登录后缓存菜单树，刷新页面后可直接恢复动态路由。
- 该项目目录名保留了当前仓库中的实际命名，文档也按现状描述。
