package com.zero9.utils;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.client.RestTemplate;

public class IpRegionUtils {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static String getRegion(String ip) {
        String url = "http://ip-api.com/json/" + ip + "?lang=zh-CN";

        try {
            String result = restTemplate.getForObject(url, String.class);
            JSONObject json = JSONObject.parseObject(result);

            if ("success".equals(json.getString("status"))) {
                return json.getString("country")
                        + json.getString("regionName")
                        + json.getString("city");
            }
        } catch (Exception e) {
            return "未知地区";
        }

        return "未知地区";
    }
}