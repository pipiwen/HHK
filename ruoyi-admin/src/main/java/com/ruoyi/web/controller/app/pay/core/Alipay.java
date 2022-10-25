package com.ruoyi.web.controller.app.pay.core;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.ser.domain.GreetInst;
import com.ruoyi.ser.domain.OrderRecord;
import com.ruoyi.ser.service.IGreetInstService;
import com.ruoyi.ser.service.IOrderRecordService;
import com.ruoyi.web.controller.app.pay.domain.AlipayBean;
import com.ruoyi.web.controller.app.pay.util.AlipayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author syw
 */
@Component
@Slf4j
public class Alipay {

    @Autowired
    private IGreetInstService greetInstService;

    @Autowired
    private IOrderRecordService orderRecordService;

    /**
     * 支付接口
     * @param alipayBean
     * @return
     * @throws AlipayApiException
     */
    public String pay(AlipayBean alipayBean) throws AlipayApiException {
        // 1、获得初始化的AlipayClient
        String serverUrl = AlipayProperties.getGatewayUrl();
        String appId = AlipayProperties.getAppId();
        String privateKey = AlipayProperties.getPrivateKey();
        String format = "json";
        String charset = AlipayProperties.getCharset();
        String alipayPublicKey = AlipayProperties.getPublicKey();
        String signType = AlipayProperties.getSignType();
        String returnUrl = AlipayProperties.getReturnUrl();
        String notifyUrl = AlipayProperties.getNotifyUrl();
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
        // 2、设置请求参数
        AlipayTradeAppPayRequest ali_request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

        model.setBody(alipayBean.getBody());
        //商品名称
        model.setSubject(alipayBean.getSubject());
        //商户订单号(根据业务需求自己生成)
        model.setOutTradeNo(alipayBean.getOut_trade_no());
        //交易超时时间 这里的30m就是30分钟
        model.setTimeoutExpress("30m");
        //支付金额 后面保留2位小数点..不能超过2位
        model.setTotalAmount(alipayBean.getTotal_amount());
        //销售产品码（固定值） //这个不做多解释..看文档api接口参数解释
        model.setProductCode("QUICK_MSECURITY_PAY");
        ali_request.setBizModel(model);
        //异步回调地址（后台）//这里我在上面的aliPayConfig有讲..自己去看
        ali_request.setNotifyUrl(notifyUrl);

        //同步回调地址（APP）同上
        ali_request.setReturnUrl(returnUrl);

        String result = alipayClient.sdkExecute(ali_request).getBody();
        // 返回付款信息
        return result;
    }
    /** 退款接口 */
    public String refund(AlipayBean alipayBean) throws AlipayApiException{
        // 1、获得初始化的AlipayClient
        String serverUrl = AlipayProperties.getGatewayUrl();
        String appId = AlipayProperties.getAppId();
        String privateKey = AlipayProperties.getPrivateKey();
        String format = "json";
        String charset = AlipayProperties.getCharset();
        String alipayPublicKey = AlipayProperties.getPublicKey();
        String signType = AlipayProperties.getSignType();
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
//        bizContent.put("trade_no", "2021081722001419121412730660");
        bizContent.put("refund_amount", alipayBean.getTotal_amount());
        bizContent.put("out_request_no", alipayBean.getOut_trade_no());

        request.setBizContent(bizContent.toString());
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        String result = "";
        if ("10000".equals(response.getCode())) {
            //匹配订单
            OrderRecord orderInfo = orderRecordService.getOne(new QueryWrapper<OrderRecord>()
                    .eq("order_num", alipayBean.getOut_trade_no()));
            GreetInst greetInst = new GreetInst();
            greetInst.setGreetStatus(5);
            greetInst.setUpdateTime(new Date());
            greetInst.setId(orderInfo.getGreetId());

            greetInstService.updateById(greetInst);
            result = "退款成功";
        }else {
            result = response.getSubMsg();
        }
        return result;

    }



    public String payClient(AlipayBean alipayBean) throws AlipayApiException {
        // 1、获得初始化的AlipayClient
        String serverUrl = AlipayProperties.getGatewayUrl();
        String appId = AlipayProperties.getAppId();
        String privateKey = AlipayProperties.getPrivateKey();
        String format = "json";
        String charset = AlipayProperties.getCharset();
        String alipayPublicKey = AlipayProperties.getPublicKey();
        String signType = AlipayProperties.getSignType();
        String returnUrl = AlipayProperties.getReturnUrl();
        String notifyUrl = AlipayProperties.getNotifyUrl();
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
        // 2、设置请求参数
        AlipayTradeAppPayRequest ali_request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

        model.setBody("充值服务");
        //商品名称
        model.setSubject("充值服务");
        //商户订单号(根据业务需求自己生成)
        model.setOutTradeNo(alipayBean.getOut_trade_no());
        //交易超时时间 这里的30m就是30分钟
        model.setTimeoutExpress("30m");
        //支付金额 后面保留2位小数点..不能超过2位
        model.setTotalAmount(alipayBean.getTotal_amount());
        //销售产品码（固定值） //这个不做多解释..看文档api接口参数解释
        model.setProductCode("QUICK_MSECURITY_PAY");
        ali_request.setBizModel(model);
        //异步回调地址（后台）//这里我在上面的aliPayConfig有讲..自己去看
        ali_request.setNotifyUrl(notifyUrl);

        //同步回调地址（APP）同上
        ali_request.setReturnUrl(returnUrl);

        String result = alipayClient.sdkExecute(ali_request).getBody();
        // 返回付款信息
        return result;
    }
}
