package com.ruoyi.web.controller.app.pay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.ser.domain.OrderRecord;
import com.ruoyi.ser.service.IOrderRecordService;
import com.ruoyi.web.controller.app.pay.core.Alipay;
import com.ruoyi.web.controller.app.pay.domain.AlipayBean;
import com.ruoyi.web.controller.app.pay.service.PayService;
import com.ruoyi.web.controller.app.pay.util.AlipayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author syw
 */
@RestController
@RequestMapping("/app/pay")
public class PayController extends BaseController {

    @Autowired
    private PayService payService;




    /**
     * 阿里支付
     * @param outTradeNo
     * @param subject
     * @param totalAmount
     * @param body
     * @return
     * @throws AlipayApiException
     */
    @PostMapping(value = "/alipay")
    public AjaxResult alipay(String outTradeNo, String subject, String totalAmount, String body) throws AlipayApiException {
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(outTradeNo);
        alipayBean.setSubject(subject);
        alipayBean.setTotal_amount(totalAmount);
        alipayBean.setBody(body);
        return AjaxResult.successApp(payService.aliPay(alipayBean));
    }

//    @PostMapping(value = "/pay2Client")
//    public AjaxResult pay2Client(OrderRecord orderRecord) throws AlipayApiException {
//        AlipayBean alipayBean = new AlipayBean();
//        alipayBean.setOut_trade_no(outTradeNo);
//        alipayBean.setSubject(subject);
//        alipayBean.setTotal_amount(totalAmount);
//        alipayBean.setBody(body);
//        return AjaxResult.successApp(payService.aliPay(alipayBean));
//    }
    /**
     * 支付宝支付成功后.异步请求该接口
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping(value="/payNotify")
    @Log(title = "支付宝回调接口")
    public String aliNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("=支付宝异步返回支付结果开始");
        //1.从支付宝回调的request域中取值
        //获取支付宝返回的参数集合
        Map<String, String[]> aliParams = request.getParameterMap();
        //用以存放转化后的参数集合
        Map<String, String> conversionParams = new HashMap<>();
        for (Iterator<String> iter = aliParams.keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            String[] values = aliParams.get(key);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
            conversionParams.put(key, valueStr);
        }
        logger.info("==返回参数集合：{}", conversionParams);
        String status= payService.aliNotify(conversionParams);
        return status;
    }

    @GetMapping(value="/payReturn")
    public AjaxResult aliNotify() throws Exception {
        logger.info("--------支付同步返回--------");
       return AjaxResult.successApp("支付完成");
    }

}
