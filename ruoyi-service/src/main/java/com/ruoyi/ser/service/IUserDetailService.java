package com.ruoyi.ser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.ser.domain.UserDetail;

import java.util.List;

/**
 * @author syw
 */
public interface IUserDetailService extends IService<UserDetail> {

    /**
     * 条件查询 <br>
     * @param userDetail <br>
     * @return UserDetail <br>
     **/
    List<UserDetail> listByCondition(UserDetail userDetail);

    /**
     * Description <br>

     * @param userName <br>
     * @return UserDetail <br>
     **/
    UserDetail getCustomerDetail(String userName);

    /**
     * Description <br>

     * @param userId <br>
     * @return UserDetail <br>
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
