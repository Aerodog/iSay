package com.patrickanker.isay.lib.logging;

import java.util.HashMap;

public abstract class ConsoleLogger {
    
    private static final HashMap<String, ConsoleLogger> multitonMap = new HashMap<String, ConsoleLogger>();
    
    public static ConsoleLogger getLogger(String pluginName)
    {
        if (!multitonMap.containsKey(pluginName)) {
            ConsoleLogger logger = new BukkitConsoleLogger(pluginName);
            multitonMap.put(pluginName, logger);
            return logger;
        } else {
            return multitonMap.get(pluginName);
        }
    }
    
    public abstract void log(String str);
    public abstract void log(String str, final int importance);
    public abstract void log(String str, String module, final int importance);
}
