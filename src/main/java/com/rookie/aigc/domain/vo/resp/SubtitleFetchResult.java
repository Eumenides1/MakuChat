package com.rookie.aigc.domain.vo.resp;

import com.rookie.aigc.domain.dto.bilibili.SubtitleItem;
import lombok.Data;

import java.util.List;

/**
 * @author eumenides
 * @description
 * @date 2024/4/10
 */
@Data
public class SubtitleFetchResult {
    private double fontSize;
    private String fontColor;
    private double backgroundAlpha;
    private String backgroundColor;
    private String Stroke;
    private String type;
    private String lang;
    private String version;
    private List<SubtitleItem> body;
}
