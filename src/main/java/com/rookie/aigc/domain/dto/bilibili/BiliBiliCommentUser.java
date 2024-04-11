package com.rookie.aigc.domain.dto.bilibili;

import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/11
 */
@Data
public class BiliBiliCommentUser {

    private Long mid;

    private Integer fans;

    private String nickname;

    private String avatar;

    private String midLink;

    private Boolean follow;
}
