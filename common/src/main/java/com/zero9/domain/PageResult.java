package com.zero9.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页
     */
    private Long pageNum;

    /**
     * 每页大小
     */
    private Long pageSize;

    /**
     * 数据列表
     */
    private List<T> records;

    public static <T> PageResult<T> build(IPage<T> page){
        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setRecords(page.getRecords());
        return result;
    }
}