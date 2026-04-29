import axios from "axios";
import { ElMessage } from "element-plus";

const defaultFailure = (message: string) => ElMessage.warning(message)
const defaultError = (err: any) => ElMessage.error("网络异常，请稍后再试")

export function getApiBaseURL() {
    return axios.defaults.baseURL ?? ''
}

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

type ApiSuccess<T = any> = (data: T) => void
type ApiFailure = (message: string, data?: any) => void

export const communityApi = {
    listGroups(success: ApiSuccess<any[]>, failure: ApiFailure = defaultFailure) {
        return get('/api/community/groups', (_, data) => success(data || []), failure)
    },
    createGroup(payload: any, success: ApiSuccess<any>, failure: ApiFailure = defaultFailure) {
        return post('/api/community/groups', payload, (_, data) => success(data), failure)
    },
    joinGroup(groupId: string, success: ApiSuccess<any>, failure: ApiFailure = defaultFailure) {
        return post(`/api/community/groups/${groupId}/join`, {}, (_, data) => success(data), failure)
    },
    listPosts(groupId: string | undefined, success: ApiSuccess<any[]>, failure: ApiFailure = defaultFailure) {
        const query = groupId ? `?groupId=${encodeURIComponent(groupId)}` : ''
        return get(`/api/community/posts${query}`, (_, data) => success(data || []), failure)
    },
    createPost(payload: any, success: ApiSuccess<any>, failure: ApiFailure = defaultFailure) {
        return post('/api/community/posts', payload, (_, data) => success(data), failure)
    },
    listComments(postId: string, success: ApiSuccess<any[]>, failure: ApiFailure = defaultFailure) {
        return get(`/api/community/posts/${postId}/comments`, (_, data) => success(data || []), failure)
    },
    createComment(postId: string, payload: any, success: ApiSuccess<any>, failure: ApiFailure = defaultFailure) {
        return post(`/api/community/posts/${postId}/comments`, payload, (_, data) => success(data), failure)
    },
    listReviews(resourceId: string, success: ApiSuccess<any[]>, failure: ApiFailure = defaultFailure) {
        return get(`/api/community/reviews?resourceId=${encodeURIComponent(resourceId)}`, (_, data) => success(data || []), failure)
    },
    createReview(payload: any, success: ApiSuccess<any>, failure: ApiFailure = defaultFailure) {
        return post('/api/community/reviews', payload, (_, data) => success(data), failure)
    },
    likeReview(reviewId: string, success: ApiSuccess<any>, failure: ApiFailure = defaultFailure) {
        return post(`/api/community/reviews/${reviewId}/like`, {}, (_, data) => success(data), failure)
    }
}

export const messageApi = {
    manageList(params: { pageNo?: number; pageSize?: number; title?: string; level?: string | number; enabled?: string | number }, success: ApiSuccess<any>, failure: ApiFailure = defaultFailure) {
        const search = new URLSearchParams()
        search.append('pageNo', String(params.pageNo ?? 1))
        search.append('pageSize', String(params.pageSize ?? 10))
        if (params.title) search.append('title', params.title)
        if (params.level !== undefined && params.level !== '') search.append('level', String(params.level))
        if (params.enabled !== undefined && params.enabled !== '') search.append('enabled', String(params.enabled))
        return get(`/api/message/manage/list?${search.toString()}`, (_, data) => success(data), failure)
    },
    add(payload: any, success: ApiSuccess<any>, failure: ApiFailure = defaultFailure) {
        return post('/api/message/manage/add', payload, (_, data) => success(data), failure)
    },
    edit(payload: any, success: ApiSuccess<any>, failure: ApiFailure = defaultFailure) {
        return post('/api/message/manage/edit', payload, (_, data) => success(data), failure)
    },
    delete(id: string, success: ApiSuccess<any>, failure: ApiFailure = defaultFailure) {
        return deleteMapping('/api/message/manage/delete', { id }, (_, data) => success(data), failure)
    },
    userList(limit = 20, success: ApiSuccess<any[]>, failure: ApiFailure = defaultFailure) {
        return get(`/api/message/user/list?limit=${limit}`, (_, data) => success(data || []), failure)
    },
    read(messageId: string, success: ApiSuccess<any>, failure: ApiFailure = defaultFailure) {
        return post(`/api/message/user/read?messageId=${encodeURIComponent(messageId)}`, {}, (_, data) => success(data), failure)
    },
    readAll(success: ApiSuccess<any>, failure: ApiFailure = defaultFailure) {
        return post('/api/message/user/readAll', {}, (_, data) => success(data), failure)
    }
}
