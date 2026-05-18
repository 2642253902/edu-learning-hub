# study-project-fronrend

## 项目简介
这是 Study Project 的前端工程，基于 Vue 3 + TypeScript + Vite 构建，负责承载登录注册、动态路由、课程管理、社区互动、消息中心、个人信息和 AI 聊天等页面。前端与后端通过统一接口约定协作，登录后会根据角色权限树动态注入可访问菜单和路由。

## 技术栈
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

## 目录说明
- [src/components/welcome](src/components/welcome)：登录、注册、忘记密码
- [src/views/study](src/views/study)：课程、分类、资源、学习记录、评价
- [src/views/community](src/views/community)：小组、帖子、评论
- [src/views/index](src/views/index)：统计看板
- [src/views/sys](src/views/sys)：个人信息、角色、用户、消息、AI 聊天
- [src/router/index.ts](src/router/index.ts)：静态路由、动态路由注入和路由守卫
- [src/net/index.ts](src/net/index.ts)：统一请求封装和鉴权失效处理

## 本地运行

### 环境要求
- Node.js 20+

### 安装依赖

```bash
npm install
```

### 开发启动

```bash
npm run dev
```

### 类型检查与构建

```bash
npm run build
```

## 联调说明
- 开发环境接口地址见 [.env.development](.env.development)
- 生产环境接口地址见 [.env.production](.env.production)
- 登录后前端会从后端拉取角色菜单树，并持久化到本地缓存，刷新后可恢复动态路由

## 备注
- 当前工程目录名保留了仓库中的实际命名 `study-project-fronrend`
- 如果在 IDE 中使用 Vue 插件，请优先选择 Volar，并关闭 Vetur
