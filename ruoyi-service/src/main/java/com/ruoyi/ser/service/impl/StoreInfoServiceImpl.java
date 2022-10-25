package com.ruoyi.ser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ser.domain.StoreInfo;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.mapper.StoreInfoMapper;
import com.ruoyi.ser.mapper.UserDetailMapper;
import com.ruoyi.ser.service.IStoreInfoService;
import com.ruoyi.ser.service.IUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author syw
 */
@Service
public class StoreInfoServiceImpl extends ServiceImpl<StoreInfoMapper, StoreInfo> implements IStoreInfoService {

    @Autowired
    private StoreInfoMapper storeInfoMapper;

    @Override
    public List<StoreInfo> listByCondition(StoreInfo storeInfo) {
        return storeInfoMapper.listByCondition(storeInfo);
    }
}
