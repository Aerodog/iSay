package com.patrickanker.isay.core.settings;

import java.sql.Connection;
import java.sql.SQLException;


public interface StorageHandler {
    
    public void load(Connection c, Object instance, String... strings) throws SQLException;
    public void load(Object instance, String... strings);
    
    public void save(Connection c, Object instance, String... strings);
    public void save(Object instance, String... strings);
    
}
