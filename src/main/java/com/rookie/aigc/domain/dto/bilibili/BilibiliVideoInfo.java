package com.rookie.aigc.domain.dto.bilibili;

import lombok.Data;

import java.util.List;

/**
 * @author eumenides
 * @description 存储视频字幕和视频信息
 * @date 2024/4/9
 */
@Data
public class BilibiliVideoInfo {

    private String title;
    private String desc;
    private String dynamic;
    private Subtitle subtitle;  // 使用Subtitle类来表示subtitle对象

}
