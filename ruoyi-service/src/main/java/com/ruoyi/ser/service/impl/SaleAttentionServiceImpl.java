package com.ruoyi.ser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ser.domain.CustomerAttention;
import com.ruoyi.ser.domain.SaleAttention;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.mapper.CustomerAttentionMapper;
import com.ruoyi.ser.mapper.SaleAttentionMapper;
import com.ruoyi.ser.service.ICustomerAttentionService;
import com.ruoyi.ser.service.ISaleAttentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author syw
 */
@Service
public class SaleAttentionServiceImpl extends ServiceImpl<SaleAttentionMapper, SaleAttention> implements ISaleAttentionService {

    @Resource
    private SaleAttentionMapper saleAttentionMapper;

    @Override
    public List<UserDetail> listAttention(UserDetail userDetail) {
        return saleAttentionMapper.listAttention(userDetail);
    }
}
