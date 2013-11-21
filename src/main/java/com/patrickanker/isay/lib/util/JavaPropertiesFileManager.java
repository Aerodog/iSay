package com.patrickanker.isay.lib.util;


import com.patrickanker.isay.lib.logging.ConsoleLogger;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JavaPropertiesFileManager {
    
    public static String BASE = "plugins";

    public JavaPropertiesFileManager(String dir) {
        BASE = dir;
    }
    
    public static void setBaseDirectory(File dir)
    {
        setBaseDirectory(dir.getAbsolutePath());
    }
    
    public static void setBaseDirectory(String dir)
    {
        BASE = dir;
    }

    public static Map<String, Object> load(String target) {
        return load(target, "");
    }

    public static Map<String, Object> load(String target, String destination) {
        // For example, when destination is "/channels",
        // the target BASE will be "${BASE}/channels/"

        Map<String, Object> map = new HashMap<String, Object>();
        map.clear();

        File f = new File(BASE + destination + "/" + target + ".properties");
        FileInputStream fi = null;

        if (f.exists()) {
            try {
                Properties props = new Properties();
                fi = new FileInputStream(f);

                props.load(fi);

                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    String key = entry.getKey().toString();

                    try {
                        if (entry.getValue().toString().contains(".") || (Double.parseDouble(entry.getValue().toString()) > 2147483647 || Double.parseDouble(entry.getValue().toString()) < -2147483648)) {
                            Double d = Double.parseDouble(entry.getValue().toString());
                            map.put(key, d);
                            continue;
                        }
                        
                        Integer i = Integer.parseInt(entry.getValue().toString());
                        map.put(key, i);
                    } catch (NumberFormatException ex) {
                        if (entry.getValue().toString().equals(Boolean.TRUE.toString()) || entry.getValue().toString().equals(Boolean.FALSE.toString())) {
                            Boolean bool = Boolean.parseBoolean(entry.getValue().toString());
                            map.put(key, bool);
                            continue;
                        }

                        map.put(key, entry.getValue().toString());
                        continue;
                    }
                }
            } catch (FileNotFoundException ex) {
                ConsoleLogger.getLogger("libpsanker").log("File not found: " + f.getAbsolutePath(), 2);
            } catch (IOException ex) {
                ConsoleLogger.getLogger("libpsanker").log("Incorrectly loaded properties from " + f.getAbsolutePath(), 2);
            } finally {
                try {
                    if (fi != null) {
                        fi.close();
                    }
                } catch (IOException ex) {
                    ConsoleLogger.getLogger("libpsanker").log("##### -- FATAL ERROR -- ##### Failed to store data to " + f.getAbsolutePath(), 2);
                }
            }
        }

        return map;
    }

    public static void save(String target, Map<String, Object> data) {
        save(target, data, "");
    }

    public static void save(String target, Map<String, Object> data, String destination) {
        // Note: Destination is appended to plugins/ISMain/data
        // For example, when destination is "/channels",
        // the target BASE will be "plugins/ISMain/data/channels/"

        File f = new File(BASE + destination + "/" + target + ".properties");
        FileOutputStream fo = null;

        try {
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }

            Properties props = new Properties();
            fo = new FileOutputStream(f);

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();

                props.setProperty(key, entry.getValue().toString());
            }

            props.store(fo, null);
        } catch (IOException ex) {
            ConsoleLogger.getLogger("libpsanker").log("Could not create file " + f.getAbsolutePath(), 2);
        } finally {
            try {
                if (fo != null) {
                    fo.close();
                }
            } catch (IOException ex) {
                ConsoleLogger.getLogger("libpsanker").log("##### -- FATAL ERROR -- ##### Failed to store data to " + f.getAbsolutePath(), 2);
            }
        }
    }
}
