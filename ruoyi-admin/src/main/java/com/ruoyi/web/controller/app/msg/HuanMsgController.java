package com.ruoyi.web.controller.app.msg;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.controller.app.util.HuanMsgUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author syw
 */
@RestController
@RequestMapping("/app/msg")
public class HuanMsgController extends BaseController {

    @Autowired
    private HuanMsgUtil huanMsgUtil;

    @GetMapping("/getToken")
    public AjaxResult getToken() {
        return AjaxResult.success(huanMsgUtil.getToken());
    }
}
