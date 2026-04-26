
// 导入自定义的 get 方法（封装了 axios 的 get 请求，见 src/net/index.ts）
import { get, post, postForm } from '@/net'
// 导入 pinia 用户状态管理
import { useStore } from '@/stores/user'
import { ref } from 'vue'
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'



// 静态路由配置，未登录时只允许访问 welcome 相关页面，登录后进入 index
const routes: Readonly<RouteRecordRaw[]> = [
  {
    path: '/', name: 'welcome', component: () => import('@/views/WelcomeView.vue'),
    children: [
      {
        path: '',
        name: 'welcome-login',
        component: () => import('@/components/welcome/LoginPage.vue')
      },
      {
        path: 'register',
        name: 'welcome-register',
        component: () => import('@/components/welcome/RegisterPage.vue')
      },
      {
        path: 'forget',
        name: 'welcome-forget',
        component: () => import('@/components/welcome/ForgetPage.vue')
      },
    ]
  },
  { path: '/index', name: 'index', component: () => import('@/views/IndexView.vue'), }
]




// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes: routes,
})


// 标记是否已经添加过动态路由，防止重复添加
let hasAddedDynamicRoutes = false;



// 路由守卫：每次路由跳转前都会执行
router.beforeEach(async (to, from) => {
  const store = useStore()

  // 已登录且还没加载动态路由时，先加载动态路由
  if (store.auth.user !== null && !hasAddedDynamicRoutes) {
    hasAddedDynamicRoutes = true // 设置标记，避免重复添加
    await routers() // 等待动态路由添加完成
    return { ...to, replace: true }
  }

  // 已登录但访问 welcome 相关页面，强制跳转到 index
  if (store.auth.user !== null && typeof to.name === 'string' && to.name.startsWith('welcome')) {
    return { name: 'index' }
  } else if (store.auth.user === null && to.fullPath.startsWith('/index')) {
    // 未登录访问 index 相关页面，强制跳转到登录页
    return { name: 'welcome-login' }
  }
  else if (to.matched.length === 0) {//没有匹配到路由
    return { name: 'index' }
  }
})


// 获取 views 下所有的 vue 文件组件 (作为动态组件导入的基础)
// 例如 '../views/study/PreparationCenter.vue' => () => import('../views/study/PreparationCenter.vue')
const modules = import.meta.glob('../views/**/*.vue')


/**
 * 动态加载后端返回的路由配置
 * 1. 登录后调用 get('/system/routes/tree', ...)
 * 2. 后端返回菜单/路由树（数组），遍历生成路由并 addRoute 动态注入
 * 3. 菜单数据同步到 pinia store.menuList，供左侧菜单渲染
 *
 * get('/system/routes/tree', onSuccess, onFailure, onError)
 *  get 方法见 src/net/index.ts，实际是 axios.get 封装
 *  - onSuccess: 请求成功，data 是后端返回的路由数组
 *  - onFailure/onError: 请求失败或未登录，直接 resolve，页面会跳转到登录页
 */
const routers = () => {
  return new Promise<void>((resolve) => {
    const store = useStore()
    // 请求成功回调
    const onSuccess = (message: string, data: any) => {
      // data 是后端返回的路由数组
      const routesData = data;
      // 如果不是数组直接结束
      if (!Array.isArray(routesData)) {
        resolve();
        return;
      }



      // 递归处理路由树，动态注入到 vue-router
      const processRoutes = (arr: any[]) => {
         console.log('请求动态路由，用户角色：')

        arr.forEach(item => {
          // 1. 路径补全

          const routePath = item.path.startsWith('/') ? item.path : `/${item.path}`;
          // 2. 组件路径拼接
          const componentPath = `../views${routePath}.vue`;

          // 3. 动态添加路由到 index 下
          if (!router.hasRoute(item.name) && modules[componentPath]) {
            router.addRoute('index', {
              path: routePath,
              name: item.name,
              component: modules[componentPath]
            });
          }

          // 4. 菜单 path 修正
          item.path = routePath;

          // 5. 递归处理子菜单
          if (item.children && item.children.length > 0) {
            processRoutes(item.children);
          }
        });
      };

      processRoutes(routesData);
      // 菜单数据存入 pinia，供左侧菜单渲染
      store.menuList = routesData;
      resolve();
    }

    // 请求失败/未授权等，直接 resolve，页面会跳转到登录页
    const onFailure = (message: any) => {
      resolve();
    }
    const onError = (err: any) => {
      resolve();
    }

    // 这里就是你看到的 get('/system/routes/tree'...)
    // 实际会发起 axios.get('/system/routes/tree') 请求后端，
    // 并根据返回结果动态生成路由和菜单
    postForm('/api/routes/tree', {
      role: store.auth.user?.role,
    }, onSuccess, onFailure, onError)
  });
}

// 退出时清除动态路由和菜单
export const resetRoutes = () => {
  const store = useStore()
  // 重置标记
  hasAddedDynamicRoutes = false;
  // 清空菜单持久化
  store.menuList = [];

  // 遍历所有当前路由，如果是动态添加的（非 welcome及index），就将其移除
  router.getRoutes().forEach(route => {
    if (route.name && route.name !== 'welcome' && route.name !== 'welcome-login' && route.name !== 'welcome-register' && route.name !== 'welcome-forget' && route.name !== 'index') {
      router.removeRoute(route.name);
    }
  });
}

export default router
