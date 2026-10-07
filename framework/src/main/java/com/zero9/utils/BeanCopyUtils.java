package com.zero9.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    /**
     * 单个对象拷贝
     */
    public static <T> T copy(Object source, Class<T> clazz) {
        if (source == null) return null;
        try {
            T target = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("对象拷贝失败", e);
        }
    }

    /**
     * 列表拷贝
     */
    public static <T> List<T> copyList(List<?> sourceList, Class<T> clazz) {
        if (sourceList == null || sourceList.isEmpty()) return List.of();
        return sourceList.stream()
                .map(item -> copy(item, clazz))
                .collect(Collectors.toList());
    }
}