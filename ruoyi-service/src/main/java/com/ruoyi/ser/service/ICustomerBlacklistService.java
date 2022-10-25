package com.ruoyi.ser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.ser.domain.CustomerAttention;
import com.ruoyi.ser.domain.CustomerBlacklist;
import com.ruoyi.ser.domain.UserDetail;

import java.util.List;

/**
 * @author syw
 */
public interface ICustomerBlacklistService extends IService<CustomerBlacklist> {

    /**
     * 黑名单列表 <br>
     *
     * @param userDetail <br>
     * @return UserDetail <br>
     **/
    List<UserDetail> listBlacklist(UserDetail userDetail);
}
