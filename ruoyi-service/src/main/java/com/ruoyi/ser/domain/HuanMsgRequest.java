package com.ruoyi.ser.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * @author syw
 */
@Data
public class HuanMsgRequest{

    private String grant_type;

    private String client_id;

    private String client_secret;

    private String ttl;
}
