package com.rookie.aigc.domain.dto.bilibili;

import lombok.Data;

import java.util.List;

/**
 * @author eumenides
 * @description
 * @date 2024/4/11
 */
@Data
public class BilibiliAtData {

    private BiliBiliCursor cursor;

    private List<BiliBiliCommentDetail> items;

}
