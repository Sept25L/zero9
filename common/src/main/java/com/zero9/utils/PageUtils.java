package com.zero9.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zero9.domain.PageResult;

public class PageUtils {

    public static <T> PageResult<T> build(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        return result;
    }
}