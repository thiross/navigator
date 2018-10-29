package com.tutuur.navigator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yujie
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface Navigation {

    /**
     * Scheme uri segment after host.
     *
     * @return scheme page part.
     */
    String page() default "";

    /**
     * Scheme uri segment after page.
     *
     * @return scheme subpage part.
     */
    String subpage() default "";

    /**
     * Navigation interceptor chains.
     *
     * @return Navigation interceptors.
     */
    Class<? extends Interceptor>[] interceptors() default {};
}
