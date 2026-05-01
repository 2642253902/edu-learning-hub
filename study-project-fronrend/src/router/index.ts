
// 路由层只依赖 net 封装，不直接碰 axios，便于统一处理鉴权、消息和错误。
import { postForm } from '@/net'
// 路由守卫需要读取用户和菜单状态，判断当前访问权限。
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

// 静态路由只描述应用骨架：欢迎页和首页壳子先固定下来，动态页面后面再补。
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
    redirect: '/index/home',
    children: [
      {
        path: 'home',
        name: 'home',
        component: () => import('@/views/HomeView.vue'),
        meta: { title: '工作台首页' }
      },
      {
        path: 'student-dashboard',
        name: 'student-dashboard',
        component: () => import('@/views/index/StudentStatsView.vue'),
        meta: { title: '学生数据统计' }
      },
      {
        path: 'teacher-dashboard',
        name: 'teacher-dashboard',
        component: () => import('@/views/index/TeacherStatsView.vue'),
        meta: { title: '教师数据统计' }
      },
    ]
  }
]

// 创建路由实例，后续统一在这里挂载守卫和动态路由。
const router = createRouter({
  history: createWebHistory(),
  routes: routes,
})

// 记录动态路由是否已经注入，避免登录态变化或重复跳转时反复添加。
let hasAddedDynamicRoutes = false;

// 全局前置守卫：负责把“登录态、动态路由、欢迎页跳转”这几件事串起来。
router.beforeEach(async (to, _from) => {
  const userStore = useUserStore()

  // 已登录但动态菜单还没注入时，先拉后端菜单并把可访问页面补进路由表。
  if (userStore.auth.user !== null && (!hasAddedDynamicRoutes || to.matched.length === 0)) {
    hasAddedDynamicRoutes = true // 先打标记，避免并发跳转时重复触发注入流程。
    await routers() // 等动态路由完成注入后，再继续本次跳转。

    // 注入后仍匹配不到，说明当前路径没有对应菜单或页面，统一回到首页兜底。
    if (to.matched.length === 0 && to.path !== '/index') {
      return { name: 'index' }
    }
    return { ...to, replace: true }
  }

  // 已登录时不允许回到欢迎页，直接把用户送回主界面。
  if (userStore.auth.user !== null && typeof to.name === 'string' && to.name.startsWith('welcome')) {
    return { name: 'index' }
  } else if (userStore.auth.user === null && to.fullPath.startsWith('/index')) {
    // 未登录访问主界面或其子页面，统一重定向到登录页。
    return { name: 'welcome-login' }
  }
  else if (to.matched.length === 0) {// 仍然没有匹配到路由时，给一个可用的落点，避免空白页。
    return { name: 'index' }
  }

  // 其余场景默认放行，避免触发“并非所有代码路径都返回值”的类型错误。
  return true
})

// 扫描 views 下的所有页面组件，后端返回路由树后再按路径进行懒加载绑定。
// 例如 '../views/study/PreparationCenter.vue' => () => import('../views/study/PreparationCenter.vue')
const modules = import.meta.glob('../views/**/*.vue')

/**
 * 将后端路由树转换成 vue-router 可识别的动态路由。
 * 这里同时负责把路径统一成绝对路径，并把菜单树递归处理到底。
 */
const injectRoutes = (routesData: any[]) => {
  const processRoutes = (arr: any[]) => {
    arr.forEach(item => {
      // 后端可能返回相对路径，这里统一补成绝对路径，保证路由匹配稳定。
      const routePath = item.path.startsWith('/') ? item.path : `/${item.path}`;
      // 组件文件名按路由路径拼接，和 views 目录保持一一对应。
      const componentPath = `../views${routePath}.vue`;

      // 只有本地确实存在对应页面组件时，才把它挂到 index 下。
      if (!router.hasRoute(item.name) && modules[componentPath]) {
        router.addRoute('index', {
          path: routePath,
          name: item.name,
          component: modules[componentPath],
          meta: {
            menuVisible: item.menuVisible ?? 1,
            title: item.remark || item.name
          }
        });
      }

      // 回写标准化路径，后续菜单渲染和跳转都直接使用同一个字段。
      item.path = routePath;

      // 子菜单继续递归处理，保证整棵菜单树都能注入。
      if (item.children && item.children.length > 0) {
        processRoutes(item.children);
      }
    });
  };
  processRoutes(routesData);
}

/**
 * 拉取后端路由树并注入到前端路由表。
 * 本地已有缓存时先同步注入，避免刷新后页面空白。
 */
const routers = () => {
  return new Promise<void>((resolve) => {
    const userStore = useUserStore()
    const menuStore = useMenuStore()

    // 如果本地已经有持久化菜单，优先同步注入，浏览器刷新后也能立即恢复可访问页面。
    if (menuStore.menuList && menuStore.menuList.length > 0) {
      injectRoutes(menuStore.menuList)
      // 本地菜单已经可用时，不必等待接口返回，直接继续路由跳转。
      resolve();
      return;
    }

    // 接口成功后，把后端路由树转成动态路由并同步进持久化菜单。
    const onSuccess = (_message: string, data: any) => {
      // data 理论上是后端返回的路由数组。
      const routesData = data;
      // 如果返回结构异常，直接结束本次注入，不让路由守卫卡死。
      if (!Array.isArray(routesData)) {
        if (menuStore.menuList.length === 0) resolve();
        return;
      }

      injectRoutes(routesData);

      // 更新持久化菜单，供刷新后恢复和侧边栏渲染使用。
      menuStore.menuList = routesData;

      // 首次登录时，路由注入完成后继续当前跳转。
      resolve();
    }

    // 请求失败、未授权或异常时，直接放行，避免守卫无限等待。
    const onFailure = (_message: any) => {
      resolve();
    }
    const onError = (_err: any) => {
      resolve();
    }

    // 按当前角色请求可访问的路由树。
    postForm('/api/routes/tree', {
      role: userStore.auth.user?.role,
    }, onSuccess, onFailure, onError)
  });
}

// 退出登录时清掉动态路由和菜单缓存，避免下次登录继承上一个角色的权限树。
export const resetRoutes = () => {
  const menuStore = useMenuStore()
  // 先把注入标记复位，下一次登录可以重新加载路由。
  hasAddedDynamicRoutes = false;
  // 清空本地菜单缓存，防止刷新后仍然渲染旧权限菜单。
  menuStore.menuList = [];

  // 遍历当前路由表，只移除动态注入的业务页面，保留欢迎页和首页骨架。
  router.getRoutes().forEach(route => {
    if (
      route.name &&
      route.name !== 'welcome' &&
      route.name !== 'welcome-login' &&
      route.name !== 'welcome-register' &&
      route.name !== 'welcome-forget' &&
      route.name !== 'index' &&
      route.name !== 'home' &&
      route.name !== 'student-dashboard' &&
      route.name !== 'teacher-dashboard'
    ) {
      router.removeRoute(route.name as string);
    }
  });
}

export default router
