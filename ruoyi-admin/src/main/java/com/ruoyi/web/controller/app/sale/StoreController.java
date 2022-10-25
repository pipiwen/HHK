package com.ruoyi.web.controller.app.sale;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.ser.domain.StoreInfo;
import com.ruoyi.ser.service.IStoreInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author syw
 */
@RestController
@RequestMapping("/app/store")
public class StoreController extends BaseController {

    @Autowired
    private IStoreInfoService storeInfoService;

    /** 保存提交 */
    @PostMapping("/add")
    @RepeatSubmit
    public AjaxResult add(@RequestBody StoreInfo storeInfo) {
        storeInfo.setCreateTime(DateUtils.getNowDate());
        storeInfo.setUserId(ShiroUtils.getUserId());
        storeInfo.setStatus(UserConstants.SYS_AUDIT_WAIT);
        storeInfoService.save(storeInfo);
        return AjaxResult.successApp();
    }

    @PostMapping("/edit")
    @RepeatSubmit
    public AjaxResult edit(@RequestBody StoreInfo storeInfo) {
        storeInfo.setUpdateTime(DateUtils.getNowDate());
        storeInfo.setUserId(ShiroUtils.getUserId());
        storeInfo.setStatus(UserConstants.SYS_AUDIT_WAIT);
        storeInfoService.updateById(storeInfo);
        return AjaxResult.successApp();
    }

    @GetMapping("/listEffective")
    public TableDataInfo listEffective() {
        List<StoreInfo> list = storeInfoService.list(new QueryWrapper<StoreInfo>()
                .eq("user_id", ShiroUtils.getUserId())
                .eq("status", UserConstants.SYS_AUDIT_PASS)
                .isNotNull("valid_time")
                .ge("valid_time", LocalDate.now())
        );
        return getDataTableApp(list);
    }
}
