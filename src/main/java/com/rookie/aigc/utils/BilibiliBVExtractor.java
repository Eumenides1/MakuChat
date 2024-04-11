package com.rookie.aigc.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author eumenides
 * @description
 * @date 2024/4/11
 */
public class BilibiliBVExtractor {
    /**
     * 获取 BiliBili 视频 BV 号
     * @param url 视频地址
     * @return 视频 BV 号
     */
    public static String getBvNum(String url){
        Pattern pattern = Pattern.compile("BV([a-zA-Z0-9]+)\\b");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
