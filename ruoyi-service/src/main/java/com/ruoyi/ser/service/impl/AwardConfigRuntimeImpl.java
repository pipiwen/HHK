package com.ruoyi.ser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ser.domain.AwardConfig;
import com.ruoyi.ser.domain.AwardConfigRuntime;
import com.ruoyi.ser.mapper.AwardConfigMapper;
import com.ruoyi.ser.mapper.AwardConfigRuntimeMapper;
import com.ruoyi.ser.service.IAwardConfigRuntimeService;
import com.ruoyi.ser.service.IAwardConfigService;
import org.springframework.stereotype.Service;

/**
 * @author syw
 */
@Service
public class AwardConfigRuntimeImpl extends ServiceImpl<AwardConfigRuntimeMapper, AwardConfigRuntime> implements IAwardConfigRuntimeService {
}
