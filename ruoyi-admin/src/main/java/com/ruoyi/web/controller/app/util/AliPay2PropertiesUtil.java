package com.ruoyi.web.controller.app.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.PropertiesUtil;

/**
 * <Description> <br>
 *
 * @author shi.yuwen<br>
 */
@Slf4j
public class AliPay2PropertiesUtil {
    private static PropertiesUtil propertiesUtil;

    static {
        propertiesUtil = new PropertiesUtil("alipay2.properties");
    }

    public static PropertiesUtil getConfig() {
        return propertiesUtil;
    }
}
