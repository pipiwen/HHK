package com.ruoyi.ser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.ser.domain.CustomerAttention;
import com.ruoyi.ser.domain.StoreInfo;
import com.ruoyi.ser.domain.UserDetail;

import java.util.List;

/**
 * @author syw
 */
public interface ICustomerAttentionService extends IService<CustomerAttention> {
    /**
     * 关注列表 <br>
     *
     * @param userDetail <br>
     * @return UserDetail <br>
     **/
    List<UserDetail> listAttention(UserDetail userDetail);
}
