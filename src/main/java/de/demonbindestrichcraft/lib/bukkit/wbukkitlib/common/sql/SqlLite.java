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
public class SqlLite implements SqlInterface {

    private Sql sql;

    public SqlLite(String host, String port, String datenbank, String username, String password) {
        String jdbcDriverSqlLite = "org.sqlite.JDBC";
        String connectionStringSqlLite = "jdbc:sqlite:";
        sql = new Sql(jdbcDriverSqlLite, connectionStringSqlLite + datenbank, username, password);
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
        return getSqlLiteCreateTableSqlQuery(tablename, usePrimaryKey, spalten);
    }

    @Override
    public String getSelectSqlQuery(String tablename, String key) {
        return getSqlLiteSelectSqlQuery(tablename, key);
    }

    @Override
    public String getSelectSqlQuery(String tablename, String key, String bedingung) {
        return getSqlLiteSelectSqlQuery(tablename, key, bedingung);
    }

    @Override
    public String getInsertIntoTableSqlQuery(String tablename, String[] spalten, String[] values) {
        return getSqlLiteInsertIntoTableSqlQuery(tablename, spalten, values);
    }

    @Override
    public String getUpdateSqlQuery(String tablename, String set) {
        return getSqlLiteUpdateSqlQuery(tablename, set);
    }

    @Override
    public String getUpdateSqlQuery(String tablename, String set, String bedingung) {
        return getSqlLiteUpdateSqlQuery(tablename, set, bedingung);
    }

    @Override
    public String getDeleteSqlQuery(String tablename, String bedingung) {
        return getSqlLiteDeleteSqlQuery(tablename, bedingung);
    }
    
    public static String getSqlLiteCreateTableSqlQuery(String tablename, boolean usePrimaryKey, String... spalten) {
       String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tablename + "(";
        String[] split = null;
        for(int i = 0; i < spalten.length; i++) {
            split = spalten[i].split("::");

            if(i == 0) {           
                if(usePrimaryKey)
                {
                    createTableQuery += split[0] + " INTEGER PRIMARY KEY AUTO_INCREMENT";
                    usePrimaryKey = false;
                    continue;
                } else {
                    createTableQuery += split[0] + " " + split[1] + " " + split[2];
                    continue;
                }
            } else {
                if(i + 1 != spalten.length)
                    createTableQuery += ",";
            }

            createTableQuery += split[0] + " " + split[1] + " " + split[2];

            if(i + 1 == spalten.length)
                createTableQuery += ");";
        }

        return createTableQuery;
    }

    public static String getSqlLiteSelectSqlQuery(String tablename, String key) {
        return "SELECT " + key + " FROM " + tablename + ";";
    }

    public static String getSqlLiteSelectSqlQuery(String tablename, String key, String bedingung) {
         return "SELECT " + key + " FROM " + tablename + " WHERE " + bedingung + ";";
    }

    public static String getSqlLiteInsertIntoTableSqlQuery(String tablename, String[] spalten, String[] values) {
        String insertIntoQuery = "INSERT INTO " + tablename + " (";
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

    public static String getSqlLiteUpdateSqlQuery(String tablename, String set) {
        return "UPDATE " + tablename + " SET " + set + ";";
    }

    public static String getSqlLiteUpdateSqlQuery(String tablename, String set, String bedingung) {
        return "UPDATE " + tablename + " SET " + set + " WHERE " + bedingung + ";";
    }

    public static String getSqlLiteDeleteSqlQuery(String tablename, String bedingung) {
        return "DELETE FROM " + tablename + " WHERE " + bedingung + ";";
    }
}
