
// 导入自定义的 get 方法（封装了 axios 的 get 请求，见 src/net/index.ts）
import { get, post, postForm } from '@/net'
// 导入 pinia 状态管理
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
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
  {
    path: '/index',
    name: 'index',
    component: () => import('@/views/IndexView.vue'),
    children: [
      {
        path: 'home',
        name: 'index-home',
        component: () => import('@/views/HomeView.vue')
      },
      {
        path: 'community/groups',
        name: 'community-groups',
        component: () => import('@/views/study/community/GroupList.vue')
      },
      {
        path: 'community/group/:id',
        name: 'community-group',
        component: () => import('@/views/study/community/GroupDetail.vue')
      },
      {
        path: 'community/public',
        name: 'community-public',
        component: () => import('@/views/study/community/PublicDiscussion.vue')
      }
    ]
  }
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
  const userStore = useUserStore()
  const menuStore = useMenuStore()

  // 已登录且还没加载动态路由时，先加载动态路由
  if (userStore.auth.user !== null && (!hasAddedDynamicRoutes || to.matched.length === 0)) {
    hasAddedDynamicRoutes = true // 设置标记，避免重复添加
    await routers() // 等待动态路由添加完成

    // 如果加载后仍然匹配不到（且不是去首页），则说明无权访问
    if (to.matched.length === 0 && to.path !== '/index') {
      return { name: 'index' }
    }
    return { ...to, replace: true }
  }

  // 已登录但访问 welcome 相关页面，强制跳转到 index
  if (userStore.auth.user !== null && typeof to.name === 'string' && to.name.startsWith('welcome')) {
    return { name: 'index' }
  } else if (userStore.auth.user === null && to.fullPath.startsWith('/index')) {
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
 * 核心：将路由数据注入到 vue-router
 */
const injectRoutes = (routesData: any[]) => {
  const processRoutes = (arr: any[]) => {
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
          component: modules[componentPath],
          meta: {
            menuVisible: item.menuVisible ?? 1
          }
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
}

/**
 * 动态加载后端返回的路由配置
 */
const routers = () => {
  return new Promise<void>((resolve) => {
    const userStore = useUserStore()
    const menuStore = useMenuStore()

    // 方案改进：如果本地已经有持久化的菜单，直接同步注入，保证浏览器重启后立刻可用
    if (menuStore.menuList && menuStore.menuList.length > 0) {
      injectRoutes(menuStore.menuList)
      // 如果本地已经有数据了，就不必阻塞路由跳转，直接 resolve
      resolve();
    }

    // 请求成功回调
    const onSuccess = (message: string, data: any) => {
      // data 是后端返回的路由数组
      const routesData = data;
      // 如果不是数组直接结束
      if (!Array.isArray(routesData)) {
        if (menuStore.menuList.length === 0) resolve();
        return;
      }

      injectRoutes(routesData);

      // 更新持久化菜单
      menuStore.menuList = routesData;

      // 如果之前没有数据（第一次登录），则在这里 resolve
      resolve();
    }

    // 请求失败/未授权等，直接 resolve
    const onFailure = (message: any) => {
      resolve();
    }
    const onError = (err: any) => {
      resolve();
    }

    // 请求后端路由
    postForm('/api/routes/tree', {
      role: userStore.auth.user?.role,
    }, onSuccess, onFailure, onError)
  });
}

// 退出时清除动态路由和菜单
export const resetRoutes = () => {
  const menuStore = useMenuStore()
  // 重置标记
  hasAddedDynamicRoutes = false;
  // 清空菜单持久化
  menuStore.menuList = [];

  // 遍历所有当前路由，如果是动态添加的（非 welcome及index），就将其移除
  router.getRoutes().forEach(route => {
    if (route.name && route.name !== 'welcome' && route.name !== 'welcome-login' && route.name !== 'welcome-register' && route.name !== 'welcome-forget' && route.name !== 'index') {
      router.removeRoute(route.name as string);
    }
  });
}

export default router
