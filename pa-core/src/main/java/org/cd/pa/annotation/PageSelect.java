package org.cd.pa.annotation;

import java.lang.annotation.*;

/**
 * @description: 页面数据对象 注解
 * @author: Mr.Wang
 * @create: 2019-08-11 10:58
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface PageSelect {

    /**
     * CSS-like query, like "#body"
     *
     * CSS选择器, 如 "#body"
     *
     * @return String
     */
    public String cssQuery() default "";
}
