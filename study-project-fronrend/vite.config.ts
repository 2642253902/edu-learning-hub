import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// 让 Element Plus 的组件和 API 能按需自动导入，减少手写导入负担。
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// Vite 构建配置：这里只保留当前项目真正需要的插件和路径别名。
export default defineConfig({
  plugins: [
    // Vue SFC 编译支持。
    vue(),
    // 浏览器中的 Vue DevTools 接入，方便调试组件树和状态。
    vueDevTools(),
    // 自动导入常用 API 与 Element Plus 相关能力。
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    // 自动注册模板中使用到的 Element Plus 组件。
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      // 统一把 @ 映射到 src，避免深层相对路径带来的维护成本。
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
