/*
 * CommandPermission.java
 *
 * Project: libpsanker
 *
 * Copyright (C) Patrick Anker 2011 - 2013. All rights reserved.
 * 
 * libpsanker by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

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
