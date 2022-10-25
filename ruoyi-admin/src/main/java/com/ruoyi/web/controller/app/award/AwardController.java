package com.ruoyi.web.controller.app.award;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.ser.domain.AwardConfig;
import com.ruoyi.ser.domain.AwardConfigRuntime;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.service.IAwardConfigRuntimeService;
import com.ruoyi.ser.service.IAwardConfigService;
import com.ruoyi.ser.service.IUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author syw
 */
@RestController
@RequestMapping("/app/award")
public class AwardController extends BaseController {

    public static final ConcurrentHashMap<Long, List<Long>> USER_AWARD_MAP = new ConcurrentHashMap<>();

    @Autowired
    private IAwardConfigService awardConfigService;

    @Autowired
    private IUserDetailService userDetailService;

    @Autowired
    private IAwardConfigRuntimeService awardConfigRuntimeService;



    @GetMapping("/getCurrentAward")
    public AjaxResult get(AwardConfig awardConfig) {
        AwardConfig one = awardConfigService.getOne(new QueryWrapper<AwardConfig>()
                .gt("award_end_time", new Date()));
        return AjaxResult.successApp(one);
    }

    @GetMapping("/list")
    public TableDataInfo list(AwardConfig awardConfig) {
        List<AwardConfig> list = awardConfigService.list();
        return getDataTableApp(list);
    }

    @PostMapping("/add")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult add(@RequestBody AwardConfig awardConfig) {
        awardConfig.setCreateTime(new Date());
        if (awardConfig.getAwardMax().compareTo(awardConfig.getAwardMin()) < 0) {
            return AjaxResult.error("最小金额无法大于最大值");
        }
        if (awardConfig.getAwardStartTime().isAfter(awardConfig.getAwardEndTime())) {
            return AjaxResult.error("时间设置错误");
        }
        awardConfigService.save(awardConfig);
        awardConfigRuntimeService.save(convert2Runtime(awardConfig));
        return AjaxResult.successApp();
    }

    @PostMapping("/edit")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult edit(@RequestBody AwardConfig awardConfig) {
        // 判断活动是否开始开始则无法更改
        AwardConfig config = awardConfigService.getById(awardConfig.getId());
        if (LocalDateTime.now().isAfter(config.getAwardStartTime())) {
            return AjaxResult.error("活动已开始无法更改！");
        }
        awardConfig.setUpdateTime(new Date());
        awardConfigService.updateById(awardConfig);
        // 同步至runtime
        AwardConfig config1 = awardConfigService.getById(awardConfig.getId());
        awardConfigRuntimeService.updateById(convert2Runtime(config1));
        return AjaxResult.successApp();
    }


    @PostMapping("/remove/{id}")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult status(@PathVariable Long id) {
        AwardConfig config = awardConfigService.getById(id);
        if (LocalDateTime.now().isAfter(config.getAwardStartTime())) {
            return AjaxResult.error("活动已开始无法删除！");
        }
        awardConfigService.removeById(id);
        awardConfigRuntimeService.removeById(id);
        return AjaxResult.successApp();
    }

    @PostMapping("/participate")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AjaxResult participate(@RequestBody AwardConfig awardConfig) {
        Long userId = ShiroUtils.getUserId();

        // 判断参与的活动是否在有效时间段
        AwardConfigRuntime awardRuntime = awardConfigRuntimeService.getOne(new QueryWrapper<AwardConfigRuntime>()
                .eq("config_id", awardConfig.getId()));
        // 判断用户是否已经参与该活动了
        List<Long> userIdList = USER_AWARD_MAP.get(awardRuntime.getId());
        if (null != userIdList) {
            if (userIdList.contains(userId)) {
                return AjaxResult.successApp("您已领取过该奖励");
            }
        }
        if (LocalDateTime.now().isBefore(awardRuntime.getAwardStartTime()) ||
                LocalDateTime.now().isAfter(awardRuntime.getAwardEndTime())) {
            return AjaxResult.error("活动未开始或已结束，请留意时间");
        }
        // 判断总额是否足够
        if(awardRuntime.getAwardTotal().doubleValue() <= 0) {
            return AjaxResult.error("红包已抢完!");
        }

        // 判断个数是否足够
        if(awardRuntime.getAwardNum() <= 0) {
            return AjaxResult.error("红包已抢完!");
        }
        // 随机金额
        double random = random(awardRuntime);
        if (random > awardRuntime.getAwardTotal().doubleValue()) {
            // 同步红包余额和数量
            awardConfigRuntimeService.update(new UpdateWrapper<AwardConfigRuntime>()
                    .set("award_num", (awardRuntime.getAwardNum() - 1))
                    .set("award_total", 0)
                    .eq("id", awardRuntime.getId()));
            // 存入用户余额
            UserDetail userAccount = userDetailService.getOne(new QueryWrapper<UserDetail>()
                    .select("user_id, account_balance")
                    .eq("user_id", userId)
            );
            UserDetail userDetail = new UserDetail();
            userDetail.setAccountBalance(awardRuntime.getAwardTotal());
            userDetail.setUserId(userId);
            userDetail.setRemark("抢红包");
            userDetailService.updateAccountBalance(userDetail);
//            BigDecimal result = userAccount.getAccountBalance().add(awardRuntime.getAwardTotal());
//            userDetailService.update(new UpdateWrapper<UserDetail>()
//                    .set("account_balance", result)
//                    .set("update_time", new Date())
//                    .eq("user_id", userId)
//            );
            // 用户参与标记
            participate(awardRuntime.getId());
            return AjaxResult.successApp(awardRuntime.getAwardTotal().doubleValue());
        }
        else {
            // 同步红包余额和数量
            awardConfigRuntimeService.update(new UpdateWrapper<AwardConfigRuntime>()
                    .set("award_num", (awardRuntime.getAwardNum() - 1))
                    .set("award_total", (awardRuntime.getAwardTotal().subtract(BigDecimal.valueOf(random)) ))
                    .eq("id", awardRuntime.getId()));
            // 存入用户余额
            UserDetail userAccount = userDetailService.getOne(new QueryWrapper<UserDetail>()
                    .select("user_id, account_balance")
                    .eq("user_id", userId)
            );

//            BigDecimal result = userAccount.getAccountBalance().add(BigDecimal.valueOf(random));
            UserDetail userDetail = new UserDetail();
            userDetail.setAccountBalance(BigDecimal.valueOf(random));
            userDetail.setUserId(userId);
            userDetail.setRemark("抢红包");
            userDetailService.updateAccountBalance(userDetail);
//            userDetailService.update(new UpdateWrapper<UserDetail>()
//                    .set("account_balance", result)
//                    .set("update_time", new Date())
//                    .eq("user_id", userId)
//            );
            // 用户参与标记
            participate(awardRuntime.getId());
            return AjaxResult.successApp(random);
        }
    }

    private AwardConfigRuntime convert2Runtime(AwardConfig awardConfig) {
        AwardConfigRuntime awardConfigRuntime = new AwardConfigRuntime();
        awardConfigRuntime.setConfigId(awardConfig.getId());
        awardConfigRuntime.setAwardName(awardConfig.getAwardName());
        awardConfigRuntime.setAwardNum(awardConfig.getAwardNum());
        awardConfigRuntime.setAwardTotal(awardConfig.getAwardTotal());
        awardConfigRuntime.setAwardMax(awardConfig.getAwardMax());
        awardConfigRuntime.setAwardMin(awardConfig.getAwardMin());
        awardConfigRuntime.setAwardStartTime(awardConfig.getAwardStartTime());
        awardConfigRuntime.setAwardEndTime(awardConfig.getAwardEndTime());
        awardConfigRuntime.setCreateTime(awardConfig.getCreateTime());
        return awardConfigRuntime;
    }

    private double random(AwardConfigRuntime awardConfigRuntime) {
        BigDecimal awardMin = awardConfigRuntime.getAwardMin();
        BigDecimal awardMax = awardConfigRuntime.getAwardMax();
        // 等额
        if (awardMin.equals(awardMax)) {
            return awardMin.doubleValue();
        }
        else {
            return awardMin
                    .add((awardMax.subtract(awardMin))
                            .multiply(BigDecimal.valueOf(new Random().nextDouble())
                                    .setScale(2, BigDecimal.ROUND_HALF_UP))).doubleValue();
        }
    }

    private void participate(Long awardId) {
        Long userId = ShiroUtils.getUserId();
        if (null == USER_AWARD_MAP.get(awardId)) {
            ArrayList<Long> userIds = new ArrayList<>();
            userIds.add(userId);
            USER_AWARD_MAP.put(awardId, userIds);
        }
        else {
            List<Long> userIds = USER_AWARD_MAP.get(awardId);
            userIds.add(userId);
        }

    }

}
