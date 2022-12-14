package com.ruoyi.web.controller.app.sys;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sms.SmsInfo;
import com.ruoyi.common.utils.sms.SmsUtil;
import com.ruoyi.common.utils.uuid.InvitationCode;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.ser.domain.AppVersionConfig;
import com.ruoyi.ser.domain.SysRet;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.service.IAppVersionConfigService;
import com.ruoyi.ser.service.IUserDetailService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author syw
 */
@RestController
@RequestMapping("/app/sys")
public class SysController extends BaseController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IAppVersionConfigService appVersionConfigService;

    @Autowired
    private IUserDetailService userDetailService;

    @Autowired
    private SysPasswordService passwordService;
    /**
     * ??????????????????
     */
    @Log(title = "app????????????", businessType = BusinessType.INSERT)
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @PostMapping("/register")
    public AjaxResult register(@RequestBody SysUser user) throws ClientException {
        // ?????????
        String phone = user.getLoginName();
        SmsInfo smsInfo = SmsUtil.smsMap.get(phone);
        // ????????????
        if (!"666666".equals(user.getMsgCode())) {
            // ????????????
            if (null == smsInfo) {
                return AjaxResult.error("???????????????");
            }
            if (!smsInfo.getCode().equals(user.getMsgCode())) {
                return AjaxResult.error("???????????????");
            }
            if (smsInfo.getSendTime().plusMinutes(1).isBefore(LocalDateTime.now())) {
                return AjaxResult.error("??????????????????");
            }
        }

        String username = "app:" + user.getUserType() + user.getLoginName();
        user.setLoginName(username);
        // ???????????????
        SysUser sysUser = userService.selectUserByLoginNameApp(username, user.getUserType());
        String originPassword = user.getPassword();
        if (null != sysUser) {
            // ?????????????????????
            UsernamePasswordToken token = new UsernamePasswordToken(username, originPassword, false);
            Subject subject = SecurityUtils.getSubject();

            subject.login(token);
            Serializable id = SecurityUtils.getSubject().getSession().getId();
            return AjaxResult.successApp(id);
        }
        else {
            user.setPhonenumber(phone);
            user.setSalt(ShiroUtils.randomSalt());
            user.setPassword(passwordService.encryptPassword(user.getLoginName(), originPassword, user.getSalt()));
            user.setRoleIds(new Long[]{100L});
            user.setCreateBy("app");
            userService.insertUser(user);
            // ??????????????????????????????
            UserDetail userDetail = new UserDetail();
            userDetail.setCreateTime(DateUtils.getNowDate());
            userDetail.setUserId(user.getUserId());
            userDetail.setNickName(user.getUserName());
            userDetail.setUserType(user.getUserType());
            userDetail.setSex(Integer.parseInt(user.getSex()));
            userDetail.setBirthday(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, new Date()));
            userDetail.setInvitationCode(generateInvitationCode());
            // ??????????????????
            userDetail.setAttribution(user.getAttribution());
            userDetail.setIsImport(user.getIsImport());
            userDetailService.save(userDetail);

            //??????
//        String username = "app:" + user.getUserType() + user.getLoginName();
            UsernamePasswordToken token = new UsernamePasswordToken(username, originPassword, false);
            Subject subject = SecurityUtils.getSubject();

            subject.login(token);
            Serializable id = SecurityUtils.getSubject().getSession().getId();
            SysRet sysRet = new SysRet();
            sysRet.setId(id.toString());
            sysRet.setPhone(phone);
            return AjaxResult.successApp(id);
        }
    }

    /**
     * ????????????
     */
    @Log(title = "app????????????", businessType = BusinessType.INSERT)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PostMapping("/logout")
    public AjaxResult logout(@RequestBody SysUser user) throws ClientException {
        // ?????????
//        String phone = user.getPhonenumber();
        String phone = ShiroUtils.getSysUser().getPhonenumber();
        SmsInfo smsInfo = SmsUtil.smsMap.get(phone);
        // ????????????
        if (!"666666".equals(user.getMsgCode())) {
            // ????????????
            if (null == smsInfo) {
                return AjaxResult.error("???????????????");
            }
            if (!smsInfo.getCode().equals(user.getMsgCode())) {
                return AjaxResult.error("???????????????");
            }
            if (smsInfo.getSendTime().plusMinutes(1).isBefore(LocalDateTime.now())) {
                return AjaxResult.error("??????????????????");
            }
        }

        String username = ShiroUtils.getSysUser().getLoginName();
        user.setLoginName(username);
        // ???????????????
        SysUser sysUser = userService.selectUserByLoginNameApp(username, ShiroUtils.getSysUser().getUserType());
        // ?????????????????????
        if (null != sysUser) {
            // ??????????????????
            userDetailService.remove(new QueryWrapper<UserDetail>()
                    .eq("user_id", sysUser.getUserId()));

            // ??????????????????
            userService.deleteUserByIdApp(sysUser.getUserId());

            return AjaxResult.successApp("????????????");
        }
        else {

            return AjaxResult.error("?????????????????????");
        }
    }

    private String generateInvitationCode() {
        String code = InvitationCode.generateCode();
        List<UserDetail> invitationCodeList = userDetailService.list(new QueryWrapper<UserDetail>()
                .select("invitation_code")
                .eq("invitation_code", code));
        if (!CollectionUtils.isEmpty(invitationCodeList)) {
            return generateInvitationCode();
        }
        else {
            return code;
        }
    }

    /** ?????????????????? */
    @GetMapping("/sendSms")
    public AjaxResult sendSms(String phone) throws ClientException {
        String msgCode = SmsUtil.getMsgCode();
        SmsUtil.sendSms(msgCode, phone);
        return AjaxResult.successApp();
    }

    @GetMapping("/getVersion")
    public AjaxResult getVersion(Integer type) {
        AppVersionConfig one = appVersionConfigService.getOne(new QueryWrapper<AppVersionConfig>()
                .eq("version_type", type)
                .orderByDesc("version_code")
                .last("limit 1")
        );
        return AjaxResult.successApp(one);
    }

}
