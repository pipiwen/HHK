package com.ruoyi.ser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.Reconciliation;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.mapper.UserDetailMapper;
import com.ruoyi.ser.service.IUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author syw
 */
@Service
public class UserDetailServiceImpl extends ServiceImpl<UserDetailMapper, UserDetail> implements IUserDetailService {

    @Resource
    private UserDetailMapper userDetailMapper;

    @Override
    public List<UserDetail> listByCondition(UserDetail userDetail) {

        return userDetailMapper.listByCondition(userDetail);
    }

    @Override
    public UserDetail getCustomerDetail(String userName) {

        return userDetailMapper.getCustomerDetail(userName);
    }

    @Override
    public UserDetail getUserDetail(Long userId) {

        return userDetailMapper.getUserDetail(userId);
    }

    @Override
    public void updateBalanceBatch(UserDetail userDetail) {
        userDetailMapper.updateBalanceBatch(userDetail);
    }

    @Override
    @Reconciliation
    public void updateAccountBalance(UserDetail userDetail) {
        userDetailMapper.updateAccountBalance(userDetail);
    }
}
