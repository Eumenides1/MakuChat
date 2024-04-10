package com.rookie.aigc.domain.vo.req;

import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/10
 */
@Data
public class SummarizeReq {
    private VideoConfig videoConfig;
    private UserConfig userConfig;
}
