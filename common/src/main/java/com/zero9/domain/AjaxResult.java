package com.zero9.domain;

import java.util.HashMap;

public class AjaxResult extends HashMap<String, Object> {

    /*初始化空构造函数*/
    public AjaxResult() {

    }

    /**
     * 初始化参数code、msg构造函数
     * @param code 状态码
     * @param msg  返回消息
     */
    public AjaxResult(Integer code, String msg) {
        super.put("code", code);
        super.put("msg", msg);
    }

    /**
     * 初始化参数code、msg、data构造函数
     * @param code 状态码
     * @param msg  返回消息
     * @param data 数据对象
     */
    public AjaxResult(Integer code, String msg, Object data) {
        super.put("code", code);
        super.put("msg", msg);
        if(data != null) {
            super.put("data", data);
        }
    }

    /**
     * 响应成功函数
     * @param msg  返回消息
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(200, msg, data);
    }

    /**
     * 响应成功函数
     * @return 成功消息
     */
    public static AjaxResult success() {
        return AjaxResult.success("操作成功!");
    }

    /**
     * 响应成功函数
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(Object data) {
        return AjaxResult.success("操作成功!", data);
    }

    /**
     * 响应成功函数
     * @param msg  返回消息
     * @return 成功消息
     */
    public static AjaxResult success(String msg) {
        return  AjaxResult.success(msg, null);
    }

    /**
     * 响应错误函数
     * @param code 状态码
     * @param msg 返回消息
     * @return 错误消息
     */
    public static AjaxResult error(Integer code, String msg) {
        return new AjaxResult(code, msg);
    }

    /**
     * 响应错误函数
     * @param msg 返回消息
     * @param data 数据对象
     * @return 错误消息
     */
    public static AjaxResult error(String msg, Object data) {
        return new AjaxResult(500, msg, data);
    }

    /**
     * 响应错误函数
     * @return 错误消息
     */
    public static AjaxResult error() {
        return AjaxResult.error("操作失败!");
    }

    /**
     * 响应错误函数
     * @param data 数据对象
     * @return 错误消息
     */
    public static AjaxResult error(Object data) {
        return AjaxResult.error("操作失败!", data);
    }

    /**
     * 响应错误函数
     * @param msg 返回消息
     * @return 错误消息
     */
    public static AjaxResult error(String msg) {
        return AjaxResult.error(msg, null);
    }


    /**
     * 链式调用
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return 数据对象
     */
    @Override
    public AjaxResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}