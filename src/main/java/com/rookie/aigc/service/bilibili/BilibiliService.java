package com.rookie.aigc.service.bilibili;

import com.rookie.aigc.domain.dto.bilibili.BiliBiliUnRead;
import com.rookie.aigc.domain.vo.req.BiliBiliReplyReq;
import com.rookie.aigc.domain.vo.resp.BilibiliAtResp;
import com.rookie.aigc.domain.vo.resp.SubtitleResult;

/**
 * @author eumenides
 * @description
 * @date 2024/4/9
 */
public interface BilibiliService {

    /**
     * 精炼视频字幕
     * @param videoId
     * @param pageNumber
     * @param shouldShowTimestamp
     * @return
     */
    SubtitleResult fetchBilibiliSubtitle(String videoId, String pageNumber, Boolean shouldShowTimestamp);

    /**
     * 获取实时 @数量
     * @return
     */
    BiliBiliUnRead getRuntimeAtNum();

    /**
     * 获取艾特消息列表
     * @return
     */
    BilibiliAtResp getAtMsgList();

    /**
     * 自动回复
     */
    void reply(BiliBiliReplyReq req);

    /**
     * 自动总结
     * @param bvNum
     * @return
     */
    String autoSummarize(String bvNum);


    String fetchBilibiliSubtitleUrls(String videoId, String pageNumber);

}
