/*
 * MySQLDriver.java
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

import java.sql.*;


public class MySQLDriver extends SQLDriver {

    private final String username;
    private final String password;
    private final String hostname;
    private final String port;
    private final String database;
    
    public MySQLDriver(String username, String password, String host, String port, String database)
    {
        this.username = username;
        this.password = password;
        this.hostname = host;
        this.port = port;
        this.database = database;
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        if (openDriver("com.mysql.jdbc.Driver")) {
            String url = "jbdc:mysql://" + hostname + ":" + port + "/" + database;
            this.connection = DriverManager.getConnection(url, username, password);
            return connection;
        }
        
        throw new SQLException("JDBC Driver for MySQL is not installed on this system.");
    }
}
