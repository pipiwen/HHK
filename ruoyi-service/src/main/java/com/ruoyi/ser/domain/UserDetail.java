package com.ruoyi.ser.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author syw
 */
@Data
@TableName(value = "user_detail")
public class UserDetail extends BaseEntity {
    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String nickName;

    /** 用户类型:02销售 03顾客 */
    private String userType;

    private Integer sex;

    private Integer age;

    private String idCard;

    private Integer city;

    private Integer area;

    private String tag;

    private String shops;

    private String invitationCode;

    private String birthday;

    private String industryType;

    private Double lat;

    private Double lon;

    @TableField(exist = false)
    private String industryTypeName;

    private String photos;

    @TableField(exist = false)
    private List<String> tagList;

    @TableField(exist = false)
    private List<String> industryTypeList;

    @TableField(exist = false)
    private List<StoreInfo> shopList;

    @TableField(exist = false)
    private String distance;

    private Integer isVip;

    private LocalDate vipTime;

    /** 通讯次数(女) */
    private Integer socketNumFemale;

    /** 通讯次数(男) */
    private Integer socketNumMale;

    private Integer socketCurrentNumMale;

    private Integer socketCurrentNumFemale;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String phonenumber;

    /** 账户余额*/
    private BigDecimal accountBalance;

    /** 1:黑名单； 0：不在黑名单*/
    @TableField(exist = false)
    private Integer isBlacklist;

    /** 1:关注； 0：未关注*/
    @TableField(exist = false)
    private Integer isAttention;

    private String generation;

    /** 个性签名*/
    private String description;

    /** 上班时间*/
    private String workingTime;

    /** 下班时间*/
    private String restTime;

    /** 归属地*/
    private String attribution;

    /** 是否导入数据*/
    private Integer isImport;

    /** 1:未支付； 2已支付；3：支付完成（同意聊天） 4:支付超时退款； 5:退款(拒绝沟通) */
    @TableField(exist = false)
    private Integer greetStatus;

    @TableField(exist = false)
    private Integer greetType;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date greetTime;

    @TableField(exist = false)
    private Long greetId;

    @TableField(exist = false)
    private Boolean hasRedPacket;

    @TableField(exist = false)
    private Integer greetSuccessCount;

    /** 打招呼支付方式*/
    @TableField(exist = false)
    private Integer payType;


}
