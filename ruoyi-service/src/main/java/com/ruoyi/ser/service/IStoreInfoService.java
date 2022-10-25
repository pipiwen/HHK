package com.ruoyi.ser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.ser.domain.StoreInfo;
import com.ruoyi.ser.domain.UserDetail;

import java.util.List;

/**
 * @author syw
 */
public interface IStoreInfoService extends IService<StoreInfo> {

    /**
     * Description <br>
     *
     * @author shi.yuwen <br>
     * @param storeInfo <br>
     * @return StoreInfo <br>
     **/
    List<StoreInfo> listByCondition(StoreInfo storeInfo);
}
