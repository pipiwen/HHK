package com.ruoyi.framework.aspectj;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.alipay.api.domain.UserDetails;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.Reconciliation;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.BusinessStatus;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.framework.web.domain.server.Sys;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.domain.SysReconciliation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 操作日志记录处理
 * 
 * @author ruoyi
 */
@Aspect
@Component
public class ReconciliationAspect
{
    private static final Logger log = LoggerFactory.getLogger(ReconciliationAspect.class);

    // 配置织入点
    @Pointcut("@annotation(com.ruoyi.common.annotation.Reconciliation)")
    public void reconciliationPointCut()
    {
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(reconciliation)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Reconciliation reconciliation, Object jsonResult)
    {
        handleReconciliation(joinPoint, reconciliation, null, jsonResult);
    }

    /**
     * 拦截异常操作
     * 
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(reconciliation)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Reconciliation reconciliation, Exception e)
    {
        handleReconciliation(joinPoint, reconciliation, e, null);
    }

    protected void handleReconciliation(final JoinPoint joinPoint, Reconciliation controllerLog, final Exception e, Object jsonResult)
    {
        try
        {
            // 处理设置注解上的参数
            Object args = joinPoint.getArgs();
            if (StringUtils.isNotNull(args))
            {
                String params = argsArrayToString(joinPoint.getArgs());
                String params1 = StringUtils.substring(params, 0, 2000);

                SysReconciliation sysReconciliation = JSONObject.parseObject(params1, SysReconciliation.class);
                sysReconciliation.setOperation(sysReconciliation.getRemark());
                sysReconciliation.setCreateTime(DateUtils.getNowDate());

                // 保存数据库
                AsyncManager.me().execute(AsyncFactory.recordReconciliation(sysReconciliation));
            }


        }
        catch (Exception exp)
        {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }


    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray)
    {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0)
        {
            for (Object o : paramsArray)
            {
                if (StringUtils.isNotNull(o) && !isFilterObject(o))
                {
                    try
                    {
                        Object jsonObj = JSONObject.toJSONString(o);
                        params += jsonObj.toString() + " ";
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象。
     * 
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o)
    {
        Class<?> clazz = o.getClass();
        if (clazz.isArray())
        {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        }
        else if (Collection.class.isAssignableFrom(clazz))
        {
            Collection collection = (Collection) o;
            for (Object value : collection)
            {
                return value instanceof MultipartFile;
            }
        }
        else if (Map.class.isAssignableFrom(clazz))
        {
            Map map = (Map) o;
            for (Object value : map.entrySet())
            {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
