package com.exampe.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果封装类，供前端请求封装和后端控制器共用。
 * <p>标准响应结构：status、success、message、data</p>
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestBean<T> {

    /**
     * 响应状态码。
     */
    private int status;

    /**
     * 请求是否成功。
     */
    private boolean success;

    /**
     * 响应消息。
     */
    private String message;

    /**
     * 响应数据（泛型）。
     */
    private T data;

    /**
     * 成功响应（无数据）。
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> RestBean<T> success() {
        return new RestBean<>(200, true, "操作成功", null);
    }

    /**
     * 成功响应（带消息）。
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> RestBean<T> success(String message) {
        return new RestBean<>(200, true, message, null);
    }

    /**
     * 成功响应（带数据）。
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 响应对象
     */
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, true, "操作成功", data);
    }

    /**
     * 成功响应（带消息和数据）。
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> RestBean<T> success(String message, T data) {
        return new RestBean<>(200, true, message, data);
    }

    /**
     * 失败响应（仅状态码）。
     *
     * @param status 错误状态码
     * @param <T>    数据类型
     * @return 响应对象
     */
    public static <T> RestBean<T> failure(int status) {
        // 默认错误文案，适合异常栈不需要直接暴露给前端的场景。
        return new RestBean<>(status, false, "操作失败", null);
    }

    /**
     * 失败响应（带消息）。
     *
     * @param status  错误状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> RestBean<T> failure(int status, String message) {
        return new RestBean<>(status, false, message, null);
    }

    /**
     * 失败响应（带数据和消息）。
     *
     * @param status  错误状态码
     * @param message 错误消息
     * @param data    错误数据
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> RestBean<T> failure(int status, String message, T data) {
        return new RestBean<>(status, false, message, data);
    }
}
