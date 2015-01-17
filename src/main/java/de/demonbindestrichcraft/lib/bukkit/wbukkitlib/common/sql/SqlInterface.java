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
public interface SqlInterface {

    public void executeSqlQuery(String sqlQuery, boolean selectQuery);

    public List<String> getResultSql(String... res);
    
    public Map<String, List<String>> getResultSqlEx(String... res);
    
    public String getCreateTableSqlQuery(String tablename, boolean usePrimaryKey, String... spalten);

    public String getSelectSqlQuery(String tablename, String key);
    
    public String getSelectSqlQuery(String tablename, String key, String bedingung);
    
    public String getInsertIntoTableSqlQuery(String tablename, String[] spalten, String[] values);
    
    public String getUpdateSqlQuery(String tablename, String set);
    
    public String getUpdateSqlQuery(String tablename, String set, String bedingung);
    
     public String getDeleteSqlQuery(String tablename, String bedingung);
    
    public boolean close();
    
    public boolean isClosed();
    
    public boolean closeResultSet();
}
