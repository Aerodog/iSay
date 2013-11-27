package com.patrickanker.isay.core.settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StorageType {
    
    public Type value() default Type.YAML;
    
    public enum Type {
        YAML,
        SQL
    }
}