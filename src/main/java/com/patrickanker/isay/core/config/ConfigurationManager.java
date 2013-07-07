/*
 * ConfigurationManager.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2013. All rights reserved.
 * 
 * iSay by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay.core.config;

import com.patrickanker.isay.ISMain;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;


public class ConfigurationManager {
    
    private static final File configFile = new File("plugins/iSay/config.yml");
    private YamlConfiguration yamlConfiguration;
    
    public ConfigurationManager()
    {
        try {
            yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.load(configFile);
        } catch (Throwable t) {
            ISMain.log("Could not load config file", 2);
        }
    }
    
    public void load(Class<? extends StorageConfiguration> clazz)
    {
        if (yamlConfiguration == null) {
            ISMain.log("Config file inaccessible; Verify file state", 2);
            return;
        }
        
        if (!clazz.isAnnotationPresent(StorageConfigurationPath.class)) {
            ISMain.log("Attempted to register configuration settings for an unknown path", 1);
        }
        
        try {
            StorageConfigurationPath _storageConfigPath = clazz.getAnnotation(StorageConfigurationPath.class);
            StorageConfiguration _storage = clazz.newInstance();

            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(Setting.class)) {
                    // Configuration loading so far only supports String Lists, Strings, and booleans
                    Setting _setting = f.getAnnotation(Setting.class);

                    if (f.getDeclaringClass().isAssignableFrom(List.class)) {
                        if (_storageConfigPath.value().equals("")) {
                            List<String> _l = yamlConfiguration.getStringList(_setting.value());
                            
                            if (_l != null) {
                                f.set(_storage, _l);
                            }
                        } else {
                            List<String> _l = yamlConfiguration.getStringList(_storageConfigPath + "." + _setting.value());
                            
                            if (_l != null) {
                                f.set(_storage, _l);
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            ISMain.log("Could not load config for class: " + clazz.getSimpleName(), 2);
        }
    }
}
