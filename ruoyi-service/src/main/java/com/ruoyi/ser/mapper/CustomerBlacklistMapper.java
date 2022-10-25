package com.ruoyi.ser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ser.domain.CustomerBlacklist;
import com.ruoyi.ser.domain.UserDetail;

import java.util.List;

/**
 * @author syw
 */
public interface CustomerBlacklistMapper extends BaseMapper<CustomerBlacklist> {

    /**
     * 黑名单列表 <br>
     *
     * @param userDetail <br>
     * @return UserDetail <br>
     **/
    List<UserDetail> listBlacklist(UserDetail userDetail);
}
