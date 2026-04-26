import axios from "axios";

import { ElMessage } from "element-plus";

const defaultFailure = (message: string) => ElMessage.warning(message)
const defaultError = (err: any) => ElMessage.error("网络异常，请稍后再试")

// 全局响应拦截器：处理会话失效 (401/403) 时自动清空用户状态并重定向到登录页
axios.interceptors.response.use(
  response => response,
  error => {
    const status = error.response?.status
    if (status === 401 || status === 403) {
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
      }, 500)
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
        if (resData.success === true) {
            success(resData.message, resData.data);
        } else {
            failure(resData.message, resData.data);
        }
    }).catch(err => {
        error(err);
    });
}
