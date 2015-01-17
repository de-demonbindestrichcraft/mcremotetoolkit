/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql;

import java.util.List;
import java.util.Map;

/**
 *
 * @author ABC
 */
public class MySql implements SqlInterface {
    private Sql sql;
    private String host;
    private String port;
    private String datenbank;
    private String username;
    private String password;
    public MySql(String host, String port, String datenbank, String username, String password)
    {
        String connectionString = "jdbc:mysql://" + host + ":" + port + "/" + datenbank;
        String jdbcDriver = "com.mysql.jdbc.Driver";
        sql = new Sql(jdbcDriver, connectionString, username, password);
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.datenbank = datenbank;
        
    }

    @Override
    public void executeSqlQuery(String sqlQuery, boolean selectQuery) {
        sql.executeSqlQuery(sqlQuery, selectQuery);
    }

    @Override
    public List<String> getResultSql(String... res) {
        return sql.getResultSql(res);
    }
    
    @Override
    public Map<String, List<String>> getResultSqlEx(String... res) {
        return sql.getResultSqlEx(res);
    }
    
    @Override
    public boolean close() {
        return sql.close();
    }

    @Override
    public boolean closeResultSet() {
        return sql.closeResultSet();
    }
    
    @Override
    public boolean isClosed()
    {
        return sql.isClosed();
    }

    @Override
    public String getCreateTableSqlQuery(String tablename, boolean usePrimaryKey, String... spalten) {
        return getMySqlCreateTableSqlQuery(datenbank, tablename, usePrimaryKey, spalten);
    }

    @Override
    public String getSelectSqlQuery(String tablename, String key) {
        return getMySqlSelectSqlQuery(datenbank, tablename, key);
    }

    @Override
    public String getSelectSqlQuery(String tablename, String key, String bedingung) {
        return getMySqlSelectSqlQuery(datenbank, tablename, key, bedingung);
    }

    @Override
    public String getInsertIntoTableSqlQuery(String tablename, String[] spalten, String[] values) {
        return getMySqlInsertIntoTableSqlQuery(datenbank, tablename, spalten, values);
    }

    @Override
    public String getUpdateSqlQuery(String tablename, String set) {
        return getMySqlUpdateSqlQuery(datenbank, tablename, set);
    }

    @Override
    public String getUpdateSqlQuery(String tablename, String set, String bedingung) {
        return getMySqlUpdateSqlQuery(datenbank, tablename, set, bedingung);
    }

    @Override
    public String getDeleteSqlQuery(String tablename, String bedingung) {
        return getMySqlDeleteSqlQuery(datenbank, tablename, bedingung);
    }
    
    public static String getMySqlCreateTableSqlQuery(String datenbank, String tablename, boolean usePrimaryKey, String... spalten) {
       String createTableQuery = "CREATE TABLE IF NOT EXISTS " + datenbank + "." + tablename + "(";
        String[] split = null;
        for(int i = 0; i < spalten.length; i++) {
            split = spalten[i].split("::");

            if(i == 0 && usePrimaryKey) {
                createTableQuery += split[0] + " INTEGER PRIMARY KEY AUTO_INCREMENT";
                usePrimaryKey = false;
            }

            if(i + 1 != spalten.length)
                createTableQuery += ",";

            createTableQuery += "" + split[0] + " " + split[1] + " " + split[2];
            createTableQuery += "" + split[0] + " " + split[1] + " " + split[2];

            if(i + 1 == spalten.length)
                createTableQuery += ");";
        }

        return createTableQuery;
    }

    public static String getMySqlSelectSqlQuery(String datenbank, String tablename, String key) {
         return "SELECT " + key + " FROM " + datenbank + "." + tablename + ";";
    }

    public static String getMySqlSelectSqlQuery(String datenbank, String tablename, String key, String bedingung) {
        return "SELECT " + key + " FROM " + datenbank + "." + tablename + " WHERE " + bedingung + ";";
    }

    public static String getMySqlInsertIntoTableSqlQuery(String datenbank, String tablename, String[] spalten, String[] values) {
        String insertIntoQuery = "INSERT INTO " + datenbank + "." + tablename + " (";
        for(int i = 0; i < spalten.length; i++) {

            insertIntoQuery += "" + spalten[i] + "";

            if(i + 1 != spalten.length)
                insertIntoQuery += ",";

            if(i + 1 == spalten.length)
                insertIntoQuery += ") VALUES (";
        }

        for(int i = 0; i < values.length; i++) {

            insertIntoQuery += "'" + values[i] + "'";

            if(i + 1 != values.length)
                insertIntoQuery += ",";

            if(i + 1 == values.length)
                insertIntoQuery += ");";
        }

        return insertIntoQuery;
    }

    public static String getMySqlUpdateSqlQuery(String datenbank, String tablename, String set) {
        return "UPDATE " + datenbank + "." + tablename + " SET " + set + ";";
    }

    public static String getMySqlUpdateSqlQuery(String datenbank, String tablename, String set, String bedingung) {
        return "UPDATE " + datenbank + "." + tablename + " SET " + set + " WHERE " + bedingung + ";";
    }

    public static String getMySqlDeleteSqlQuery(String datenbank, String tablename, String bedingung) {
        return "DELETE FROM " + datenbank + "." + tablename + " WHERE " + bedingung + ";";
    }
}
