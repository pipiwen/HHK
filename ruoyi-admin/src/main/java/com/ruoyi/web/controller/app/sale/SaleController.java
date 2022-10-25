package com.ruoyi.web.controller.app.sale;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.*;
import com.ruoyi.ser.domain.*;
import com.ruoyi.ser.service.*;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author syw
 */
@RestController
@RequestMapping("/app/sale")
public class SaleController extends BaseController {

    @Autowired
    private IUserDetailService userDetailService;

    @Autowired
    private IStoreInfoService storeInfoService;

    @Autowired
    private ICustomerBlacklistService customerBlacklistService;

    @Autowired
    private ICustomerAttentionService customerAttentionService;

    @Autowired
    private ISaleAttentionService saleAttentionService;

    @Autowired
    private ISaleBlacklistService saleBlacklistService;

    @Autowired
    private IGreetInstService greetInstService;



    @GetMapping("/list")
    public TableDataInfo list(UserDetail userDetail) {
        startPage();
        if (StringUtils.isNotEmpty(userDetail.getTag())) {
            String[] split = userDetail.getTag().split(",");
            userDetail.setTagList(Arrays.asList(split));
        }
        userDetail.setUserType("02");
        List<UserDetail> list = userDetailService.listByCondition(userDetail);
        // 查询红包个数
        list.forEach(e-> {
            int greetCount = greetInstService.count(new QueryWrapper<GreetInst>()
                    .eq("greet_type", 1)
                    .eq("greet_status", 3)
                    .eq("launch_id", e.getUserId()));
            e.setGreetSuccessCount(greetCount);
        });
        // 查看自己的经纬度
//        UserDetail self = userDetailService.getOne(new QueryWrapper<UserDetail>()
//                .eq("user_id", ShiroUtils.getUserId()));
        if (null != userDetail.getLat() && null != userDetail.getLon()) {
            list.forEach(e -> {
                if (StringUtils.isNotEmpty(e.getIndustryType())) {
                    e.setIndustryTypeName(
                            DictUtils.getDictLabel("app_sale_industry", e.getIndustryType())
                    );

                }
//                e.setDistance("999");
//                if (null != e.getLat() && null != e.getLon()) {
//                    BigDecimal distance = DistanceUtils.getDistance(userDetail.getLon(), userDetail.getLat(), e.getLon(), e.getLat());
//                    e.setDistance(distance.toString());
//                }
            });
        }
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
        List<CustomerBlacklist> saleIdList = customerBlacklistService.list(new QueryWrapper<CustomerBlacklist>()
                .eq("cust_id", ShiroUtils.getUserId()));
        if (!CollectionUtils.isEmpty(saleIdList)) {
            List<Long> saleIds = saleIdList.stream().map(CustomerBlacklist::getSaleId).collect(Collectors.toList());
            List<UserDetail> sales = list.stream().filter(e -> !saleIds.contains(e.getUserId())).collect(Collectors.toList());
            return getDataTableApp(sales);
        }
        return getDataTableApp(list);

    }

    @GetMapping("/info")
    public AjaxResult info() {
        Long userId = ShiroUtils.getUserId();
        UserDetail info = userDetailService.getOne(new QueryWrapper<UserDetail>()
                .eq("user_id", userId));
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
        return AjaxResult.successApp(info);
    }



    @PostMapping("/add")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult add(@RequestBody UserDetail userDetail) {
        SysUser sysUser = ShiroUtils.getSysUser();
//        userDetail.sysUser.getUserType()
        userDetail.setCreateTime(DateUtils.getNowDate());
        userDetailService.save(userDetail);
        return AjaxResult.successApp();
    }

    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody UserDetail userDetail) {
        SysUser sysUser = ShiroUtils.getSysUser();
        userDetailService.update(new UpdateWrapper<UserDetail>()
                .set(null != userDetail.getCity(), "city", userDetail.getCity())
                .set(null != userDetail.getArea(), "area", userDetail.getArea())
                .set(null != userDetail.getTag(), "tag", userDetail.getTag())
                .set(null != userDetail.getSex(), "sex", userDetail.getSex())
                .set(null != userDetail.getLat(), "lat", userDetail.getLat())
                .set(null != userDetail.getLon(), "lon", userDetail.getLon())
                .set(null != userDetail.getBirthday(), "birthday", userDetail.getBirthday())
                .set(null != userDetail.getPhotos(), "photos", userDetail.getPhotos())
                .set(null != userDetail.getIndustryType(), "industry_type", userDetail.getIndustryType())
                .set(null != userDetail.getShops(), "shops", userDetail.getShops())
                .set(null != userDetail.getNickName(), "nick_name", userDetail.getNickName())
                .set(null != userDetail.getSocketNumMale(), "socket_num_male", userDetail.getSocketNumMale())
                .set(null != userDetail.getSocketNumFemale(), "socket_num_female", userDetail.getSocketNumFemale())
                .set(null != userDetail.getSocketNumMale(), "socket_current_num_male", userDetail.getSocketNumMale())
                .set(null != userDetail.getSocketNumFemale(), "socket_current_num_female", userDetail.getSocketNumFemale())
                .set(null != userDetail.getGeneration(), "generation", userDetail.getGeneration())
                .set(null != userDetail.getDescription(), "description", userDetail.getDescription())
                .set(null != userDetail.getWorkingTime(), "working_time", userDetail.getWorkingTime())
                .set(null != userDetail.getRestTime(), "rest_time", userDetail.getRestTime())
                .set(null != userDetail.getIsImport(), "is_import", userDetail.getIsImport())
                .set(null != userDetail.getAttribution(), "attribution", userDetail.getAttribution())
                .set("update_time", DateUtils.getNowDate())
                .eq("user_id", sysUser.getUserId()));
        return AjaxResult.successApp();
    }

    @GetMapping("/saleType")
    public AjaxResult getSaleType() {
        List<SysDictData> saleTypeList = DictUtils.getDictCache("app_sale_type");
        return AjaxResult.successApp(saleTypeList);
    }

    @GetMapping("/industryType")
    public AjaxResult getIndustryType() {
        List<SysDictData> industryTypeList = DictUtils.getDictCache("app_sale_industry");
        return AjaxResult.successApp(industryTypeList);
    }

    /** 加关注 */
    @RepeatSubmit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PostMapping("/attention")
    public AjaxResult attention(Long custId) {
        if (null == custId) {
            throw new RuntimeException("参数异常");
        }
        // 检查互斥
        List<SaleBlacklist> blacklistList = saleBlacklistService.list(new QueryWrapper<SaleBlacklist>()
                .eq("cust_id", custId)
                .eq("sale_id", ShiroUtils.getUserId())
        );
        if (!CollectionUtils.isEmpty(blacklistList)) {
            saleBlacklistService.remove(new QueryWrapper<SaleBlacklist>()
                    .eq("cust_id", custId)
                    .eq("sale_id", ShiroUtils.getUserId())
            );
        }
        SaleAttention saleAttention = new SaleAttention();
        saleAttention.setCustId(custId);
        saleAttention.setSaleId(ShiroUtils.getUserId());
        saleAttentionService.save(saleAttention);
        return AjaxResult.successApp();
    }

    /** 取消关注 */
    @RepeatSubmit
    @PostMapping("/takeOff")
    public AjaxResult takeOff(Long custId) {
        if (null == custId) {
            throw new RuntimeException("参数异常");
        }
        saleAttentionService.remove(new QueryWrapper<SaleAttention>()
                .eq("cust_id", custId)
                .eq("sale_id", ShiroUtils.getUserId())
        );

        return AjaxResult.successApp();
    }

    /** 黑名单 */
    @PostMapping("/blacklist")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult blacklist(Long custId) {
        if (null == custId) {
            throw new RuntimeException("参数异常");
        }
        // 检查互斥
        List<SaleAttention> attentionList = saleAttentionService.list(new QueryWrapper<SaleAttention>()
                .eq("cust_id", custId)
                .eq("sale_id", ShiroUtils.getUserId())
        );
        if (!CollectionUtils.isEmpty(attentionList)) {
            saleAttentionService.remove(new QueryWrapper<SaleAttention>()
                    .eq("cust_id", custId)
                    .eq("sale_id", ShiroUtils.getUserId())
            );
        }
        SaleBlacklist saleBlacklist = new SaleBlacklist();
        saleBlacklist.setCustId(custId);
        saleBlacklist.setSaleId(ShiroUtils.getUserId());
        saleBlacklistService.save(saleBlacklist);

        return AjaxResult.successApp();
    }

    /** 黑名单移除 */
    @PostMapping("/blacklist-remove")
    public AjaxResult blacklistRemove(Long custId) {
        saleBlacklistService.remove(new QueryWrapper<SaleBlacklist>()
                .eq("cust_id", custId)
                .eq("sale_id", ShiroUtils.getUserId())
        );

        return AjaxResult.successApp();
    }

    @GetMapping("/listBlacklist")
    public TableDataInfo listBlackList(UserDetail userDetail) {
        startPage();
        userDetail.setUserId(ShiroUtils.getUserId());
        List<UserDetail> userDetails = saleBlacklistService.listBlacklist(userDetail);
        return getDataTableApp(userDetails);
    }

    @GetMapping("/listAttention")
    public TableDataInfo listAttention(UserDetail userDetail) {
        startPage();
        userDetail.setUserId(ShiroUtils.getUserId());
        List<UserDetail> userDetails = saleAttentionService.listAttention(userDetail);
        return getDataTableApp(userDetails);
    }

    @GetMapping("/attentionNum")
    public AjaxResult attentionNum(UserDetail userDetail) {
        // 查看销售被关注的数量
        if (UserConstants.SALE_USER_TYPE.equals(userDetail.getUserType())) {
            int saleAttentionCount = customerAttentionService.count(new QueryWrapper<CustomerAttention>()
                    .eq("sale_id", userDetail.getUserId()));
            return AjaxResult.successApp(saleAttentionCount);
        }

        // 查看销售被关注的数量
        if (UserConstants.CUSTOMER_USER_TYPE.equals(userDetail.getUserType())) {
            int custAttentionCount = saleAttentionService.count(new QueryWrapper<SaleAttention>()
                    .eq("cust_id", userDetail.getUserId()));
            return AjaxResult.successApp(custAttentionCount);
        }

        return AjaxResult.error();
    }
}
