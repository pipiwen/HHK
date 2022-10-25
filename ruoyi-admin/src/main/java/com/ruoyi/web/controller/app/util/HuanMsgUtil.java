package com.ruoyi.web.controller.app.util;

import com.ruoyi.ser.domain.HuanMsgRequest;
import com.ruoyi.ser.domain.HuanMsgResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author syw
 */
@Component
public class HuanMsgUtil {

    private static Map<String, String> tokenMap = new HashMap();

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    public String getToken() {
        if (null != tokenMap.get("token")) {
            return tokenMap.get("token");
        }
        else {
            synchronized (HuanMsgUtil.class) {
                String uri = HuanMsgPropertiesUtil.getConfig().getStringProperty("uri");
                String orgname = HuanMsgPropertiesUtil.getConfig().getStringProperty("orgname");
                String appname = HuanMsgPropertiesUtil.getConfig().getStringProperty("appname");
                String clientId = HuanMsgPropertiesUtil.getConfig().getStringProperty("clientId");
                String clientSecret = HuanMsgPropertiesUtil.getConfig().getStringProperty("clientSecret");
                String ttl = HuanMsgPropertiesUtil.getConfig().getStringProperty("ttl");
                String url = "http://" + uri + "/" + orgname + "/" + appname + "/" + "token";

                RestTemplate restTemplate = restTemplateBuilder.build();
                HuanMsgRequest request = new HuanMsgRequest();
                // 请求token固定值
                request.setGrant_type("client_credentials");
                request.setClient_id(clientId);
                request.setClient_secret(clientSecret);
                request.setTtl(ttl);
                HuanMsgResponse huanMsgResponse = restTemplate.postForObject(url, request, HuanMsgResponse.class);
                tokenMap.put("token", huanMsgResponse.getAccess_token());

                return huanMsgResponse.getAccess_token();
            }
        }
    }

}
