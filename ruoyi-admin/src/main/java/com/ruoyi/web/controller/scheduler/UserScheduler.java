package com.ruoyi.web.controller.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.sms.SmsInfo;
import com.ruoyi.common.utils.sms.SmsUtil;
import com.ruoyi.ser.domain.*;
import com.ruoyi.ser.service.*;
import com.ruoyi.web.controller.app.award.AwardController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author syw
 */
@Slf4j
@Configuration
@EnableScheduling
public class UserScheduler {

    @Autowired
    private IUserDetailService userDetailService;

    @Autowired
    private IAwardConfigRuntimeService awardConfigRuntimeService;

    @Autowired
    private IGreetInstService greetInstService;

    @Autowired
    private IOrderRecordService orderRecordService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Scheduled(cron = "0 30 0 * * ?")
    public void userTasks() {
        //短信验证码过期删除
        threadPoolTaskExecutor.execute(()-> {
            log.info("[执行定时任务] -> 删除过期验证码");
            SmsUtil.smsMap.values().removeIf(smsInfo -> smsInfo.getSendTime().plusMinutes(1).isBefore(LocalDateTime.now()));

        });

        threadPoolTaskExecutor.execute(()-> {
            // 通信次数恢复默认值
            List<UserDetail> userList = userDetailService.list(new QueryWrapper<UserDetail>()
                    .ne("socket_num", -1)
            );
            userList.forEach(e-> {
                userDetailService.update(new UpdateWrapper<UserDetail>()
                        .set("socket_current_num_male", e.getSocketNumMale())
                        .set("socket_current_num_female", e.getSocketNumFemale())
                        .eq("id", e.getId())
                );
            });

        });

        threadPoolTaskExecutor.execute(()-> {
            List<AwardConfigRuntime> awardEndList = awardConfigRuntimeService.list(new QueryWrapper<AwardConfigRuntime>()
                    .lt("award_end_time", LocalDateTime.now()));

            for (AwardConfigRuntime a : awardEndList) {
                AwardController.USER_AWARD_MAP.remove(a.getId());
            }

        });

    }

    @Scheduled(fixedRate = 300000)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void fixedRate() throws Exception {
        log.info("[执行定时任务] -> 打招呼红包超时退款");
        // 查询所有已支付的聊天
        List<GreetInst> greetList = greetInstService.list(new QueryWrapper<GreetInst>()
                .eq("greet_status", 2));
        // 筛选>= 2h 的聊天
        if (!CollectionUtils.isEmpty(greetList)) {
            List<GreetInst> filterList = greetList.stream().filter(e ->
                    DateUtils.date2LocalDateTime(e.getGreetTime())
                            .plusHours(2)
                            .isBefore(LocalDateTime.now())
            ).collect(Collectors.toList());
            // 退款到余额
            Date now = new Date();
            List<GreetInst> greetBatchList = filterList.stream().map(e -> {
                GreetInst greetInst = new GreetInst();
                greetInst.setId(e.getId());
                greetInst.setGreetStatus(4);
                greetInst.setUpdateTime(now);
                return greetInst;
            }).collect(Collectors.toList());

//            List<Long> launchIdList = filterList.stream().map(GreetInst::getLaunchId).collect(Collectors.toList());
            // 批量更新状态
            greetInstService.updateBatchById(greetBatchList);
            // 更新用户表余额
            filterList.forEach(e -> {
                UserDetail userDetail = new UserDetail();
                userDetail.setUserId(e.getLaunchId());
                userDetail.setAccountBalance(e.getGreetAmount());
                userDetail.setUpdateTime(now);
                userDetail.setRemark("超时退款");
                userDetailService.updateAccountBalance(userDetail);
            });
            //查询订单
//            List<OrderRecord> orderRecordList = orderRecordService.list(new QueryWrapper<OrderRecord>()
//                    .in("user_id", launchIdList));
//            orderRecordList.forEach(e-> {
//                UserDetail userDetail = new UserDetail();
//                userDetail.setUserId(e.getUserId());
//                userDetail.setAccountBalance(e.getTotalAmount());
//                userDetail.setUpdateTime(now);
//                userDetailService.updateAccountBalance(userDetail);
//            });

        }
    }



}
