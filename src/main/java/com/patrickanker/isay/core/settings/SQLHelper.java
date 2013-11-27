package com.patrickanker.isay.core.settings;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.Validate;

public class SQLHelper {
    
    private static String listDelimit = "%%%";
    
    protected static Connection getNewConnection(File dbFile)
    {
        Connection c;
        
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            return c;
        } catch (Exception e) {
            return null;
        }
    }
    
    protected static void createTable(Statement s, String table, String columns) throws SQLException
    {
        Validate.notNull(s, "Statement cannot be null");
        Validate.notNull(table, "Table name cannot be null");
        Validate.notNull(columns, "Table columns cannot be null");
        
        s.executeUpdate("CREATE TABLE " + table + " " + columns + ";");
    }
    
    protected static void dropTable(Statement s, String table, String column) throws SQLException
    {
        Validate.notNull(s, "Statement cannot be null");
        Validate.notNull(table, "Table name cannot be null");
        Validate.notNull(column, "Table column cannot be null");
        
        s.executeUpdate("ALTER TABLE " + table + " DROP " + column + ";");
    }
    
    protected static void addColumn(Statement s, String table, String column, String qualities) throws SQLException
    {
        Validate.notNull(s, "Statement cannot be null");
        Validate.notNull(table, "Table name cannot be null");
        Validate.notNull(column, "Column name cannot be null");
        Validate.notNull(qualities, "Column qualities cannot be null");
        
        s.executeUpdate("ALTER TABLE " + table + " ADD " + column + " " + qualities + ";");
    }
    
    protected static void removeColumn(Statement s, String table, String column) throws SQLException
    {
        Validate.notNull(s, "Statement cannot be null");
        Validate.notNull(table, "Table name cannot be null");
        Validate.notNull(column, "Column name cannot be null");
        
        s.executeUpdate("ALTER TABLE " + table + " DROP " + column + ";");
    }
    
    protected static String concatListToString(List<String> list)
    {
        Validate.notNull(list);
        
        String concat = "";
        
        for (String str : list) {
            concat += str + listDelimit;
        }
        
        concat = concat.substring(0, concat.length() - (listDelimit.length() + 1));
        return concat;
    }
    
    protected static List<String> splitStringToList(String str)
    {
        Validate.notNull(str);
        
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(str.split(listDelimit)));
        
        return list;
    }
}
