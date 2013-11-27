package com.patrickanker.isay.core.settings;

import com.patrickanker.isay.ISMain;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.Validate;

public final class SettingsManager {

    private final File dataFolder;
    private HashMap<Class<? extends StorageHandler>, StorageHandler> storageHandlers = new HashMap<Class<? extends StorageHandler>, StorageHandler>();
    
    public SettingsManager(File data)
    {
        this.dataFolder = data;
    }
    
    public void registerHandler(final StorageHandler handler)
    {
        Validate.notNull(handler, "StorageHandler cannot be null");
        
        if (!handler.getClass().isAnnotationPresent(StorageType.class)) {
            ISMain.log("Attempted to register improperly formatted StorageHandler: " + handler.getClass().getCanonicalName(), 1);
            return;
        }
        
        if (storageHandlers.containsKey(handler.getClass())) {
            ISMain.log("Attempted to register duplicate StorageHandler: " + handler.getClass().getCanonicalName(), 1);
            return;
        }
        
        storageHandlers.put(handler.getClass(), handler);
    }
    
    public void loadSettings(final Object obj, final String... strings)
    {
        Validate.notNull(obj, "Object to load settings must not be null");
        
        if (!obj.getClass().isAnnotationPresent(StorageHandlerRequest.class)) {
            ISMain.log("Object improperly structured: " + obj.getClass().getCanonicalName(), 1);
            return;
        }
        
        StorageHandlerRequest request = (StorageHandlerRequest) obj.getClass().getAnnotation(StorageHandlerRequest.class);
        
        if (!storageHandlers.containsKey(request.value())) {
            ISMain.log("Storage handler not found for type: " + request.value().getCanonicalName(), 1);
            return;
        }
        
        StorageHandler handler = storageHandlers.get(request.value());
        
        StorageType type = (StorageType) handler.getClass().getAnnotation(StorageType.class);
        
        if (type.value() == StorageType.Type.SQL) {
            // Create SQL session for SQL storage
            
            Connection c = SQLHelper.getNewConnection(new File(dataFolder.getAbsolutePath() + "/persist.db"));
            
            if (c != null) {
                try {
                    handler.load(c, obj, strings);
                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    try {
                        c.close();
                    } catch (SQLException ex) {
                        ISMain.log("Could not close SQLite connection with file: " + dataFolder.getAbsolutePath() + "/persist.db", 2);
                    }
                }
            } else {
                ISMain.log("Could not establish SQLite connection with file: " + dataFolder.getAbsolutePath() + "/persist.db", 2);
            }
        } else {
            handler.load(obj, strings);
        }
    }
    
    public void loadSettings(final HashMap<Object, String[]> map)
    {
        for (Map.Entry<Object, String[]> entry : map.entrySet()) {
            loadSettings(entry.getKey(), entry.getValue());
        }
    }

    public void saveSettings(final Object obj, final String... strings)
    {
        Validate.notNull(obj, "Object to save settings must not be null");
        
        if (!obj.getClass().isAnnotationPresent(StorageHandlerRequest.class)) {
            ISMain.log("Object improperly structured: " + obj.getClass().getCanonicalName(), 1);
            return;
        }
        
        StorageHandlerRequest request = (StorageHandlerRequest) obj.getClass().getAnnotation(StorageHandlerRequest.class);
        
        if (!storageHandlers.containsKey(request.value())) {
            ISMain.log("Storage handler not found for type: " + request.value().getCanonicalName(), 1);
            return;
        }
        
        StorageHandler handler = storageHandlers.get(request.value());
        
        StorageType type = (StorageType) handler.getClass().getAnnotation(StorageType.class);
        
        if (type.value() == StorageType.Type.SQL) {
            // Create SQL session for SQL storage
            
            Connection c = SQLHelper.getNewConnection(new File(dataFolder.getAbsolutePath() + "/persist.db"));
            
            if (c != null) {
                try {
                    handler.save(c, obj, strings);
                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    try {
                        c.close();
                    } catch (SQLException ex) {
                        ISMain.log("Could not close SQLite connection with file: " + dataFolder.getAbsolutePath() + "/persist.db", 2);
                    }
                }
            } else {
                ISMain.log("Could not establish SQLite connection with file: " + dataFolder.getAbsolutePath() + "/persist.db", 2);
            }
        } else {
            handler.save(obj, strings);
        }
    }
    
    public void saveSettings(final HashMap<Object, String[]> map)
    {
        for (Map.Entry<Object, String[]> entry : map.entrySet()) {
            loadSettings(entry.getKey(), entry.getValue());
        }
    }
}
