package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author syw
 */
@Controller
@RequestMapping("/system/statute")
public class StatuteController extends BaseController {

    @RequestMapping("/show")
    public String statute() {
        return "statute";
    }
}
