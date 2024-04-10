package com.rookie.aigc.domain.dto.bilibili;

import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/10
 */
@Data
public class UnReadData {
    private Integer at;

    private Integer chat;

    private Integer like;

    private Integer reply;

    private Integer sysMsg;

    private Integer up;
}
