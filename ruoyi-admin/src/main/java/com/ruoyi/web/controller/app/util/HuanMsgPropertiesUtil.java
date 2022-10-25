package com.ruoyi.web.controller.app.util;

import org.apache.logging.log4j.util.PropertiesUtil;

/**
 * @author syw
 */
public class HuanMsgPropertiesUtil {

    private static PropertiesUtil propertiesUtil;

    static {
        propertiesUtil = new PropertiesUtil("huanMsg.properties");
    }

    public static PropertiesUtil getConfig() {
        return propertiesUtil;
    }
}
