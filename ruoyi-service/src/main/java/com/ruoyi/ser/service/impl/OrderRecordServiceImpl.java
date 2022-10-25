package com.ruoyi.ser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.ser.domain.OrderRecord;
import com.ruoyi.ser.domain.SubsPlan;
import com.ruoyi.ser.mapper.OrderRecordMapper;
import com.ruoyi.ser.mapper.SubsPlanMapper;
import com.ruoyi.ser.service.IOrderRecordService;
import com.ruoyi.ser.service.ISubsPlanService;
import org.springframework.stereotype.Service;

/**
 * @author syw
 */
@Service
public class OrderRecordServiceImpl extends ServiceImpl<OrderRecordMapper, OrderRecord> implements IOrderRecordService {
}
