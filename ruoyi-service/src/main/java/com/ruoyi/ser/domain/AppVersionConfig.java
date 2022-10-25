package com.ruoyi.ser.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;


/**
 * @author syw
 */
@Data
@TableName("app_version_config")
public class AppVersionConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer versionCode;

    private String versionName;

    private String description;

    private String downloadUrl;

    private Integer versionType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date endTime;

}
