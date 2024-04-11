package com.rookie.aigc.domain.vo.resp;

import com.rookie.aigc.domain.dto.bilibili.BilibiliAtData;
import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/11
 */
@Data
public class BilibiliAtResp {

    private Integer code;

    private String message;

    private Integer ttl;

    private BilibiliAtData data;

}
