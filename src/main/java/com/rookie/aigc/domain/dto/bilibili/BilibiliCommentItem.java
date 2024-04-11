package com.rookie.aigc.domain.dto.bilibili;

import lombok.Data;

import java.util.List;

/**
 * @author eumenides
 * @description
 * @date 2024/4/11
 */
@Data
public class BilibiliCommentItem {

    private String type;

    private String business;

    private Integer  businessId;

    private String title;

    private String image;

    private String uri;

    private Long subjectId;

    private Integer rootId;

    private Long targetId;

    private Long sourceId;

    private String sourceContent;

    private String nativeUri;

    private List<BiliBiliCommentUser> atDetails;

    //TODO 这里的类型未知
    private List topicDetails;

    private Boolean hideReplyButton;

}

