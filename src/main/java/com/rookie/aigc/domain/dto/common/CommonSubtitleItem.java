package com.rookie.aigc.domain.dto.common;

import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/9
 */
@Data
public class CommonSubtitleItem {
    private String text;
    private int index;
    // 可以是秒数，这里用Double来容纳可能的小数点
    private Double s;

    public CommonSubtitleItem(String text, int index, Double s) {
        this.text = text;
        this.index = index;
        this.s = s;
    }
}
