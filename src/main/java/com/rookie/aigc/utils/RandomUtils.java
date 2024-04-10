package com.rookie.aigc.utils;

import java.util.List;
import java.util.Random;

/**
 * @author eumenides
 * @description 随机工具类
 * @date 2024/4/9
 */
public class RandomUtils {
    public static <T> T sample(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(list.size());
        return list.get(index);
    }
}
