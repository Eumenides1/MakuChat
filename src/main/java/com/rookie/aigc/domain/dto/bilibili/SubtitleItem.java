package com.rookie.aigc.domain.dto.bilibili;

import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/10
 */
@Data
public class SubtitleItem {
    private double from;
    private double to;
    private int sid;
    private int location;
    private String content;
    private double music;
}
