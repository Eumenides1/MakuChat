package com.rookie.aigc.domain.vo.resp;

import com.rookie.aigc.domain.dto.common.CommonSubtitleItem;
import lombok.Data;

import java.util.List;

/**
 * @author eumenides
 * @description
 * @date 2024/4/9
 */
@Data
public class SubtitleResult {
    private String title;
    private List<CommonSubtitleItem> subtitlesArray;
    private String descriptionText;

    public SubtitleResult(String title, List<CommonSubtitleItem> subtitlesArray, String descriptionText) {
        this.title = title;
        this.subtitlesArray = subtitlesArray;
        this.descriptionText = descriptionText;
    }
}
