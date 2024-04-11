package com.rookie.aigc.domain.dto.bilibili;

import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/11
 */
@Data
public class BiliBiliCommentDetail {
    private Long id;

    private BiliBiliCommentUser user;

    private BilibiliCommentItem item;

    private Long atTime;

}
