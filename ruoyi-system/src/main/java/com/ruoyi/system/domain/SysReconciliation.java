package com.ruoyi.system.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author syw
 */
@Data
public class SysReconciliation {

    private Long id;

    private Long userId;

    private String operation;

    private String accountBalance;

    private String remark;

    private Date createTime;

}
