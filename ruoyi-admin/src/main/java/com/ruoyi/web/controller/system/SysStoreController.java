package com.ruoyi.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.shiro.util.AuthorizationUtils;
import com.ruoyi.ser.domain.StoreInfo;
import com.ruoyi.ser.service.IStoreInfoService;
import com.ruoyi.system.domain.CfgArea;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.service.ICfgAreaService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 角色信息
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/store")
public class SysStoreController extends BaseController
{
    private String prefix = "system/store";

    @Autowired
    private IStoreInfoService storeInfoService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ICfgAreaService cfgAreaService;

    @RequiresPermissions("system:store:view")
    @GetMapping()
    public String store()
    {
        return prefix + "/store";
    }

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(StoreInfo storeInfo)
    {
        startPage();
        List<StoreInfo> list = storeInfoService.listByCondition(storeInfo);
        StringBuilder addressName = new StringBuilder();
        list.forEach(e-> {
//            if (null != e.getCity()) {
//                CfgArea city = cfgAreaService.getById(e.getCity());
//                addressName.append(city.getName()).append(" ");
//            }
//            if (null != e.getArea()) {
//                CfgArea area = cfgAreaService.getById(e.getCity());
//                addressName.append(area.getName()).append(" ");
//            }
            if (null != e.getAddress()) {
                e.setAddressName(e.getAddress());
            }

        });
        return getDataTable(list);
    }


    @GetMapping("/picture/{id}")
    public String detail(@PathVariable("id") Long id, ModelMap mmap)
    {
        StoreInfo storeInfo = storeInfoService.getById(id);
//        String license = storeInfo.getLicense();
//        ArrayList<StoreInfo> storeInfoList = new ArrayList<>();
//        if(StringUtils.isNotEmpty(license)) {
//            String[] lics = license.split(",");
//            for (String s : lics) {
//                StoreInfo storeInfo1 = new StoreInfo();
//                storeInfo1.setId(storeInfo.getId());
//                storeInfo1.setStoreName(storeInfo.getStoreName());
//                storeInfo1.setLicense(s);
//                storeInfo1.setCreateTime(storeInfo.getCreateTime());
//                storeInfoList.add(storeInfo1);
//            }
//
//        }
        mmap.put("storeInfo", storeInfo);
        return "system/store/picture";
    }

    @GetMapping("/reason/{id}")
    public String reason(@PathVariable("id") Long id, ModelMap mmap)
    {
        StoreInfo storeInfo = storeInfoService.getById(id);
        mmap.put("storeInfo", storeInfo);
        return "system/store/refuse";
    }

    @PostMapping("/picture/list/{id}")
    @ResponseBody
    public TableDataInfo detail(@PathVariable("id") Long id)
    {
        startPage();
        StoreInfo storeInfo = storeInfoService.getById(id);
        String license = storeInfo.getLicense();
        ArrayList<StoreInfo> storeInfoList = new ArrayList<>();
        if(StringUtils.isNotEmpty(license)) {
            String[] lics = license.split(",");
            for (String s : lics) {
                StoreInfo storeInfo1 = new StoreInfo();
                storeInfo1.setId(storeInfo.getId());
                storeInfo1.setStoreName(storeInfo.getStoreName());
                storeInfo1.setLicense(s);
                storeInfo1.setCreateTime(storeInfo.getCreateTime());
                storeInfoList.add(storeInfo1);
            }

        }
        return getDataTable(storeInfoList);
    }

    @PostMapping("/audit")
    @ResponseBody
    public AjaxResult audit(StoreInfo storeInfo)
    {
        storeInfo.setUpdateTime(new Date());
        storeInfoService.updateById(storeInfo);
        return AjaxResult.success();
    }


}