package com.ruoyi.ser.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author syw
 */
@Data
@TableName("subs_plan")
public class SubsPlan extends BaseEntity {

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String subsName;

    /** 1:天2:月3:年 */
    private Integer subsUnit;

    private Integer subsDuring;

    private BigDecimal subsPrice;

    private String description;

    private String status;
}
