package com.rookie.aigc.controller;

import com.rookie.aigc.domain.dto.bilibili.BiliBiliCommentDetail;
import com.rookie.aigc.domain.dto.bilibili.BiliBiliUnRead;
import com.rookie.aigc.domain.vo.req.BiliBiliReplyReq;
import com.rookie.aigc.domain.vo.resp.BilibiliAtResp;
import com.rookie.aigc.service.bilibili.BilibiliService;
import com.rookie.aigc.utils.BilibiliBVExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eumenides
 * @description
 * @date 2024/4/9
 */
@RestController
@RequestMapping("/v1/chat")
@EnableScheduling
public class ChatController {
    public static final String KEY_WORD = "总结";

    @Autowired
    private BilibiliService bilibiliService;

    @Scheduled(cron = "0 */1 * * * ?")
    public void getUnRead(){
        BiliBiliUnRead runtimeAtNum = bilibiliService.getRuntimeAtNum();
        if (runtimeAtNum.getCode() == 0) {
            System.out.println(runtimeAtNum.getData().getAt());
            if (runtimeAtNum.getData().getAt() > 0) {
                BilibiliAtResp atMsgList = bilibiliService.getAtMsgList();
                BiliBiliCommentDetail biliBiliCommentDetail = atMsgList.getData().getItems().get(0);
                if (biliBiliCommentDetail.getItem().getSourceContent().contains(KEY_WORD)){
                    BiliBiliReplyReq req = new BiliBiliReplyReq();
                    // TODO 抽离枚举
                    req.setType(1);
                    req.setPlat(1);
                    req.setOid(biliBiliCommentDetail.getItem().getSubjectId());
                    req.setRoot(biliBiliCommentDetail.getItem().getSubjectId());
                    req.setParent(biliBiliCommentDetail.getItem().getSourceId());
                    // 获取 video id
                    String bvNum = BilibiliBVExtractor.getBvNum(biliBiliCommentDetail.getItem().getUri());
                    req.setMessage(bilibiliService.autoSummarize(bvNum));
                    bilibiliService.reply(req);
                }
            }
        }
    }





}
