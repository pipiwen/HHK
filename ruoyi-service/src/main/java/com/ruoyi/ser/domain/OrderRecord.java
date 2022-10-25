package com.ruoyi.ser.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;

/**
 * @author syw
 */
@Data
@TableName("order_record")
public class OrderRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long productId;

    private Long storeId;

    /** 1:充值服务 2:提现*/
    private Integer productType;

    private String orderNum;

    /** 订单状态(W:待支付 F:支付完成 T:超时)*/
    private String orderStatus;

    private BigDecimal totalAmount;

    /** 1:开通 2:续费*/
    @TableField(exist = false)
    private Integer orderType;

    /** 提现金额*/
    @TableField(exist = false)
    private BigDecimal withdrawal;

    /** 真实姓名*/
    @TableField(exist = false)
    private String realName;

    /** 支付宝账号*/
    @TableField(exist = false)
    private String aliAccount;

    private Long greetId;

    @TableField(exist = false)
    private String msgCode;


}
