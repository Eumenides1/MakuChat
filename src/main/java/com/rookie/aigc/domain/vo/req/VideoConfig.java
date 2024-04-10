package com.rookie.aigc.domain.vo.req;

import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/10
 */
@Data
public class VideoConfig {

    private String videoId;

    private Boolean enableStream;

    private Boolean showTimestamp;

    private Boolean showEmoji;

    private String outputLanguage;

    private Integer detailLevel;

    private Integer sentenceNumber;

    private Integer outlineLevel;


}
