package com.ruoyi.ser.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author syw
 */
@Data
@TableName(value = "cust_sale_attention")
public class CustomerAttention {

    private Long custId;

    private Long saleId;

}
