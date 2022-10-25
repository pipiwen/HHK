package com.ruoyi.ser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ser.domain.CustomerAttention;
import com.ruoyi.ser.domain.CustomerBlacklist;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.mapper.CustomerAttentionMapper;
import com.ruoyi.ser.mapper.CustomerBlacklistMapper;
import com.ruoyi.ser.service.ICustomerAttentionService;
import com.ruoyi.ser.service.ICustomerBlacklistService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author syw
 */
@Service
public class CustomerBlacklistServiceImpl extends ServiceImpl<CustomerBlacklistMapper, CustomerBlacklist> implements ICustomerBlacklistService {

    @Resource
    private CustomerBlacklistMapper customerBlacklistMapper;
    @Override
    public List<UserDetail> listBlacklist(UserDetail userDetail) {
        return customerBlacklistMapper.listBlacklist(userDetail);
    }
}
