package com.rookie.aigc.service.bilibili;

import com.rookie.aigc.domain.vo.resp.SubtitleResult;

/**
 * @author eumenides
 * @description
 * @date 2024/4/9
 */
public interface BilibiliService {


    SubtitleResult fetchBilibiliSubtitle(String videoId, String pageNumber, Boolean shouldShowTimestamp);

}
