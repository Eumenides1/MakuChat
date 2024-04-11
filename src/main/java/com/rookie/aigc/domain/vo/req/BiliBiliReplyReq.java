package com.rookie.aigc.domain.vo.req;

import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/11
 */
@Data
public class BiliBiliReplyReq {

    private Integer type;

    private Long oid;

    private Long root;

    private Long parent;

    private String message;

    private String csrf;

    private Integer plat;


}
