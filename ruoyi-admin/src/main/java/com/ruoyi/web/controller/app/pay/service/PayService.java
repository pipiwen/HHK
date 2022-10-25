package com.ruoyi.web.controller.app.pay.service;

import com.alipay.api.AlipayApiException;
import com.ruoyi.web.controller.app.pay.domain.AlipayBean;

import java.util.Map;

/**
 * @author syw
 */
public interface PayService {

    /**
     * 支付宝支付接口
     * @param alipayBean
     * @return
     * @throws AlipayApiException
     */
    String aliPay(AlipayBean alipayBean) throws AlipayApiException;

    /**
     * 支付回调 <br>
     *
     * @param conversionParams <br>
     * @return java.lang.String <br>
     * @throws AlipayApiException
     **/
    String aliNotify(Map<String, String> conversionParams) throws AlipayApiException;

}
