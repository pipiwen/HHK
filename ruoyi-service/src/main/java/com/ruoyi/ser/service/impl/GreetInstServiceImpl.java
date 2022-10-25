package com.ruoyi.ser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ser.domain.GreetInst;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.mapper.GreetInstMapper;
import com.ruoyi.ser.service.IGreetInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author syw
 */
@Service
public class GreetInstServiceImpl extends ServiceImpl<GreetInstMapper, GreetInst> implements IGreetInstService {

    @Resource
    private GreetInstMapper greetInstMapper;
    @Override
    public List<UserDetail> listByCondition(UserDetail userDetail) {
        return greetInstMapper.listByCondition(userDetail);
    }

    @Override
    public List<UserDetail> saleInvitationList(UserDetail userDetail) {
        return greetInstMapper.saleInvitationList(userDetail);
    }

    @Override
    public List<UserDetail> customerInvitationList(UserDetail userDetail) {
        return greetInstMapper.customerInvitationList(userDetail);
    }
}
