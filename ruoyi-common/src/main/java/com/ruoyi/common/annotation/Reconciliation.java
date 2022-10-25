package com.ruoyi.common.annotation;

import java.lang.annotation.*;

/**
 * @author syw
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Reconciliation {

    /**
     * 模块
     */
    String title() default "";

}
