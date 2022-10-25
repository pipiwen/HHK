package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 数据字典-地区对象 cfg_area
 * 
 * @author azx
 * @date 2021-05-03
 */
@Data
@TableName(value = "cfg_area")
public class CfgArea
{
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long code;

    /** 地区名称 */
    @Excel(name = "地区名称")
    private String name;

    /** 上级ID */
    @Excel(name = "上级ID")
    private Long parentId;

    /** $column.columnComment */
    @Excel(name = "上级ID")
    private String memo;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("parentId", getParentId())
            .append("memo", getMemo())
            .toString();
    }
}
