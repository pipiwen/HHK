package com.ruoyi.ser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ser.domain.GreetInst;
import com.ruoyi.ser.domain.UserDetail;

import java.util.List;

/**
 * @author syw
 */
public interface GreetInstMapper extends BaseMapper<GreetInst> {

    /***
     * 条件查询 <br>
     *
     * @author shi.yuwen <br>
     * @param userDetail <br>
     * @return UserDetail <br>
     **/
    List<UserDetail> listByCondition(UserDetail userDetail);

    /***
     * 邀请中(销售端) <br>
     *
     * @author shi.yuwen <br>
     * @param userDetail <br>
     * @return UserDetail <br>
     **/
    List<UserDetail> saleInvitationList(UserDetail userDetail);

    /***
     * 请求中(顾客端) <br>
     *
     * @author shi.yuwen <br>
     * @param userDetail <br>
     * @return UserDetail <br>
     **/
    List<UserDetail> customerInvitationList(UserDetail userDetail);

}
