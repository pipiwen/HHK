package com.ruoyi.ser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ser.domain.UserDetail;

import java.util.List;

/**
 * @author syw
 */
public interface UserDetailMapper extends BaseMapper<UserDetail> {

    /**
     * 条件查询 <br>
     *
     * @param userDetail <br>
     * @return UserDetail <br>
     **/
    List<UserDetail> listByCondition(UserDetail userDetail);

    /**
     * Description <br>
     * @param userName <br>
     * @return com.ruoyi.ser.domain.UserDetail <br>
     **/
    UserDetail getCustomerDetail(String userName);

    /**
     * Description <br>
     * @param userId <br>
     * @return com.ruoyi.ser.domain.UserDetail <br>
     **/
    UserDetail getUserDetail(Long userId);

    /**
     * Description <br>
     * @param userDetail <br>
     **/
    void updateBalanceBatch (UserDetail userDetail);

    /**
     * Description <br>
     * @param userDetail <br>
     **/
    void updateAccountBalance(UserDetail userDetail);

}
