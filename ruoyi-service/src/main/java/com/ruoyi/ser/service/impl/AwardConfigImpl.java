package com.ruoyi.ser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ser.domain.AwardConfig;
import com.ruoyi.ser.domain.OrderRecord;
import com.ruoyi.ser.mapper.AwardConfigMapper;
import com.ruoyi.ser.mapper.OrderRecordMapper;
import com.ruoyi.ser.service.IAwardConfigService;
import com.ruoyi.ser.service.IOrderRecordService;
import org.springframework.stereotype.Service;

/**
 * @author syw
 */
@Service
public class AwardConfigImpl extends ServiceImpl<AwardConfigMapper, AwardConfig> implements IAwardConfigService {
}
