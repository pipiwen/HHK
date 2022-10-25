package com.ruoyi.ser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.ser.domain.CustomerAttention;
import com.ruoyi.ser.domain.SaleAttention;
import com.ruoyi.ser.domain.UserDetail;

import java.util.List;

/**
 * @author syw
 */
public interface ISaleAttentionService extends IService<SaleAttention> {
    /**
     * 关注列表 <br>
     *
     * @param userDetail <br>
     * @return UserDetail <br>
     **/
    List<UserDetail> listAttention(UserDetail userDetail);
}
