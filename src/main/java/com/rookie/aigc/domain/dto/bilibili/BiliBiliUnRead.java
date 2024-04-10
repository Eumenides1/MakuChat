package com.rookie.aigc.domain.dto.bilibili;

import lombok.Data;

/**
 * @author eumenides
 * @description BiliBili 用户未读消息
 * @date 2024/4/10
 */
@Data
public class BiliBiliUnRead {

    private Integer code;

    private String message;

    private Integer ttl;

    private UnReadData data;


}
