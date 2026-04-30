// Vite 提供的 ImportMeta 与环境变量基础类型。
/// <reference types="vite/client" />

// 项目级环境变量声明：在这里补齐 VITE_ 前缀变量可获得完整类型提示。
interface ImportMetaEnv {
  // 后端 API 基础地址，例如 http://localhost:8080。
  readonly VITE_API_BASE_URL: string
}

// 将上面的环境变量类型挂到 import.meta.env。
interface ImportMeta {
  readonly env: ImportMetaEnv
}

// 让 TypeScript 能识别 .vue 单文件组件的默认导出类型。
declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  // 对于大多数页面/组件，使用通用组件类型足以满足类型检查。
  const component: DefineComponent<{}, {}, any>
  export default component
}


