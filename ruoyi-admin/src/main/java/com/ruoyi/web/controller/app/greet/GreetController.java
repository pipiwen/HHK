package com.ruoyi.web.controller.app.greet;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.ser.domain.GreetInst;
import com.ruoyi.ser.domain.OrderRecord;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.service.IGreetInstService;
import com.ruoyi.ser.service.IOrderRecordService;
import com.ruoyi.ser.service.IUserDetailService;
import com.ruoyi.web.controller.app.order.LdOrderController;
import com.ruoyi.web.controller.app.pay.core.Alipay;
import com.ruoyi.web.controller.app.pay.domain.AlipayBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author syw
 */
@RestController
@RequestMapping("/app/greet")
public class GreetController extends BaseController {

    @Autowired
    private IGreetInstService greetInstService;

    @Autowired
    private LdOrderController orderController;

    @Autowired
    private IOrderRecordService orderRecordService;

    @Autowired
    private IUserDetailService userDetailService;

    @Autowired
    private Alipay alipay;

    @GetMapping("/list")
    public TableDataInfo getInfo(UserDetail userDetail) {
        startPage();
        userDetail.setUserId(ShiroUtils.getUserId());
        String userType = ShiroUtils.getSysUser().getUserType();
        //setUserType 在sql中当GreetType使用
        if (null != userDetail.getGreetType()) {
            userDetail.setUserType(String.valueOf(userDetail.getGreetType()));
        }

        if (UserConstants.CUSTOMER_USER_TYPE.equals(userType)) {
            userDetail.setGreetType(2);

        }
        if (UserConstants.SALE_USER_TYPE.equals(userType)) {
            userDetail.setGreetType(1);
        }
        List<UserDetail> userDetailList = greetInstService.listByCondition(userDetail);
        userDetailList.forEach(e -> {
            //匹配支付成功的最后一个订单
            OrderRecord orderInfo = orderRecordService.getOne(new QueryWrapper<OrderRecord>()
                    .eq("greet_id", e.getGreetId())
                    .and(queryWrapper-> queryWrapper.eq("order_status", UserConstants.PAY_TRADE_SUCCESS)
                            .or()
                            .eq("order_status", UserConstants.PAY_TRADE_FINISHED)
                    )
                    .orderByDesc("create_time")
                    .last("limit 1")
            );
            if (null != orderInfo) {
                e.setHasRedPacket(true);
            }
            else {
                e.setHasRedPacket(false);
            }
            // 判断是否是余额支付
            if (2 == e.getPayType()) {
                e.setHasRedPacket(true);
            }
        });
        return getDataTableApp(userDetailList);
    }

    @GetMapping("/getStatus")
    public AjaxResult getStatus(GreetInst greet) {
        GreetInst one = greetInstService.getOne(new QueryWrapper<GreetInst>()
                .eq("launch_id", greet.getLaunchId())
                .eq("target_id", greet.getTargetId())
        );
        return AjaxResult.successApp(one);
    }

    @GetMapping("/listInvitation")
    public TableDataInfo listInvitation(UserDetail userDetail) {
        startPage();
        userDetail.setUserId(ShiroUtils.getUserId());
        List<UserDetail> userDetailList = null;
        if (UserConstants.SALE_USER_TYPE.equals(ShiroUtils.getSysUser().getUserType())) {
            userDetailList = greetInstService.customerInvitationList(userDetail);
        }
        else if (UserConstants.CUSTOMER_USER_TYPE.equals(ShiroUtils.getSysUser().getUserType())) {
            userDetailList = greetInstService.saleInvitationList(userDetail);
        }

        return getDataTableApp(userDetailList);
    }

    @PostMapping("/greet")
    public AjaxResult greet(@RequestBody GreetInst greet) {
        String userType = ShiroUtils.getSysUser().getUserType();
        Long userId = ShiroUtils.getUserId();
        if (null == greet.getTargetId()) {
            throw new RuntimeException("接口参数异常");
        }
        // 查看聊天状态
        GreetInst one = greetInstService.getOne(new QueryWrapper<GreetInst>()
                .eq("launch_id", userId)
                .eq("target_id", greet.getTargetId())
        );
        if (null != one) {
            return AjaxResult.success(one);
        }
        // 顾客端
        if (UserConstants.CUSTOMER_USER_TYPE.equals(userType)) {
            greet.setLaunchId(userId);
            greet.setGreetStatus(1);
            greet.setGreetType(2);
            greet.setCreateTime(new Date());
            greetInstService.save(greet);
        }

        // 销售端
        if (UserConstants.SALE_USER_TYPE.equals(userType)) {
            greet.setLaunchId(userId);
            greet.setGreetStatus(1);
            greet.setGreetType(1);
            greet.setCreateTime(new Date());
            greetInstService.save(greet);
        }
        return AjaxResult.successApp(greet);
    }
    /** 同意聊天 */
    @PostMapping("/agree")
    @Log(title = "chat_agree")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult agree(@RequestBody OrderRecord orderRecord) throws AlipayApiException {

        GreetInst greet1 = greetInstService.getById(orderRecord.getGreetId());
        // 是否仅聊天
        if (greet1.getGreetStatus() == 6) {
            GreetInst greetInst = new GreetInst();

            greetInst.setUpdateTime(new Date());
            greetInst.setGreetStatus(3);
            greetInst.setId(orderRecord.getGreetId());
            greetInst.setPayType(1);
            greetInstService.updateById(greetInst);

            return AjaxResult.successApp();
        }

        if (greet1.getGreetStatus() != 2) {
            throw new RuntimeException("支付状态异常");
        }

//        orderRecord.setCreateTime(new Date());
//        orderRecord.setProductType(2);
//        orderRecord.setUserId(ShiroUtils.getUserId());
//        orderRecord.setTotalAmount(new BigDecimal("0.1"));
//        orderRecordService.save(orderRecord);
//        String time = DateUtils.parseDateToStr("yyyyMMddHHmmss", new Date());
//        String orderNum = time + "_" + orderRecord.getProductType() + "_" + orderRecord.getId();
//        orderRecordService.update(new UpdateWrapper<OrderRecord>()
//                .set("order_num", orderNum)
//                .eq("id", orderRecord.getId()));
//        orderRecord.setOrderNum(orderNum);
//
//        orderController.getPayInfoClient(orderRecord);

        //匹配最后一个订单
        OrderRecord orderInfo = orderRecordService.getOne(new QueryWrapper<OrderRecord>()
                .eq("greet_id", orderRecord.getGreetId())
//                .and(e-> e.eq("order_status", UserConstants.PAY_TRADE_SUCCESS)
//                        .or()
//                        .eq("order_status", UserConstants.PAY_TRADE_FINISHED)
//                )
                .orderByDesc("create_time")
                .last("limit 1")
        );
        if (null == orderInfo) {
            return AjaxResult.error("未匹配到订单");
        }
        if (!(UserConstants.PAY_TRADE_SUCCESS.equals(orderInfo.getOrderStatus()) ||
                UserConstants.PAY_TRADE_FINISHED.equals(orderInfo.getOrderStatus()))) {
            return AjaxResult.error("订单状态异常");
        }



        //匹配订单
        GreetInst greetInst = new GreetInst();

        greetInst.setUpdateTime(new Date());
        greetInst.setGreetStatus(3);
//        if (UserConstants.CUSTOMER_USER_TYPE.equals(userType)) {
//            greetInst.setGreetStatus(3);
//        }
//        else if (UserConstants.SALE_USER_TYPE.equals(userType)) {
//            greetInst.setGreetStatus(6);
//        }
        greetInst.setId(orderRecord.getGreetId());
        greetInst.setPayType(1);
        greetInstService.updateById(greetInst);

        // 更新账户的余额
        GreetInst greetById = greetInstService.getById(orderInfo.getGreetId());

        UserDetail userDetail = new UserDetail();
        userDetail.setAccountBalance(orderInfo.getTotalAmount().multiply(BigDecimal.valueOf(0.6)));
        userDetail.setUserId(greetById.getTargetId());
        userDetail.setRemark("打招呼-同意聊天");
        userDetailService.updateAccountBalance(userDetail);

        return AjaxResult.successApp();
    }

    @PostMapping("/refuse")
    @Log(title = "chat_refuse")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult refuse(@RequestBody GreetInst greet) throws AlipayApiException {
//        Long userId = ShiroUtils.getUserId();
        // 检验接口是否已完成支付
        GreetInst greet1 = greetInstService.getById(greet.getId());

        // 是否仅聊天
        if (greet1.getGreetStatus() == 6) {

            GreetInst greetInst = new GreetInst();
            greetInst.setGreetStatus(5);
            greetInst.setUpdateTime(new Date());
            greetInst.setId(greet.getId());

            greetInstService.updateById(greetInst);


            return AjaxResult.successApp();
        }
        if (greet1.getGreetStatus() != 2) {
            throw new RuntimeException("支付状态异常");
        }
        if (null == greet.getId()) {
            throw new RuntimeException("接口参数异常");
        }
        //匹配支付成功的最后一个订单
        OrderRecord orderInfo = orderRecordService.getOne(new QueryWrapper<OrderRecord>()
                .eq("greet_id", greet.getId())
//                .and(e-> e.eq("order_status", UserConstants.PAY_TRADE_SUCCESS)
//                        .or()
//                        .eq("order_status", UserConstants.PAY_TRADE_FINISHED)
//                )
                .orderByDesc("create_time")
                .last("limit 1")
        );
        if (null == orderInfo) {
            return AjaxResult.error("退款异常，未匹配到订单");
        }
        if (!(UserConstants.PAY_TRADE_SUCCESS.equals(orderInfo.getOrderStatus()) ||
                UserConstants.PAY_TRADE_FINISHED.equals(orderInfo.getOrderStatus()))) {
            return AjaxResult.error("订单状态异常");
        }

        // 退款接口
//        AlipayBean alipayBean = new AlipayBean();
//        alipayBean.setOut_trade_no(orderInfo.getOrderNum());
//        alipayBean.setTotal_amount(orderInfo.getTotalAmount().toString());
//        alipay.refund(alipayBean);

        GreetInst greetInst = new GreetInst();
        greetInst.setGreetStatus(5);
        greetInst.setUpdateTime(new Date());
        greetInst.setId(orderInfo.getGreetId());

        greetInstService.updateById(greetInst);

        GreetInst greetById = greetInstService.getById(orderInfo.getGreetId());

        UserDetail userDetail = new UserDetail();
        userDetail.setAccountBalance(orderInfo.getTotalAmount());
        userDetail.setUserId(greetById.getLaunchId());
        userDetail.setRemark("打招呼-退款");
        userDetailService.updateAccountBalance(userDetail);

        return AjaxResult.successApp();
    }

    /** 余额支付 */
    @PostMapping("/balancePay")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult balancePay(@RequestBody GreetInst greet) throws AlipayApiException {
        Long userId = ShiroUtils.getUserId();
        String userType = ShiroUtils.getSysUser().getUserType();
        UserDetail userDetail = userDetailService.getOne(new QueryWrapper<UserDetail>().eq("user_id", userId));
        BigDecimal accountBalance = userDetail.getAccountBalance();
        if (null == userDetail.getAccountBalance()) {
            throw new RuntimeException("账户余额异常!");
        }
        if (accountBalance.compareTo(BigDecimal.valueOf(5)) < 0) {
            return AjaxResult.error("账户余额不足");
        }

        UserDetail userDetail1 = new UserDetail();

        userDetail1.setAccountBalance(BigDecimal.valueOf(-5));
        userDetail1.setUserId(userId);
        userDetail1.setRemark("打招呼-余额支付");
        userDetailService.updateAccountBalance(userDetail1);

//        UserDetail userDetail1 = new UserDetail();
//        userDetail1.setId(userDetail.getId());
//        userDetail1.setAccountBalance(accountBalance.subtract(BigDecimal.valueOf(5)));
//        userDetail1.setUpdateTime(new Date());
//        userDetailService.updateById(userDetail1);
        Date now = new Date();
        GreetInst greetInst = new GreetInst();

        greetInst.setUpdateTime(now);
        greetInst.setGreetTime(now);
        greetInst.setGreetStatus(2);
//        if (UserConstants.CUSTOMER_USER_TYPE.equals(userType)) {
//            greetInst.setGreetStatus(3);
//        }
//        else if (UserConstants.SALE_USER_TYPE.equals(userType)) {
//            greetInst.setGreetStatus(6);
//        }
        greetInst.setId(greet.getId());
        greetInst.setPayType(2);
        greetInstService.updateById(greetInst);

        return AjaxResult.successApp();
    }

    @PostMapping("/onlyChat")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult onlyChat(@RequestBody GreetInst greet) throws AlipayApiException {
        if (null == greet.getId()) {
            throw new RuntimeException("参数缺失!");
        }
        Date now = new Date();
        GreetInst greetInst = new GreetInst();
        greetInst.setId(greet.getId());
        greetInst.setGreetStatus(6);
        greetInst.setGreetAmount(BigDecimal.ZERO);
        greetInst.setUpdateTime(now);
        greetInst.setGreetTime(now);
        greetInstService.updateById(greetInst);

        return AjaxResult.successApp();
    }
}
