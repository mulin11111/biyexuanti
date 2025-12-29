package com.graduation.topic.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应结果
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码：200成功，其他失败
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功响应，带数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败响应，默认状态码
     */
    public static <T> Result<T> fail(String message) {
        return fail(500, message);
    }

}