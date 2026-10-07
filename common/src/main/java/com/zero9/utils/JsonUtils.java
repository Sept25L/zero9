package com.zero9.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import java.util.List;

public class JsonUtils {
    private JsonUtils() {}

    public static String  toJSONString(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.IgnoreEmpty);
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static <T> List<T> parseList(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    public static <T> T parse(String json, TypeReference<T> ref) {
        return JSON.parseObject(json, ref);
    }

    public static boolean isValid(String json) {
        return JSON.isValid(json);
    }

}