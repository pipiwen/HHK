package com.ruoyi.web.controller.app.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.ser.domain.*;
import com.ruoyi.ser.service.*;
import com.ruoyi.web.controller.app.pay.core.Alipay;
import com.ruoyi.web.controller.app.pay.domain.AlipayBean;
import com.ruoyi.web.controller.app.pay.service.PayService;
import com.ruoyi.web.controller.app.pay.util.AlipayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class PayServiceImpl implements PayService {

    @Autowired
    private Alipay alipay;

    @Autowired
    private IOrderRecordService orderRecordService;

    @Autowired
    private ISubsPlanService subsPlanService;

    @Autowired
    private IStoreInfoService storeInfoService;

    @Autowired
    private IGreetInstService greetInstService;

    @Override
    public String aliPay(AlipayBean alipayBean) throws AlipayApiException {
        return alipay.pay(alipayBean);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String aliNotify(Map<String, String> conversionParams) throws AlipayApiException {
        boolean signVerified = false;
        //调用SDK验证签名
        String alipayPublicKey = AlipayProperties.getPublicKey();
        String charset = "UTF-8";
        String signType = AlipayProperties.getSignType();

        signVerified = AlipaySignature.rsaCheckV1(conversionParams, alipayPublicKey, charset, signType);
        //对验签进行处理.
        if (signVerified) {
            log.info("+支付宝回调签名认证成功+");
            // 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure 支付宝官方建议校验的值（out_trade_no、total_amount、sellerId、app_id）
//                this.check(conversionParams);
            //验签通过 获取交易状态
            String tradeStatus = conversionParams.get("trade_status");

            //只处理支付成功的订单: 修改交易表状态,支付成功
            //只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
            if (UserConstants.PAY_TRADE_SUCCESS.equals(tradeStatus) || UserConstants.PAY_TRADE_FINISHED.equals(tradeStatus)) {
                //交易成功 归档订单表
                Date nowDate = DateUtils.getNowDate();
                String orderNo = conversionParams.get("out_trade_no");
                OrderRecord orderInfo = orderRecordService.getOne(new QueryWrapper<OrderRecord>()
                        .eq("order_num", orderNo));
                SubsPlan subsPlan = subsPlanService.getById(orderInfo.getProductId());
                Integer subsUnit = subsPlan.getSubsUnit();
                Integer during = subsPlan.getSubsDuring();
                int cycle = 0;
                if (null != subsUnit && null != during) {
                    cycle = this.getCycle(subsUnit, during);
                }

                orderRecordService.update(new UpdateWrapper<OrderRecord>()
                        .set("order_status", tradeStatus)
                        .set("update_time", nowDate)
                        .eq("order_num", orderNo)
                );
                if("好获客店铺开通".equals(conversionParams.get("subject"))) {
                    long userId = Long.parseLong(conversionParams.get("body"));
                    // 查看销售对应的店铺(一个销售只能对应3家店铺)
                    int storeCount = storeInfoService.count(new QueryWrapper<StoreInfo>().eq("user_id", userId));
                    if (storeCount >= 3) {
                        throw new RuntimeException("店铺数量异常");
                    }
                    StoreInfo storeInfo = new StoreInfo();
                        storeInfo.setUserId(userId);
                        storeInfo.setValidTime(LocalDate.now().plusDays(cycle));
                        storeInfo.setCreateTime(new Date());
                        storeInfo.setStatus(4);
                        storeInfoService.save(storeInfo);
                }
                else if ("好获客店铺续费".equals(conversionParams.get("subject"))){
                    StoreInfo storeInfo = storeInfoService.getById(orderInfo.getStoreId());
                    if (null != storeInfo.getValidTime() && LocalDate.now().isBefore(storeInfo.getValidTime())) {
                        // 门店信息更新
                        storeInfoService.update(new UpdateWrapper<StoreInfo>()
                                .set("valid_time", storeInfo.getValidTime().plusDays(cycle))
                                .set("update_time", nowDate)
                                .eq("id", orderInfo.getStoreId()));
                    }
                    else {
                        storeInfoService.update(new UpdateWrapper<StoreInfo>()
                                .set("valid_time", LocalDate.now().plusDays(cycle))
                                .set("update_time", nowDate)
                                .eq("id", orderInfo.getStoreId()));
                    }
                }
                else if ("好获客打招呼红包".equals(conversionParams.get("subject"))){
                    // 更新打招呼状态
                    greetInstService.update(new UpdateWrapper<GreetInst>()
                            .set("greet_time", nowDate)
                            .set("update_time", nowDate)
                            .set("greet_amount", new BigDecimal(conversionParams.get("total_amount")))
                            .set("greet_status", 2)
                            .set("pay_type", 1)
                            .eq("id", orderInfo.getGreetId()));
                }

            }
            return "success";
        } else {
            return "fail";
        }
    }

    private int getCycle(int unit, int during) {
        // 天
        if (unit == 1) {
            return unit * during;
        }
        if (unit == 2) {
            return 30 * during;
        }
        if (unit == 3) {
            return 365 * during;
        }
        else {
            return 0;
        }
    }
}
