package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.ser.domain.AwardConfig;
import com.ruoyi.ser.domain.AwardConfigRuntime;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.service.IAwardConfigRuntimeService;
import com.ruoyi.ser.service.IAwardConfigService;
import com.ruoyi.ser.service.IUserDetailService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author syw
 */
@Controller
@RequestMapping("/system/award")
public class SysAwardController extends BaseController {

    private String prefix = "system/award";

    @Autowired
    private IAwardConfigService awardConfigService;

    @Autowired
    private IAwardConfigRuntimeService awardConfigRuntimeService;


    @RequiresPermissions("system:award:view")
    @GetMapping()
    public String award()
    {
        return prefix + "/award";
    }

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AwardConfig awardConfig) {
        List<AwardConfig> list = awardConfigService.list();
        return getDataTable(list);
    }

    /**
     * 新增活动配置
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    @GetMapping("/edit/{awardId}")
    public String edit(@PathVariable("awardId") Long awardId, ModelMap mmap)
    {
        mmap.put("award", awardConfigService.getById(awardId));
        return "system/award/edit";
    }

    @PostMapping("/add")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult add(AwardConfig awardConfig) {
        awardConfig.setCreateTime(new Date());
        if (awardConfig.getAwardMax().compareTo(awardConfig.getAwardMin()) < 0) {
            return AjaxResult.error("最小金额无法大于最大值");
        }
        if (awardConfig.getAwardStartTime().isAfter(awardConfig.getAwardEndTime())) {
            return AjaxResult.error("时间设置错误");
        }
        awardConfigService.save(awardConfig);
        awardConfigRuntimeService.save(convert2Runtime(awardConfig));
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult edit(AwardConfig awardConfig) {
        // 判断活动是否开始开始则无法更改
        AwardConfig config = awardConfigService.getById(awardConfig.getId());
        if (LocalDateTime.now().isAfter(config.getAwardStartTime())) {
            return AjaxResult.error("活动已开始无法更改！");
        }
        awardConfig.setUpdateTime(new Date());
        awardConfigService.updateById(awardConfig);
        // 同步至runtime
        AwardConfig config1 = awardConfigService.getById(awardConfig.getId());
        awardConfigRuntimeService.updateById(convert2Runtime(config1));
        return AjaxResult.success();
    }


    @PostMapping("/remove")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult remove(String ids) {
        Long id = Long.valueOf(ids);
        AwardConfig config = awardConfigService.getById(id);
        if (LocalDateTime.now().isAfter(config.getAwardStartTime()) &&
                LocalDateTime.now().isBefore(config.getAwardEndTime())
        ) {
            return AjaxResult.error("活动进行中无法删除！");
        }
        awardConfigService.removeById(id);
        awardConfigRuntimeService.removeById(id);
        return AjaxResult.success();
    }

    private AwardConfigRuntime convert2Runtime(AwardConfig awardConfig) {
        AwardConfigRuntime awardConfigRuntime = new AwardConfigRuntime();
        awardConfigRuntime.setConfigId(awardConfig.getId());
        awardConfigRuntime.setAwardName(awardConfig.getAwardName());
        awardConfigRuntime.setAwardNum(awardConfig.getAwardNum());
        awardConfigRuntime.setAwardTotal(awardConfig.getAwardTotal());
        awardConfigRuntime.setAwardMax(awardConfig.getAwardMax());
        awardConfigRuntime.setAwardMin(awardConfig.getAwardMin());
        awardConfigRuntime.setAwardStartTime(awardConfig.getAwardStartTime());
        awardConfigRuntime.setAwardEndTime(awardConfig.getAwardEndTime());
        awardConfigRuntime.setCreateTime(awardConfig.getCreateTime());
        return awardConfigRuntime;
    }

}
