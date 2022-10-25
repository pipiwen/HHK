package com.ruoyi.web.controller.app.certification;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.HttpUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.ser.domain.CertificationEntity;
import com.ruoyi.ser.domain.IdentityInfo;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.service.IUserDetailService;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author syw
 */
@RestController
@RequestMapping("/app/verified")
public class VerifiedController extends BaseController {

    @Autowired
    private IUserDetailService userDetailService;

    @PostMapping("/certification")
    public AjaxResult verified(@RequestBody IdentityInfo info) throws Exception{
        String host = "https://phone3.market.alicloudapi.com";
        String path = "/phonethree";
        String method = "GET";
        String appcode = "62acde74bb4b475e9e724972de95f0da";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("idcard", info.getIdCard());
        querys.put("phone", info.getPhone());
        querys.put("realname", info.getRealName());
        HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
        String result = EntityUtils.toString(response.getEntity());
        CertificationEntity certificationEntity = JSON.parseObject(result, CertificationEntity.class);
        // 验证是否认证成功
        if (Constants.HTTP_STATUS_200.equals(certificationEntity.getCode()) &&
                Constants.CERTIFICATION_SUCCESS.equals(certificationEntity.getMsg())) {
            userDetailService.update(new UpdateWrapper<UserDetail>()
                    .set("id_card", info.getIdCard())
                    .set("update_time", new Date())
                    .eq("user_id", ShiroUtils.getUserId()));

            return AjaxResult.successApp();
        }
        return AjaxResult.error("实名认证失败");
    }

    public static void main(String[] args) {
        String host = "https://phone3.market.alicloudapi.com";
        String path = "/phonethree";
        String method = "GET";
        String appcode = "62acde74bb4b475e9e724972de95f0da";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("idcard", "320481199708043619");
        querys.put("phone", "18115151318");
        querys.put("realname", "史毓文");


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            System.out.println(response.toString());
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
