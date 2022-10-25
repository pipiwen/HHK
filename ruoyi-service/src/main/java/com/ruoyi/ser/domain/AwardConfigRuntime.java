package com.ruoyi.ser.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author syw
 */
@Data
@TableName("award_config_runtime")
public class AwardConfigRuntime extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long configId;

    /** 奖励名称 */
    private String awardName;

    /** 奖励总额 */
    private BigDecimal awardTotal;

    /** 奖励个数 */
    private Integer awardNum;

    /** 奖励最小值 */
    private BigDecimal awardMin;

    /** 奖励最大值 */
    private BigDecimal awardMax;

    /** 奖励开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime awardStartTime;

    /** 奖励结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime awardEndTime;


}
