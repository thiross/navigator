package com.tutuur.navigator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD})
public @interface BundleExtra {
    String value() default "[undefined]";

    String key() default "[undefined]";

    boolean autowired() default false;
}
