package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.ser.domain.AppVersionConfig;
import com.ruoyi.ser.domain.AwardConfig;
import com.ruoyi.ser.service.IAppVersionConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author syw
 */
@Controller
@RequestMapping("/system/version")
public class VersionController extends BaseController {

    @Autowired
    private IAppVersionConfigService appVersionConfigService;

    @RequiresPermissions("system:version:view")
    @GetMapping()
    public String version()
    {
        return "system/version/version";
    }

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AppVersionConfig appVersionConfig)
    {
        startPage();
        List<AppVersionConfig> list = appVersionConfigService.list(new QueryWrapper<AppVersionConfig>()
                .eq(StringUtils.isNotEmpty(appVersionConfig.getVersionName()), "version_name", appVersionConfig.getVersionName())
                .ge(null != appVersionConfig.getStartTime(),"create_time", appVersionConfig.getStartTime())
                .lt(null != appVersionConfig.getEndTime(), "create_time", appVersionConfig.getEndTime())
        );
        return getDataTable(list);
    }

    /**
     * 新增活动配置
     */
    @GetMapping("/add")
    public String add()
    {
        return "system/version/add";
    }

    @GetMapping("/edit/{versionId}")
    public String edit(@PathVariable("versionId") Long versionId, ModelMap mmap)
    {
        mmap.put("version", appVersionConfigService.getById(versionId));
        return "system/version/edit";
    }

    @PostMapping("/add")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult add(AppVersionConfig appVersionConfig) {
        appVersionConfig.setCreateTime(new Date());
        boolean save = appVersionConfigService.save(appVersionConfig);
        return toAjax(save);
    }

    @PostMapping("/edit")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult edit(AppVersionConfig appVersionConfig) {
        appVersionConfig.setUpdateTime(new Date());
        boolean b = appVersionConfigService.updateById(appVersionConfig);
        return toAjax(b);
    }

    @PostMapping("/remove")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult remove(String ids) {
        Long id = Long.valueOf(ids);
        appVersionConfigService.removeById(id);
        return AjaxResult.success();
    }
}
