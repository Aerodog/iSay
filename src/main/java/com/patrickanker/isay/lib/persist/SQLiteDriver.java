/*
 * SQLiteDriver.java
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
import java.io.File;
import java.io.IOException;
import java.sql.*;


public class SQLiteDriver extends SQLDriver {
    
    private String database;
    private File sqlFile;
    
    public SQLiteDriver(String db, String directory)
    {
        this.database = db;
        
        database = database.replace("/", "");
        database = database.replace("\\", "");
        database = database.replace(".db", "");
        
        File dir = new File(directory);
        
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        
        sqlFile = new File(directory + "/" + database + ".db");
        
        if (!sqlFile.exists()) {
            try {
                sqlFile.createNewFile();
            } catch (IOException ex) {
                ConsoleLogger.getLogger("libpsanker").log("Could not create SQLite file \"" + database + "\"", 2);
            }
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException
    {
        if (openDriver("org.sqlite.JDBC")) {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + sqlFile.getAbsolutePath());
            return this.connection;
        }
        
        throw new SQLException("JDBC Driver for SQLit is not installed on this system.");
    }
}
