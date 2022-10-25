package com.ruoyi.ser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ser.domain.CustomerAttention;
import com.ruoyi.ser.domain.SaleAttention;
import com.ruoyi.ser.domain.UserDetail;

import java.util.List;

/**
 * @author syw
 */
public interface SaleAttentionMapper extends BaseMapper<SaleAttention> {

    /**
     * 关注列表 <br>
     *
     * @param userDetail <br>
     * @return UserDetail <br>
     **/
    List<UserDetail> listAttention(UserDetail userDetail);
}
