package com.rookie.aigc.domain.dto.bilibili;

import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/9
 */
@Data
public class BilibiliSubtitleItem {
    // 开始时间
    private double from;
    // 字幕内容
    private String content;

    public BilibiliSubtitleItem(double from, String content) {
        this.from = from;
        this.content = content;
    }
}
