/*
 * SQLDriver.java
 *
 * Project: libpsanker
 *
 * Copyright (C) Patrick Anker 2011 - 2012. All rights reserved.
 * 
 * libpsanker by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */
package com.patrickanker.isay.lib.persist;

import com.patrickanker.isay.lib.logging.ConsoleLogger;
import java.sql.*;

public abstract class SQLDriver {
    
    protected String NULL_STRING = "null";
    protected String LIST_DIVIDER = "-";
    
    protected Connection connection;
    protected Statement statement;

    public boolean openDriver(String className)
    {
        try {
            Class.forName(className);

            return true;
        } catch (ClassNotFoundException ex) {
            // Java does not support MySQL/SQLite on this server

            return false;
        }
    }

    protected boolean checkConnection()
    {
        return (this.connection != null);
    }
    
    public Statement getStatement() throws SQLException
    {
        if (!checkConnection())
            getConnection();
        
        this.statement = this.connection.createStatement();
        return this.statement;
    }
    
    public void releaseConnection() throws SQLException
    {
        if (!checkConnection())
            return;
        
        this.connection.close();
        this.connection = null;
    }
    
    public void releaseStatement() throws SQLException
    {
        if (this.statement == null)
            return;
        
        this.statement.close();
        this.statement = null;
    }
    
    public void release() throws SQLException
    {   
        releaseConnection();
        releaseStatement();
    } 

    public abstract Connection getConnection() throws SQLException;
    
    public boolean createTable(String query)
    {
        boolean ret = false;
        
        try {
            Statement state;
            ResultSet result;
            
            try {
                getConnection();
                state = getStatement();
                state.execute(query);
                ret = true;
            } catch (SQLException ex) {
                ConsoleLogger.getLogger("libpsanker").log("SQLException caught in SQLDriver.createTable(): " + ex.getMessage());
            } finally {
                release();
            }
        } catch (SQLException ex) {
            ConsoleLogger.getLogger("libpsanker").log("SQLException caught in SQLDriver.createTable(): " + ex.getMessage());
        }
        
        return ret;
    }
    
    public boolean checkTable(String table)
    {
        boolean ret = false;
        
        try {
            Connection connect;
            DatabaseMetaData data;
            ResultSet result;
            
            try {
                connect = getConnection();
                data = connect.getMetaData();
                result = data.getTables(null, null, table, null);
                
                if (result.next())
                    ret = true;
                
            } catch (SQLException ex) {
                ConsoleLogger.getLogger("libpsanker").log("SQLException caught in SQLDriver.checkTable(): " + ex.getMessage());
            } finally {
                release();
            }
        } catch (SQLException ex) {
            ConsoleLogger.getLogger("libpsanker").log("SQLException caught in SQLDriver.ccheckTable(): " + ex.getMessage());
        }
        
        return ret;
    }
    
    public boolean clearTable(String table)
    {
        boolean ret = false;
        
        try {
            Statement state;
            ResultSet result;
            
            try {
                getConnection();
                state = getStatement();
                state.execute("DELETE FROM " + table);
                ret = true;
            } catch (SQLException ex) {
                ConsoleLogger.getLogger("libpsanker").log("SQLException caught in SQLDriver.clearTable(): " + ex.getMessage());
            } finally {
                release();
            }
        } catch (SQLException ex) {
            ConsoleLogger.getLogger("libpsanker").log("SQLException caught in SQLDriver.clearTable(): " + ex.getMessage());
        }
        
        return ret;
    }
    
    public String getNullString()
    {
        return NULL_STRING;
    }
    
    public String getListDivider()
    {
        return LIST_DIVIDER;
    }
}
