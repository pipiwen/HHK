package com.ruoyi.web.controller.app.open;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.DistanceUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.ser.domain.UserDetail;
import com.ruoyi.ser.service.IUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author syw
 */
@RestController
@RequestMapping("/app/open")
public class OpenController extends BaseController {

    @Autowired
    private IUserDetailService userDetailService;

    @GetMapping("/custList")
    public TableDataInfo saleList(UserDetail userDetail) {

        startPage();
        if (StringUtils.isNotEmpty(userDetail.getTag())) {
            String[] split = userDetail.getTag().split(",");
            userDetail.setTagList(Arrays.asList(split));
        }
        userDetail.setUserType("03");
        List<UserDetail> list = userDetailService.listByCondition(userDetail);
        list.forEach(e -> {
            if (StringUtils.isNotEmpty(e.getIndustryType())) {
                e.setIndustryTypeName(
                        DictUtils.getDictLabel("app_sale_industry", e.getIndustryType())
                );

            }
//            if (null != userDetail.getLat() && null != userDetail.getLon()) {
//                e.setDistance("未知");
//                if (null != e.getLat() && null != e.getLon()) {
//                    BigDecimal distance = DistanceUtils.getDistance(userDetail.getLon(), userDetail.getLat(), e.getLon(), e.getLat());
//                    e.setDistance(distance.toString());
//                }
//            }

        });
        List<UserDetail> collect = list.stream().sorted(Comparator.comparing(UserDetail::getDistance)).collect(Collectors.toList());

        return getDataTableApp(collect);
    }

    @GetMapping("/saleList")
    public TableDataInfo list(UserDetail userDetail) {
        startPage();
        if (StringUtils.isNotEmpty(userDetail.getTag())) {
            String[] split = userDetail.getTag().split(",");
            userDetail.setTagList(Arrays.asList(split));
        }
        userDetail.setUserType("02");
        List<UserDetail> list = userDetailService.listByCondition(userDetail);

        list.forEach(e -> {
            if (StringUtils.isNotEmpty(e.getIndustryType())) {
                e.setIndustryTypeName(
                        DictUtils.getDictLabel("app_sale_industry", e.getIndustryType())
                );

            }
//            if (null != userDetail.getLat() && null != userDetail.getLon()) {
//                e.setDistance("未知");
//                if (null != e.getLat() && null != e.getLon()) {
//                    BigDecimal distance = DistanceUtils.getDistance(userDetail.getLon(), userDetail.getLat(), e.getLon(), e.getLat());
//                    e.setDistance(distance.toString());
//                }
//            }
        });
        List<UserDetail> collect = list.stream().sorted(Comparator.comparing(UserDetail::getDistance)).collect(Collectors.toList());
        return getDataTableApp(collect);

    }
}
