package com.ruoyi.web.controller.app.pay.client;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransOrderQueryModel;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.request.AlipayFundTransOrderQueryRequest;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.ser.domain.OrderRecord;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.service.IOrderRecordService;
import com.ruoyi.ser.service.IUserDetailService;
import com.ruoyi.ser.service.impl.OrderRecordServiceImpl;
import com.ruoyi.ser.service.impl.UserDetailServiceImpl;
import com.ruoyi.web.controller.app.pay.util.AlipayProperties;
import com.ruoyi.web.controller.app.util.AliPay2PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author syw
 */
@Slf4j
@Component
public class Alipay2Client {

    private static IOrderRecordService orderRecordService = SpringUtils.getBean(OrderRecordServiceImpl.class);

    private static IUserDetailService userDetailService = SpringUtils.getBean(UserDetailServiceImpl.class);

    private static AlipayClient alipayClient;
    static  {
        String serverUrl = AliPay2PropertiesUtil.getConfig().getStringProperty("gatewayUrl");
        String appId = AliPay2PropertiesUtil.getConfig().getStringProperty("appId");
        String privateKey = AliPay2PropertiesUtil.getConfig().getStringProperty("privateKey");
        String format = "json";
        String charset = AliPay2PropertiesUtil.getConfig().getStringProperty("charset");
        String alipayPublicKey = AliPay2PropertiesUtil.getConfig().getStringProperty("publicKey");
        String signType = AliPay2PropertiesUtil.getConfig().getStringProperty("signType");
        alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
    }

    public static boolean transfer(AlipayFundTransToaccountTransferModel model) throws AlipayApiException {
        AlipayFundTransToaccountTransferResponse response = transferToResponse(model);
        String result = response.getBody();
        if (response.isSuccess()) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String orderNum = jsonObject.getJSONObject("alipay_fund_trans_toaccount_transfer_response").getString("out_biz_no");
            syncUserAcct(orderNum);
            return true;
        } else {
            //调用查询接口查询数据
            JSONObject jsonObject = JSONObject.parseObject(result);
            String out_biz_no = jsonObject.getJSONObject("alipay_fund_trans_toaccount_transfer_response").getString("out_biz_no");
            AlipayFundTransOrderQueryModel queryModel = new AlipayFundTransOrderQueryModel();
            model.setOutBizNo(out_biz_no);
            boolean isSuccess = transferQuery(queryModel);
            if (isSuccess) {
                syncUserAcct(out_biz_no);
                return true;
            }
        }
        return false;
    }

    public static AlipayFundTransToaccountTransferResponse transferToResponse(AlipayFundTransToaccountTransferModel model) throws AlipayApiException{
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        request.setBizModel(model);
        return alipayClient.execute(request);
    }

    public static boolean transferQuery(AlipayFundTransOrderQueryModel model) throws AlipayApiException{
        AlipayFundTransOrderQueryResponse response = transferQueryToResponse(model);
        log.info("transferQuery result>"+response.getBody());
        if(response.isSuccess()){
            return true;
        }
        return false;
    }
    public static AlipayFundTransOrderQueryResponse transferQueryToResponse(AlipayFundTransOrderQueryModel model) throws AlipayApiException{
        AlipayFundTransOrderQueryRequest request = new AlipayFundTransOrderQueryRequest();
        request.setBizModel(model);
        return alipayClient.execute(request);
    }

    private static void syncUserAcct(String orderNum) {
        OrderRecord orderRecord = orderRecordService.getOne(new QueryWrapper<OrderRecord>()
                .eq("order_num", orderNum)
                .eq("product_type", 2)
        );
        BigDecimal totalAmount = orderRecord.getTotalAmount();
        UserDetail userDetail = userDetailService.getOne(new QueryWrapper<UserDetail>()
                .eq("user_id", ShiroUtils.getUserId())
        );
        UserDetail userDetail1 = new UserDetail();
        userDetail1.setId(userDetail.getId());
        userDetail1.setAccountBalance(userDetail.getAccountBalance().subtract(totalAmount));
        userDetail1.setUpdateTime(new Date());
        userDetailService.updateById(userDetail1);

    }
}
