package com.ruoyi.web.controller.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.ser.domain.GreetInst;
import com.ruoyi.ser.service.IGreetInstService;
import com.ruoyi.system.domain.CfgArea;
import com.ruoyi.system.service.ICfgAreaService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author syw
 */
@RestController
@RequestMapping("/test1")
public class TestController1 extends BaseController {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ICfgAreaService cfgAreaService;

    @Autowired
    private IGreetInstService greetInstService;

    @GetMapping("/userList")
    public AjaxResult test1() {
        SysUser sysUser = sysUserService.selectUserById(1L);
        return AjaxResult.success(sysUser);

    }

    @GetMapping("/getArea")
    public AjaxResult test2() {
        List<CfgArea> areas = cfgAreaService.list(new QueryWrapper<CfgArea>().eq("parent_id", 3204));
        return AjaxResult.success(areas);
    }

    @PostMapping("/updateGreet")
    public AjaxResult testGreet() {
        GreetInst greetInst = new GreetInst();
//        greetInst.setGreetStatus(5);
        greetInst.setUpdateTime(new Date());
        greetInst.setId(1L);

        greetInstService.updateById(greetInst);
        logger.info(greetInst.toString());
        return AjaxResult.success();
    }


}
