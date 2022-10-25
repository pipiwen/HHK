package com.ruoyi.ser.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.framework.shiro.web.session.SpringSessionValidationScheduler;
import lombok.Data;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author syw
 */
@Data
@TableName(value = "store_info_record")
public class StoreInfo extends BaseEntity {

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String storeName;

    private Integer city;

    private Integer area;

    private String license;

    private Integer status;

    private String address;

    @TableField(exist = false)
    private String addressName;

    private String reason;

    private Date examineTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validTime;

    private Double lat;

    private Double lon;

    @TableField(exist = false)
    private Date startTime;

    @TableField(exist = false)
    private Date endTime;

    @TableField(exist = false)
    private String phonenumber;
}
