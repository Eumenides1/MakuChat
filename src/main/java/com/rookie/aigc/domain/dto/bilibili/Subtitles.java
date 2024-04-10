package com.rookie.aigc.domain.dto.bilibili;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/9
 */
@Data
public class Subtitles {

    private String lan;

    @JsonProperty("subtitle_url")
    private String subtitleUrl;


}
