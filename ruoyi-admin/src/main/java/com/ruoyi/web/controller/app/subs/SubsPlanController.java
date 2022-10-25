package com.ruoyi.web.controller.app.subs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.ser.domain.SubsPlan;
import com.ruoyi.ser.service.ISubsPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author syw
 */
@RestController
@RequestMapping("/app/subsPlan")
public class SubsPlanController extends BaseController {

    @Autowired
    private ISubsPlanService subsPlanService;

    @GetMapping("/list")
    public AjaxResult list() {
        List<SubsPlan> list = subsPlanService.list(new QueryWrapper<SubsPlan>()
                .eq("status", "S"));
        return AjaxResult.successApp(list);
    }

    @PostMapping("/add")
    public AjaxResult add(@RequestBody SubsPlan subsPlan) {
        subsPlan.setCreateTime(new Date());
        subsPlanService.save(subsPlan);
        return AjaxResult.successApp();
    }

    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SubsPlan subsPlan) {
        subsPlan.setUpdateTime(new Date());
        subsPlanService.updateById(subsPlan);
        return AjaxResult.successApp();
    }

    @PostMapping("/remove/{id}")
    public AjaxResult edit(@PathVariable("id") Integer id) {
        subsPlanService.update(new UpdateWrapper<SubsPlan>()
                .set("status", "X")
                .eq("id", id)
        );
        return AjaxResult.successApp();
    }

}
