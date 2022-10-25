package com.ruoyi.ser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ser.domain.CustomerBlacklist;
import com.ruoyi.ser.domain.SaleBlacklist;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.mapper.CustomerBlacklistMapper;
import com.ruoyi.ser.mapper.SaleBlacklistMapper;
import com.ruoyi.ser.service.ICustomerBlacklistService;
import com.ruoyi.ser.service.ISaleBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author syw
 */
@Service
public class SaleBlacklistServiceImpl extends ServiceImpl<SaleBlacklistMapper, SaleBlacklist> implements ISaleBlacklistService {

    @Resource
    private SaleBlacklistMapper saleBlacklistMapper;

    @Override
    public List<UserDetail> listBlacklist(UserDetail userDetail) {

        return saleBlacklistMapper.listBlacklist(userDetail);
    }
}
