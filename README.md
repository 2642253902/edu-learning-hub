# Study Project - 校园智慧学习社区

## 🌟 最新更新

> 🎉 **已完成：** 企业级 RBAC 权限管理系统 + 用户管理模块 + 消息模块 + 个人信息页
>
> 🎯 **已补充：** 学习小组管理、公共讨论管理、课程评价管理、学习记录显示优化、顶部菜单搜索跳转
> 
> 📖 **文档导航：**
> - **首次使用？** 👉 [READING_GUIDE.md](READING_GUIDE.md) 选择你的角色
> - **查看全部文档？** 👉 [DOCS_INDEX.md](DOCS_INDEX.md) 完整索引
> - **立即部署？** 👉 [DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md) 分阶段指南

## 1. 项目简介
Study Project 是一个面向教学场景的前后端分离学习平台，覆盖用户认证、角色权限管理、用户管理、课程管理、资源管理、学习记录、社区互动、消息通知与基于角色的动态菜单。

项目当前由两个子工程组成：
- `study-project-barkend`：Spring Boot 后端服务
- `study-project-fronrend`：Vue 3 前端应用

> 说明：子工程目录中的 `barkend` 与 `fronrend` 为当前仓库实际命名，本文档按现状描述。

## 2. 核心能力

### 2.1 认证与账号
- 用户注册（邮箱验证码）
- 登录/退出登录
- 忘记密码与密码重置
- 获取当前登录用户信息
- **[新增] 管理员用户管理** - 创建、编辑、删除用户账户
- **[新增] 个人信息页** - 查看当前登录账号基础资料

### 2.2 权限与菜单
- **[新增] 角色管理** - 创建、编辑、删除角色
- **[新增] 权限分配** - 为角色分配菜单访问权限（树形选择器）
- **[新增] 菜单显隐控制** - 支持菜单显示/隐藏
- 后端按角色返回路由树
- 前端登录后动态注入路由
- 支持菜单持久化与退出清理
- 顶部搜索框支持菜单模块快速检索与跳转

### 2.3 教学业务
- 课程分类管理
- 课程管理（分页、搜索、增删改查、状态管理）
- 课程资源管理（列表、状态过滤、增删改查）
- 学习记录管理
- 学习中心按分类展示课程并按资源类型跳转学习详情

### 2.4 社区与消息
- 学习小组管理
- 公共讨论管理
- 课程评价管理
- 消息模块：管理员发布消息提醒，用户查看/已读

## 3. 技术栈

### 3.1 后端
- Java 17
- Spring Boot 3
- Spring Security
- MyBatis + MyBatis-Plus
- MySQL
- Redis
- Java Mail

### 3.2 前端
- Vue 3 + TypeScript
- Vite
- Vue Router
- Pinia
- Element Plus
- Axios

## 4. 项目结构

```text
study-project/
├─ study-project-barkend/         # 后端服务
│  ├─ src/main/resources/sql/     # 数据库脚本
│  └─ src/main/java/com/exampe/
│     ├─ auth/                    # 认证与用户
│     ├─ study/                   # 课程/资源/学习记录/社区/消息
│     └─ sys/                     # 系统路由、角色路由与系统功能
└─ study-project-fronrend/        # 前端应用
   └─ src/
      ├─ components/welcome/      # 登录/注册/忘记密码
   ├─ views/study/             # 学习中心与教学模块页面
   ├─ views/community/         # 社区与小组管理页面
   ├─ views/sys/               # 个人信息与消息中心
      ├─ router/                  # 静态+动态路由
      └─ stores/                  # 用户与菜单状态
```

## 5. 快速开始

### 5.1 环境要求
- JDK 17+
- Maven 3.9+
- Node.js 20+
- MySQL 5.7+/8.x
- Redis 6+

### 5.2 数据库初始化
1. 创建数据库 `study`
2. 执行脚本：`study-project-barkend/src/main/resources/sql/message_module.sql`
3. 如果你的环境里已有原始业务表，请继续导入原始的 `study.sql` 初始化脚本

### 5.3 启动后端
```bash
cd study-project-barkend
mvn spring-boot:run
```

默认后端使用 `application.yaml` 中的配置连接 MySQL、Redis 与邮件服务。

### 5.4 启动前端
```bash
cd study-project-fronrend
npm install
npm run dev
```

默认开发服务器启动后，浏览器访问 Vite 控制台显示的地址（本地常见为 `http://localhost:5173`，如果端口占用会自动切换）。

## 6. 主要接口分组（示例）
- 认证：`/api/auth/*`
- 用户：`/api/user/*`
- 消息：`/api/message/*`
- 路由：`/api/routes/tree`
- 课程：`/study/cloudComputingCourse/*`
- 课程资源：`/study/cloudComputingCourseResource/*`
- 课程分类：`/study/cloudComputingCourseType/*`
- 学习记录：`/study/cloudComputingStudentLearningRecord/*`
- 社区：`/api/community/*`

## 7. 注意事项
- 当前配置文件包含本地开发账号、邮箱服务等信息，建议在团队协作或部署前改为环境变量方式管理。
- 前端动态路由依赖登录用户角色，请确保后端路由树接口返回正确结构。
- 后端消息模块依赖 `message_notice` 和 `message_notice_read` 两张表，请先执行对应 SQL。

## 8. 后续优化建议
- 统一子工程命名（backend/frontend）以提升可读性
- 补充接口文档（OpenAPI/Swagger）
- 增加自动化测试与 CI 流程
- 完善部署文档（Docker 或脚本化部署）
- 给消息模块补充分角色模板和定时发送能力
