package com.patrickanker.isay.core;

import com.patrickanker.isay.ISMain;
import com.patrickanker.isay.core.settings.Setting;
import com.patrickanker.isay.core.settings.StorageHandler;
import com.patrickanker.isay.core.settings.StorageType;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.lang.Validate;

@StorageType(StorageType.Type.SQL)
public class PlayerStorageHandler implements StorageHandler {

    private final File databaseFile;
    
    private Connection connection;
    
    public PlayerStorageHandler(File dbFile)
    {
        this.databaseFile = dbFile;
    }
    
    @Override
    public void load(Connection c, Object instance, String... strings) throws SQLException {
        Validate.notNull(instance, "Player instance cannot be null");
        Validate.notNull(c, "Connection cannot be null for this method");
        
        if (!(instance instanceof ChatPlayer)) {
            ISMain.log("Incorrect player instance passed", 1);
            return;
        }
        
        ChatPlayer cp = (ChatPlayer) instance;
        Statement s = null;
        
        try {
            s = c.createStatement();

            ResultSet rs = s.executeQuery("SELECT * WHERE username='" + cp.getPlayer().getName() + "'");
            boolean empty = true;
            
            while (rs.next()) {
                empty = false;
                
                        
            }
            
            if (empty) {
                
            }
        } finally {
            s.close();
        }
    }

    @Override
    public void load(Object instance, String... strings) 
    {
        ChatPlayer cp = (ChatPlayer) instance;
    }
    
    private void loadField(Field f, Statement s, ChatPlayer cp, String... strings) throws SQLException
    {
        if (!f.isAnnotationPresent(Setting.class))
            return;
        
        String path = ((Setting) f.getAnnotation(Setting.class)).value();
        
        if (strings != null && strings.length != 0) {
            if (path.contains("%s")) {
                path = String.format(path, (Object[]) strings);
            } else {
                ISMain.log("Incorrect formatting for dynamic setting: " + path, 1);
                return;
            }
        }
        
        // Format is table.row.column
        
        String[] split = path.split(".");
        String table = split[0];
        String row = split[1];
        String column = split[2];
        
        f.setAccessible(true);
        
        ResultSet rs = s.executeQuery("SELECT * WHERE username='" + row + "'");
        
    }

    @Override
    public void save(Object instance, String... strings) 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save(Connection c, Object instance, String... strings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
