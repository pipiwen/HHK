package com.ruoyi.ser.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author syw
 */
@Data
@TableName("greet_inst")
public class GreetInst extends BaseEntity {

    private Long id;

    /** 发起者id */
    private Long launchId;

    /** 聊天对象id */
    private Long targetId;

    /** 1:销售发起 2：客户发起 */
    private Integer greetType;

    /** 红包金额 */
    private BigDecimal greetAmount;

    /** 1:未支付； 2已支付；3：支付完成（同意聊天） 4:支付超时退款； 5:退款(拒绝沟通) */
    private Integer greetStatus;

    /** 1:支付宝支付 2：余额支付 */
    private Integer payType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date greetTime;



}
