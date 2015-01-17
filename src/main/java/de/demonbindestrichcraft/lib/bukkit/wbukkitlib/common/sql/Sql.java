package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public class Sql implements SqlInterface {

    private Statement sqlStatement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet sqlResultSet = null;
    private Connection sqlConnect = null;
    private boolean isDbFile = false;

    public Sql(String classDriver, String connectionString, String user, String password) {
        try {
            Class.forName(classDriver);
            sqlConnect = DriverManager.getConnection(connectionString, user, password);
            if (sqlConnect == null) {
                return;
            }
            if (classDriver.contains("sqlite")) {
                isDbFile = true;
            }
            sqlStatement = sqlConnect.createStatement();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException sqlex) {
            Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, sqlex);
        }
    }

    @Override
    public void executeSqlQuery(String sqlQuery, boolean selectQuery) {
        if (sqlStatement == null) {
            return;
        }

        if (isDbFile && sqlQuery.contains("AUTO_INCREMENT")) {
            sqlQuery = sqlQuery.replaceAll("AUTO_INCREMENT", "AUTOINCREMENT");
        }

        try {
            if (selectQuery) {
                sqlResultSet = sqlStatement.executeQuery(sqlQuery);
            } else {
                sqlStatement.executeUpdate(sqlQuery);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<String> getResultSql(String... res) {
        List<String> resultSql = new LinkedList<String>();

        if (sqlResultSet == null) {
            return null;
        }

        if (res == null) {
            return null;
        }
        try {
            while (sqlResultSet.next()) {
                for (String result : res) {
                    resultSql.add(sqlResultSet.getString(result));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return resultSql;
    }
    
    @Override
    public Map<String, List<String>> getResultSqlEx(String... res) {
        Map<String, List<String>> resultSql = new HashMap<String, List<String>>();

        if (sqlResultSet == null) {
            return null;
        }

        if (res == null) {
            return null;
        }
        try {
            while (sqlResultSet.next()) {
                for (String result : res) {
                  if(!resultSql.containsKey(result))
                  {
                      resultSql.put(result, new LinkedList<String>());
                  }
                    resultSql.get(result).add(sqlResultSet.getString(result));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return resultSql;
    }

    @Override
    public boolean close() {
        try {
            if (sqlStatement != null) {
                sqlStatement.close();
            }
            if (sqlConnect != null) {
                sqlConnect.close();
            }
            if (isClosed()) {
                return true;
            }
            sqlStatement = null;
            sqlConnect = null;
        } catch (SQLException ex) {
            return false;
        }
        return false;
    }

    @Override
    public boolean closeResultSet() {
        try {
            if (sqlResultSet != null) {
                sqlResultSet.close();
            }
        } catch (SQLException ex) {
            return false;
        }
        return false;
    }

    public void setPreparedStatement(String preparedStatement) {
        try {
            this.preparedStatement = sqlConnect.prepareStatement(preparedStatement);
        } catch (SQLException ex) {
            Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    @Override
    public boolean isClosed() {
        boolean closeStmt = true;
        boolean closeConnect = true;
        try {
            if (!isDbFile) {
                closeStmt = sqlStatement.isClosed();
            }
            closeConnect = sqlConnect.isClosed();
            if (closeStmt && closeConnect) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            if (!isDbFile) {
                Logger.getLogger(Sql.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public String getCreateTableSqlQuery(String tablename, boolean usePrimaryKey, String... spalten) {
        return getSqlCreateTableSqlQuery(tablename, usePrimaryKey, spalten);
    }

    @Override
    public String getSelectSqlQuery(String tablename, String key) {
        return getSqlSelectSqlQuery(tablename, key);
    }

    @Override
    public String getSelectSqlQuery(String tablename, String key, String bedingung) {
        return getSqlSelectSqlQuery(tablename, key, bedingung);
    }

    @Override
    public String getInsertIntoTableSqlQuery(String tablename, String[] spalten, String[] values) {
        return getSqlInsertIntoTableSqlQuery(tablename, spalten, values);
    }

    @Override
    public String getUpdateSqlQuery(String tablename, String set) {
        return getSqlUpdateSqlQuery(tablename, set);
    }

    @Override
    public String getUpdateSqlQuery(String tablename, String set, String bedingung) {
        return getSqlUpdateSqlQuery(tablename, set, bedingung);
    }

    @Override
    public String getDeleteSqlQuery(String tablename, String bedingung) {
        return getSqlDeleteSqlQuery(tablename, bedingung);
    }

    public static String getSqlCreateTableSqlQuery(String tablename, boolean usePrimaryKey, String... spalten) {
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

    public static String getSqlSelectSqlQuery(String tablename, String key) {
        return "SELECT " + key + " FROM " + tablename + ";";
    }

    public static String getSqlSelectSqlQuery(String tablename, String key, String bedingung) {
        return "SELECT " + key + " FROM " + tablename + " WHERE " + bedingung + ";";
    }

    public static String getSqlInsertIntoTableSqlQuery(String tablename, String[] spalten, String[] values) {
        String insertIntoQuery = "INSERT INTO " + tablename + " (";
        for (int i = 0; i < spalten.length; i++) {

            insertIntoQuery += "" + spalten[i] + "";

            if (i + 1 != spalten.length) {
                insertIntoQuery += ",";
            }

            if (i + 1 == spalten.length) {
                insertIntoQuery += ") VALUES (";
            }
        }

        for (int i = 0; i < values.length; i++) {

            insertIntoQuery += "'" + values[i] + "'";

            if (i + 1 != values.length) {
                insertIntoQuery += ",";
            }

            if (i + 1 == values.length) {
                insertIntoQuery += ");";
            }
        }

        return insertIntoQuery;
    }

    public static String getSqlUpdateSqlQuery(String tablename, String set) {
        return "UPDATE " + tablename + " SET " + set + ";";
    }

    public static String getSqlUpdateSqlQuery(String tablename, String set, String bedingung) {
        return "UPDATE " + tablename + " SET " + set + " WHERE " + bedingung + ";";
    }

    public static String getSqlDeleteSqlQuery(String tablename, String bedingung) {
        return "DELETE FROM " + tablename + " WHERE " + bedingung + ";";
    }
}
