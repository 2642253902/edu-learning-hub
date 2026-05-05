import axios from "axios";
import { ElMessage } from "element-plus";
import { resetRoutes } from '@/router'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'

// 默认失败处理统一提示业务消息，避免前端每个调用点重复写同一段兜底逻辑。
const defaultFailure = (message: string) => ElMessage.warning(message)
// 网络异常通常意味着前后端链路中断，这里统一提示用户稍后重试。
const defaultError = (_err: any) => ElMessage.error("网络异常，请稍后再试")

export function getApiBaseURL() {
    return axios.defaults.baseURL ?? ''
}

const handleAuthError = () => {
    // 只允许触发一次退出流程，避免前后端连续返回 401/403 时重复弹窗和跳转。
    if ((window as any)._isExiting) return

    // 登录页已经是恢复入口；/index 也要正常走失效处理，避免前端继续停留在失效态。
    if (window.location.pathname === '/') {
        return
    }

    (window as any)._isExiting = true

    // 会话失效或权限不足时，清空本地缓存，避免旧状态继续影响路由守卫和后端接口判断。
    const storage = typeof window !== 'undefined' ? window.localStorage : null
    storage?.removeItem('user')
    storage?.removeItem('menuList')

    // 同步清理内存中的状态和动态路由，保证 SPA 会话内不再保留旧角色的菜单与路由。
    try {
        // 清空路由与持久化菜单
        resetRoutes()
        const userStore = useUserStore()
        const menuStore = useMenuStore()
        userStore.auth.user = null
        menuStore.menuList = []
    } catch (e) {
        // 在非常罕见的时序情况下（Pinia 未安装或其他），仍然保证 localStorage 被清理并跳转。
        console.warn('handleAuthError: reset stores/routes failed', e)
    }

    ElMessage.error('会话已过期，请重新登录')

    // 先给提示消息一点展示时间，再跳回登录页重新建立前后端会话。
    setTimeout(() => {
        if (typeof window !== 'undefined') {
            window.location.href = '/'
        }
    }, 1500)
}

// 全局响应拦截器负责兜底认证失败场景，避免每个前端 API 调用都单独处理 401/403。
axios.interceptors.response.use(
    response => response,
    error => {
        const status = error.response?.status
        if (status === 401 || status === 403) {
            handleAuthError()
        }
        return Promise.reject(error)
    }
)

export function postForm(
    url: string,
    data: any,
    success: (message: string, data: any) => void,
    failure: (message: string, data: any) => void = defaultFailure,
    error: (err: any) => void = defaultError
) {
    // 统一把对象转成 x-www-form-urlencoded，匹配后端表单型接口和登录类请求。
    const formData = new URLSearchParams()
    Object.entries(data || {}).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
            formData.append(key, String(value))
        }
    })

    return axios.post(url, formData, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        withCredentials: true
    }).then(response => {
        const resData = response.data;
        if (resData.status === 401 || resData.status === 403) {
            // 先把后端的认证失败信息交给调用方处理（比如登录页需要显示具体失败原因），
            // 然后再执行统一的会话失效流程（会在登录页被短路）。
            try { failure(resData.message, resData.data) } catch (e) {}
            handleAuthError()
            return
        }
        if (resData.success === true) {
            success(resData.message, resData.data);
        } else {
            failure(resData.message, resData.data);
        }
    }).catch(err => {
        error(err);
    });
}

export function post(
    url: string,
    data: any,
    success: (message: string, data: any) => void,
    failure: (message: string, data: any) => void = defaultFailure,
    error: (err: any) => void = defaultError
) {
    return axios.post(url, data, { withCredentials: true }).then(response => {
        const resData = response.data;
        if (resData.status === 401 || resData.status === 403) {
            try { failure(resData.message, resData.data) } catch (e) {}
            handleAuthError()
            return
        }
        if (resData.success === true) {
            success(resData.message, resData.data);
        } else {
            failure(resData.message, resData.data);
        }
    }).catch(err => {
        error(err);
    });
}

export function get(
    url: string,
    success: (message: string, data: any) => void,
    failure: (message: string, data: any) => void = defaultFailure,
    error: (err: any) => void = defaultError
) {
    return axios.get(url, {
        withCredentials: true
    }).then(response => {
        const resData = response.data;
        // 有些接口会返回 200 但把失败状态放在业务字段里，这里再补一层前后端协商判断。
        if (resData.status === 401 || resData.status === 403) {
            try { failure(resData.message, resData.data) } catch (e) {}
            handleAuthError()
            return
        }
        if (resData.success) {
            success(resData.message, resData.data);
        } else {
            failure(resData.message, resData.data);
        }
    }).catch(err => {
        error(err);
    });
}

export function getBlob(
    url: string,
    error: (err: any) => void = defaultError
) {
    return axios.get(url, {
        withCredentials: true,
        responseType: 'blob'
    }).then(response => {
        return response.data as Blob
    }).catch(err => {
        error(err)
        throw err
    })
}

export function deleteMapping(
    url: string,
    data: any,
    success: (message: string, data: any) => void,
    failure: (message: string, data: any) => void = defaultFailure,
    error: (err: any) => void = defaultError
) {
    return axios.delete(url, {
        params: data,
        data,
        withCredentials: true
    }).then(response => {
        const resData = response.data;
        if (resData.status === 401 || resData.status === 403) {
            try { failure(resData.message, resData.data) } catch (e) {}
            handleAuthError()
            return
        }
        if (resData.success === true) {
            success(resData.message, resData.data);
        } else {
            failure(resData.message, resData.data);
        }
    }).catch(err => {
        error(err);
    });
}

