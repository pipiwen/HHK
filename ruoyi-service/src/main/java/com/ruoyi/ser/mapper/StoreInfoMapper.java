package com.ruoyi.ser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.ser.domain.StoreInfo;
import com.ruoyi.ser.domain.UserDetail;

import java.util.List;

/**
 * @author syw
 */
public interface StoreInfoMapper extends BaseMapper<StoreInfo> {

    /**
     * Description <br>
     *
     * @param storeInfo <br>
     * @return StoreInfo <br>
     **/
    List<StoreInfo> listByCondition(StoreInfo storeInfo);
}
