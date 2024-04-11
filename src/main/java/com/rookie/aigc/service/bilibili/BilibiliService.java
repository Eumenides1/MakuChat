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


    SubtitleResult fetchBilibiliSubtitle(String videoId, String pageNumber, Boolean shouldShowTimestamp);

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

    String autoSummarize(String bvNum);

}
