package com.patrickanker.isay.lib.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermission {

    /**
     * 
     * The value needed
     * 
     */
    String value() default "";
}
