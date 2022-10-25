package com.ruoyi.ser.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author syw
 */
@Data
@TableName(value = "sale_cust_blacklist")
public class SaleBlacklist {

    private Long custId;

    private Long saleId;

}
