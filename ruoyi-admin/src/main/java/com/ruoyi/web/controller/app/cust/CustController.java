package com.ruoyi.web.controller.app.cust;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.DistanceUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.ser.domain.*;
import com.ruoyi.ser.service.*;
import org.apache.catalina.User;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author syw
 */
@RestController
@RequestMapping("/app/cust")
public class CustController extends BaseController {

    @Autowired
    private ICustomerAttentionService customerAttentionService;

    @Autowired
    private ICustomerBlacklistService customerBlacklistService;

    @Autowired
    private ISaleBlacklistService saleBlacklistService;

    @Autowired
    private ISaleAttentionService saleAttentionService;

    @Autowired
    private IUserDetailService userDetailService;

    @Autowired
    private IStoreInfoService storeInfoService;

    @GetMapping("/list")
    public TableDataInfo saleList(UserDetail userDetail) {

        startPage();
        if (StringUtils.isNotEmpty(userDetail.getTag())) {
            String[] split = userDetail.getTag().split(",");
            userDetail.setTagList(Arrays.asList(split));
        }
        userDetail.setUserType("03");
        List<UserDetail> list = userDetailService.listByCondition(userDetail);
        // 查看自己的经纬度
//        UserDetail self = userDetailService.getOne(new QueryWrapper<UserDetail>()
//                .eq("user_id", ShiroUtils.getUserId()));
        list.forEach(e -> {
            if (StringUtils.isNotEmpty(e.getIndustryType())) {
                e.setIndustryTypeName(
                        DictUtils.getDictLabel("app_sale_industry", e.getIndustryType())
                );
            }
//            e.setDistance("999");
//            if (null != userDetail.getLat() && null != userDetail.getLon()) {
//                if (null != e.getLat() && null != e.getLon()) {
//                    BigDecimal distance = DistanceUtils.getDistance(userDetail.getLon(), userDetail.getLat(), e.getLon(), e.getLat());
//                    e.setDistance(distance.toString());
//                }
//            }
        });
//        List<UserDetail> collect;
//        if (null == userDetail.getAttribution()) {
//            collect = list.stream()
//                    .filter(e-> Double.valueOf(e.getDistance()).compareTo(50.00) <= 0)
//                    .sorted(Comparator.comparing(e -> Double.valueOf(e.getDistance()))).collect(Collectors.toList());
//
//        }
//        else {
//            collect = list.stream()
//                    .filter(e-> userDetail.getAttribution().equals(e.getAttribution()) || Double.valueOf(e.getDistance()).compareTo(50.00) <= 0)
//                    .sorted(Comparator.comparing(e -> Double.valueOf(e.getDistance()))).collect(Collectors.toList());
//
//        }

        // 去除黑名单中的id
        List<SaleBlacklist> saleIdList = saleBlacklistService.list(new QueryWrapper<SaleBlacklist>()
                .eq("sale_id", ShiroUtils.getUserId()));
        if (!CollectionUtils.isEmpty(saleIdList)) {
            List<Long> custIds = saleIdList.stream().map(SaleBlacklist::getCustId).collect(Collectors.toList());
            List<UserDetail> custs = list.stream().filter(e -> !custIds.contains(e.getUserId())).collect(Collectors.toList());
            return getDataTableApp(custs);
        }

        return getDataTableApp(list);
    }

    @GetMapping("/info")
    public AjaxResult info() {
        Long userId = ShiroUtils.getUserId();
        UserDetail info = userDetailService.getUserDetail(userId);
        List<String> tagList = new ArrayList<>();
        if (StringUtils.isNotEmpty(info.getTag())) {
            for (String s : info.getTag().split(",")) {
                String tagName = DictUtils.getDictLabel("app_sale_type", s);
                tagList.add(tagName);
            }
        }
        info.setTagList(tagList);
        return AjaxResult.successApp(info);
    }

    @GetMapping("/getSaleInfo")
    public AjaxResult getSaleInfo(UserDetail userDetail) {
        Long userId = userDetail.getUserId();
        UserDetail info = userDetailService.getUserDetail(userId);
        List<String> tagList = new ArrayList<>();
        if (StringUtils.isNotEmpty(info.getTag())) {
            for (String s : info.getTag().split(",")) {
                String tagName = DictUtils.getDictLabel("app_sale_type", s);
                tagList.add(tagName);
            }
        }
        if (StringUtils.isNotEmpty(info.getIndustryType())) {
            info.setIndustryTypeName(
                    DictUtils.getDictLabel("app_sale_industry", info.getIndustryType())
            );

        }

        List<StoreInfo> storeList = storeInfoService.list(new QueryWrapper<StoreInfo>()
                .eq("user_id", userId)
        );
        info.setTagList(tagList);
        info.setShopList(storeList);

        // 客户端
        List<CustomerBlacklist> blacklistList = customerBlacklistService.list(new QueryWrapper<CustomerBlacklist>()
                .eq("cust_id", ShiroUtils.getUserId()));
        if (!CollectionUtils.isEmpty(blacklistList)) {
            List<Long> collect = blacklistList.stream().map(CustomerBlacklist::getSaleId).collect(Collectors.toList());
            if (collect.contains(info.getUserId())) {
                info.setIsBlacklist(1);
            }
        }
        else {
            info.setIsBlacklist(0);
        }

        List<CustomerAttention> saleAttentionList = customerAttentionService.list(new QueryWrapper<CustomerAttention>()
                .eq("cust_id", ShiroUtils.getUserId()));
        if (!CollectionUtils.isEmpty(saleAttentionList)) {
            List<Long> collect = saleAttentionList.stream().map(CustomerAttention::getSaleId).collect(Collectors.toList());
            if (collect.contains(info.getUserId())) {
                info.setIsAttention(1);
            }
        }
        else {
            info.setIsAttention(0);
        }
        return AjaxResult.successApp(info);
    }



    @GetMapping("/detail")
    public AjaxResult userInfo(UserDetail userDetail) {
        if (null == userDetail.getPhonenumber() || null == userDetail.getUserType()) {
            return AjaxResult.error("手机号或用户类型不全！");
        }
        String userName = "app:" + userDetail.getUserType() + userDetail.getPhonenumber();

        UserDetail userInfo = userDetailService.getCustomerDetail(userName);
        String userType = ShiroUtils.getSysUser().getUserType();
        userInfo.setIsBlacklist(0);
        userInfo.setIsAttention(0);
        // 销售端
        if (UserConstants.SALE_USER_TYPE.equals(userType)) {
            List<SaleBlacklist> blacklistList = saleBlacklistService.list(new QueryWrapper<SaleBlacklist>()
                    .eq("sale_id", ShiroUtils.getUserId()));
            if (!CollectionUtils.isEmpty(blacklistList)) {
                List<Long> collect = blacklistList.stream().map(SaleBlacklist::getCustId).collect(Collectors.toList());
                if (collect.contains(userInfo.getUserId())) {
                    userInfo.setIsBlacklist(1);
                }
            }

            List<SaleAttention> saleAttentionList = saleAttentionService.list(new QueryWrapper<SaleAttention>()
                    .eq("sale_id", ShiroUtils.getUserId()));
            if (!CollectionUtils.isEmpty(saleAttentionList)) {
                List<Long> collect = saleAttentionList.stream().map(SaleAttention::getCustId).collect(Collectors.toList());
                if (collect.contains(userInfo.getUserId())) {
                    userInfo.setIsAttention(1);
                }
            }
        }

        // 客户端
        if (UserConstants.CUSTOMER_USER_TYPE.equals(userType)) {
            List<CustomerBlacklist> blacklistList = customerBlacklistService.list(new QueryWrapper<CustomerBlacklist>()
                    .eq("cust_id", ShiroUtils.getUserId()));
            if (!CollectionUtils.isEmpty(blacklistList)) {
                List<Long> collect = blacklistList.stream().map(CustomerBlacklist::getSaleId).collect(Collectors.toList());
                if (collect.contains(userInfo.getUserId())) {
                    userInfo.setIsBlacklist(1);
                }
            }

            List<CustomerAttention> saleAttentionList = customerAttentionService.list(new QueryWrapper<CustomerAttention>()
                    .eq("cust_id", ShiroUtils.getUserId()));
            if (!CollectionUtils.isEmpty(saleAttentionList)) {
                List<Long> collect = saleAttentionList.stream().map(CustomerAttention::getSaleId).collect(Collectors.toList());
                if (collect.contains(userInfo.getUserId())) {
                    userInfo.setIsAttention(1);
                }
            }
        }

        return AjaxResult.successApp(userInfo);
    }


    /** 加关注 */
    @RepeatSubmit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PostMapping("/attention")
    public AjaxResult attention(Long saleId) {
        if (null == saleId) {
            throw new RuntimeException("参数异常");
        }
        // 检查互斥
        List<CustomerBlacklist> blacklistList = customerBlacklistService.list(new QueryWrapper<CustomerBlacklist>()
                .eq("cust_id", ShiroUtils.getUserId())
                .eq("sale_id", saleId)
        );
        if (!CollectionUtils.isEmpty(blacklistList)) {
            customerBlacklistService.remove(new QueryWrapper<CustomerBlacklist>()
                    .eq("cust_id", ShiroUtils.getUserId())
                    .eq("sale_id", saleId)
            );
        }
        CustomerAttention customerAttention = new CustomerAttention();
        customerAttention.setCustId(ShiroUtils.getUserId());
        customerAttention.setSaleId(saleId);
        customerAttentionService.save(customerAttention);
        return AjaxResult.successApp();
    }

    /** 取消关注 */
    @RepeatSubmit
    @PostMapping("/takeOff")
    public AjaxResult takeOff(Long saleId) {
        if (null == saleId) {
            throw new RuntimeException("参数异常");
        }
        customerAttentionService.remove(new QueryWrapper<CustomerAttention>()
                .eq("sale_id", saleId)
                .eq("cust_id", ShiroUtils.getUserId())
        );

        return AjaxResult.successApp();
    }

    /** 黑名单 */
    @PostMapping("/blacklist")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult blacklist(Long saleId) {
        if (null == saleId) {
            throw new RuntimeException("参数异常");
        }
        // 检查互斥
        List<CustomerAttention> attentionList = customerAttentionService.list(new QueryWrapper<CustomerAttention>()
                .eq("cust_id", ShiroUtils.getUserId())
                .eq("sale_id", saleId)
        );
        if (!CollectionUtils.isEmpty(attentionList)) {
            customerAttentionService.remove(new QueryWrapper<CustomerAttention>()
                    .eq("cust_id", ShiroUtils.getUserId())
                    .eq("sale_id", saleId)
            );
        }
        CustomerBlacklist customerBlacklist = new CustomerBlacklist();
        customerBlacklist.setCustId(ShiroUtils.getUserId());
        customerBlacklist.setSaleId(saleId);
        customerBlacklistService.save(customerBlacklist);

        return AjaxResult.successApp();
    }

    @PostMapping("/blacklist-remove")
    public AjaxResult blacklistRemove(Long saleId) {
        customerBlacklistService.remove(new QueryWrapper<CustomerBlacklist>()
                .eq("cust_id", ShiroUtils.getUserId())
                .eq("sale_id", saleId)
        );

        return AjaxResult.successApp();
    }

    @GetMapping("/listBlacklist")
    public TableDataInfo listBlackList(UserDetail userDetail) {
        startPage();
        userDetail.setUserId(ShiroUtils.getUserId());
        List<UserDetail> userDetails = customerBlacklistService.listBlacklist(userDetail);
        return getDataTableApp(userDetails);
    }

    @GetMapping("/listAttention")
    public TableDataInfo listAttention(UserDetail userDetail) {
        startPage();
        userDetail.setUserId(ShiroUtils.getUserId());
        List<UserDetail> userDetails = customerAttentionService.listAttention(userDetail);
        return getDataTableApp(userDetails);
    }

    @GetMapping("/detail/{userName}")
    public AjaxResult userInfo(@PathVariable("userName") String userName) {

        UserDetail userInfo = userDetailService.getCustomerDetail(userName);
        String userType = ShiroUtils.getSysUser().getUserType();
        userInfo.setIsBlacklist(0);
        userInfo.setIsAttention(0);
        // 客户端
        if (UserConstants.SALE_USER_TYPE.equals(userType)) {
            List<SaleBlacklist> blacklistList = saleBlacklistService.list(new QueryWrapper<SaleBlacklist>()
                    .eq("sale_id", ShiroUtils.getUserId()));
            if (!CollectionUtils.isEmpty(blacklistList)) {
                List<Long> collect = blacklistList.stream().map(SaleBlacklist::getCustId).collect(Collectors.toList());
                if (collect.contains(userInfo.getUserId())) {
                    userInfo.setIsBlacklist(1);
                }
            }

            List<SaleAttention> saleAttentionList = saleAttentionService.list(new QueryWrapper<SaleAttention>()
                    .eq("sale_id", ShiroUtils.getUserId()));
            if (!CollectionUtils.isEmpty(saleAttentionList)) {
                List<Long> collect = saleAttentionList.stream().map(SaleAttention::getCustId).collect(Collectors.toList());
                if (collect.contains(userInfo.getUserId())) {
                    userInfo.setIsAttention(1);
                }
            }
        }

        // 客户端
        if (UserConstants.CUSTOMER_USER_TYPE.equals(userType)) {
            List<CustomerBlacklist> blacklistList = customerBlacklistService.list(new QueryWrapper<CustomerBlacklist>()
                    .eq("cust_id", ShiroUtils.getUserId()));
            if (!CollectionUtils.isEmpty(blacklistList)) {
                List<Long> collect = blacklistList.stream().map(CustomerBlacklist::getSaleId).collect(Collectors.toList());
                if (collect.contains(userInfo.getUserId())) {
                    userInfo.setIsBlacklist(1);
                }
            }

            List<CustomerAttention> saleAttentionList = customerAttentionService.list(new QueryWrapper<CustomerAttention>()
                    .eq("cust_id", ShiroUtils.getUserId()));
            if (!CollectionUtils.isEmpty(saleAttentionList)) {
                List<Long> collect = saleAttentionList.stream().map(CustomerAttention::getSaleId).collect(Collectors.toList());
                if (collect.contains(userInfo.getUserId())) {
                    userInfo.setIsAttention(1);
                }
            }
        }

        return AjaxResult.successApp(userInfo);
    }

    @GetMapping("/socket")
    public AjaxResult socket(UserDetail userDetail) {

        String userType = ShiroUtils.getSysUser().getUserType();
        //客户端通讯(顾客端：判断销售是否在黑名单)
        if (UserConstants.CUSTOMER_USER_TYPE.equals(userType)) {
            List<CustomerBlacklist> blacklistList = customerBlacklistService.list(new QueryWrapper<CustomerBlacklist>()
                    .eq("cust_id", ShiroUtils.getUserId()));
            if (!CollectionUtils.isEmpty(blacklistList)) {
                List<Long> collect = blacklistList.stream().map(CustomerBlacklist::getSaleId).collect(Collectors.toList());
                if (collect.contains(userDetail.getUserId())) {
                    return AjaxResult.successApp(false);
                }
            }
        }
        //销售端：1. 判断顾客是否在黑名单 2. 判断 顾客 剩余交流次数
        if (UserConstants.SALE_USER_TYPE.equals(userType)) {
            List<SaleBlacklist> blacklistList1 = saleBlacklistService.list(new QueryWrapper<SaleBlacklist>()
                    .eq("sale_id", ShiroUtils.getUserId()));
            if (!CollectionUtils.isEmpty(blacklistList1)) {
                List<Long> collect = blacklistList1.stream().map(SaleBlacklist::getCustId).collect(Collectors.toList());
                if (collect.contains(userDetail.getUserId())) {
                    return AjaxResult.successApp(false);
                }
            }
            UserDetail userInfo = userDetailService.getOne(new QueryWrapper<UserDetail>()
                    .eq("user_id", userDetail.getUserId()));
            // 男
            if (0 == userDetail.getSex()) {
                if(userInfo.getSocketNumMale() == -1) {
                    return AjaxResult.successApp(true);
                }
                if(userInfo.getSocketCurrentNumMale() != -1 && userInfo.getSocketCurrentNumMale() <= 0) {
                    return AjaxResult.successApp(false);
                }
                else {
                    userDetailService.update(new UpdateWrapper<UserDetail>()
                            .set("socket_current_num_male", userInfo.getSocketCurrentNumMale() - 1 )
                            .eq("user_id", userDetail.getUserId()));
                }
            }

            // 女
            if (1 == userDetail.getSex()) {
                if (userInfo.getSocketNumFemale() == -1) {
                    return AjaxResult.successApp(true);
                }
                if (userInfo.getSocketCurrentNumFemale() != -1 && userInfo.getSocketCurrentNumFemale() <= 0) {
                    return AjaxResult.successApp(false);
                } else {
                    userDetailService.update(new UpdateWrapper<UserDetail>()
                            .set("socket_current_num_female", userInfo.getSocketCurrentNumFemale() - 1)
                            .eq("user_id", userDetail.getUserId()));
                }
            }


        }

        return AjaxResult.successApp(true);
    }
}
