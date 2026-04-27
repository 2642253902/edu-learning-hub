import axios from "axios";
import { ElMessage } from "element-plus";

const defaultFailure = (message: string) => ElMessage.warning(message)
const defaultError = (err: any) => ElMessage.error("网络异常，请稍后再试")

const handleAuthError = () => {
    // 检查是否已经在处理退出流程，避免循环
    if ((window as any)._isExiting) return
    
    // 如果已经在登录页，不要再触发报错和跳转
    if (window.location.pathname === '/' || window.location.pathname === '/index') {
        return
    }

    (window as any)._isExiting = true

    // 会话失效或无权限，清空本地用户数据并重定向
    const storage = typeof window !== 'undefined' ? window.localStorage : null
    storage?.removeItem('user')
    storage?.removeItem('menuList')
    
    ElMessage.error('会话已过期，请重新登录')
    
    // 延迟导航，确保消息显示
    setTimeout(() => {
        if (typeof window !== 'undefined') {
            window.location.href = '/'
        }
    }, 1500)
}

// 全局响应拦截器：处理 Http Status 为 401/403 的情况
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
        // 处理 Spring Security 可能返回的 200 状态码但包含 401 错误的情况
        if (resData.status === 401 || resData.status === 403) {
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
