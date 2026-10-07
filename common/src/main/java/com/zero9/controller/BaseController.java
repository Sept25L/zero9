package com.zero9.controller;

import com.zero9.domain.AjaxResult;

public class BaseController {

    /*成功返回（无数据）*/
    public AjaxResult success() {
        return AjaxResult.success();
    }

    /*成功返回（带消息，无数据）*/
    public AjaxResult success(String msg) {
        return AjaxResult.success(msg);
    }

    /*成功返回（带数据）*/
    public AjaxResult success(Object data) {
        return AjaxResult.success(data);
    }

    /*失败返回（无数据）*/
    public AjaxResult error() {
        return AjaxResult.error();
    }

    /*失败返回（带消息）*/
    public AjaxResult error(String msg) {
        return AjaxResult.error(msg);
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
