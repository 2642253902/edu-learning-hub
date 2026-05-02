// 声明常用路径别名，避免 TypeScript 报 "Cannot find module '@/xxx'"。
// 根据项目实际导出可逐步补充更精确的类型定义。

declare module '@/net' {
  const _default: any
  export default _default
  export function get(...args: any[]): any
  export function post(...args: any[]): any
  export function put(...args: any[]): any
  export function deleteMapping(...args: any[]): any
}

declare module '@/stores/*' {
  const _default: any
  export default _default
}

declare module '@/stores/user' {
  // 简要声明 user store 的常见结构，方便类型推断（可按需细化）
  export const useUserStore: () => {
    auth: { user?: { id?: string | number; username?: string } }
    [key: string]: any
  }
}

declare module '@/router' {
  const _default: any
  export default _default
}
