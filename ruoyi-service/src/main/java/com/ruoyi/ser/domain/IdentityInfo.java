package com.ruoyi.ser.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * @author syw
 */
@Data
public class IdentityInfo extends BaseEntity {
    private String idCard;

    private String phone;

    private String realName;
}
