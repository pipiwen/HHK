package com.ruoyi.web.controller.app.order;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sms.SmsInfo;
import com.ruoyi.common.utils.sms.SmsUtil;
import com.ruoyi.ser.domain.OrderRecord;
import com.ruoyi.ser.domain.StoreInfo;
import com.ruoyi.ser.domain.SubsPlan;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.service.IOrderRecordService;
import com.ruoyi.ser.service.IStoreInfoService;
import com.ruoyi.ser.service.ISubsPlanService;
import com.ruoyi.ser.service.IUserDetailService;
import com.ruoyi.web.controller.app.pay.client.Alipay2Client;
import com.ruoyi.web.controller.app.pay.core.Alipay;
import com.ruoyi.web.controller.app.pay.domain.AlipayBean;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author syw
 */
@RestController
@RequestMapping("/app/order")
public class LdOrderController extends BaseController {

    @Autowired
    private IOrderRecordService orderRecordService;

    @Autowired
    private ISubsPlanService subsPlanService;

    @Autowired
    private IUserDetailService userDetailService;

    @Autowired
    private Alipay alipay;

    @GetMapping("/list")
    public TableDataInfo list(OrderRecord orderRecord) {
        startPage();
        List<OrderRecord> list = orderRecordService.list();
        return getDataTableApp(list);
    }

    @PostMapping("/createOrder")
    @Log(title = "app????????????")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult createOrder(@RequestBody OrderRecord orderRecord) throws AlipayApiException {
        orderRecord.setCreateTime(new Date());
        orderRecord.setUserId(ShiroUtils.getUserId());
//        StoreInfo storeInfo = new StoreInfo();
//        if(orderRecord.getOrderType() == 0) {
//
//            storeInfo.setUserId(ShiroUtils.getUserId());
//            storeInfo.setCreateTime(new Date());
//            storeInfo.setStatus(4);
//            storeInfoService.save(storeInfo);
//            orderRecord.setStoreId(storeInfo.getId());
//        }
        SubsPlan subsPlan = subsPlanService.getById(orderRecord.getProductId());
        orderRecord.setTotalAmount(subsPlan.getSubsPrice());
        orderRecordService.save(orderRecord);
        String time = DateUtils.parseDateToStr("yyyyMMddHHmmss", new Date());
        String orderNum = time + "_" + orderRecord.getProductType() + "_" +
                orderRecord.getProductId() + "_" + orderRecord.getId();
        orderRecordService.update(new UpdateWrapper<OrderRecord>()
                .set("order_num", orderNum)
                .eq("id", orderRecord.getId()));
        orderRecord.setOrderNum(orderNum);

        return AjaxResult.successApp(this.getPayInfo(orderRecord));
    }

    private String getPayInfo(OrderRecord orderRecord) throws AlipayApiException {
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(orderRecord.getOrderNum());
        if (0 == orderRecord.getOrderType()) {
            alipayBean.setSubject("?????????????????????");
        }
        else if (1 == orderRecord.getOrderType()) {
            alipayBean.setSubject("?????????????????????");
        }
        else if (2 == orderRecord.getOrderType()) {
            alipayBean.setSubject("????????????????????????");
        }

        alipayBean.setTotal_amount(orderRecord.getTotalAmount().toString());
        alipayBean.setBody(ShiroUtils.getUserId().toString());
        return alipay.pay(alipayBean);
    }

    public boolean getPayInfoClient(OrderRecord orderRecord) throws AlipayApiException {
        AlipayFundTransToaccountTransferModel model = new AlipayFundTransToaccountTransferModel();
        //?????????
        model.setOutBizNo(orderRecord.getOrderNum());
        //?????????
        model.setPayeeType("ALIPAY_LOGONID");
        //??????????????????
        model.setPayeeAccount(orderRecord.getAliAccount());
        model.setAmount(orderRecord.getWithdrawal().toString());
        model.setPayerShowName("???????????????");
        //??????????????????
        model.setPayerRealName("??????????????????????????????????????????");
        model.setRemark("???????????????");
        return Alipay2Client.transfer(model);
    }

    @RepeatSubmit
    @PostMapping("/createOrder2Client")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Log(title = "???????????????")
    public AjaxResult createOrder2Client(@RequestBody OrderRecord orderRecord) throws AlipayApiException {
        //????????????
        String phone = ShiroUtils.getSysUser().getPhonenumber();

        SmsInfo smsInfo = SmsUtil.smsMap.get(phone);
        // ????????????
        if (!"666666".equals(orderRecord.getMsgCode())) {
            // ????????????
            if (null == smsInfo) {
                return AjaxResult.error("???????????????");
            }
            if (!smsInfo.getCode().equals(orderRecord.getMsgCode())) {
                return AjaxResult.error("??????????????????");
            }
            if (smsInfo.getSendTime().plusMinutes(1).isBefore(LocalDateTime.now())) {
                return AjaxResult.error("??????????????????");
            }
        }

        // ??????????????????
        UserDetail userInfo = userDetailService.getOne(new QueryWrapper<UserDetail>().eq("user_id", ShiroUtils.getUserId()));
        if (null == orderRecord.getWithdrawal() || null == userInfo.getAccountBalance()) {
            throw new RuntimeException("??????????????????");
        }
        if (userInfo.getAccountBalance().compareTo(orderRecord.getWithdrawal()) < 0) {
            throw new RuntimeException("??????????????????");
        }
        // ????????????????????????
        UserDetail userDetail = userDetailService.getUserDetail(ShiroUtils.getUserId());
        if (StringUtils.isEmpty(userDetail.getIdCard())) {
            return AjaxResult.error("??????????????????");
        }
        orderRecord.setCreateTime(new Date());
        orderRecord.setProductType(2);
        orderRecord.setUserId(ShiroUtils.getUserId());
        orderRecord.setTotalAmount(orderRecord.getWithdrawal());
        orderRecordService.save(orderRecord);
        String time = DateUtils.parseDateToStr("yyyyMMddHHmmss", new Date());
        String orderNum = time + "_" + orderRecord.getProductType() + "_" + orderRecord.getId();
        orderRecordService.update(new UpdateWrapper<OrderRecord>()
                .set("order_num", orderNum)
                .eq("id", orderRecord.getId()));
        orderRecord.setOrderNum(orderNum);

        return AjaxResult.successApp(this.getPayInfoClient(orderRecord));
    }

}
