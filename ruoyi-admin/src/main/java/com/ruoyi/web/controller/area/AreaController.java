package com.ruoyi.web.controller.area;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.CfgArea;
import com.ruoyi.system.service.ICfgAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author syw
 */
@RestController
@RequestMapping("/area")
public class AreaController extends BaseController {

    @Autowired
    private ICfgAreaService cfgAreaService;


    @GetMapping("/{code}")
    public AjaxResult getArea(@PathVariable Integer code) {
        List<CfgArea> areaList = cfgAreaService.list(new QueryWrapper<CfgArea>().eq("parent_id", code));
        return AjaxResult.success(areaList);
    }
}
