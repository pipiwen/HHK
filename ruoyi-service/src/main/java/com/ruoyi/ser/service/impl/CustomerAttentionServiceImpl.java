package com.ruoyi.ser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ser.domain.CustomerAttention;
import com.ruoyi.ser.domain.StoreInfo;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.mapper.CustomerAttentionMapper;
import com.ruoyi.ser.mapper.StoreInfoMapper;
import com.ruoyi.ser.service.ICustomerAttentionService;
import com.ruoyi.ser.service.IStoreInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author syw
 */
@Service
public class CustomerAttentionServiceImpl extends ServiceImpl<CustomerAttentionMapper, CustomerAttention> implements ICustomerAttentionService {

    @Resource
    private CustomerAttentionMapper customerAttentionMapper;
    @Override
    public List<UserDetail> listAttention(UserDetail userDetail) {
        return customerAttentionMapper.listAttention(userDetail);
    }
}
