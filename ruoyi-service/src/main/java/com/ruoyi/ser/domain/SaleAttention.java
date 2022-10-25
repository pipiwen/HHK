package com.ruoyi.ser.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author syw
 */
@Data
@TableName(value = "sale_cust_attention")
public class SaleAttention {

    private Long custId;

    private Long saleId;

}
