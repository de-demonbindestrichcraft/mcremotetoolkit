/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql;

/**
 *
 * @author ABC
 */
public class SqlColumn {
    private String columnName;
    private String columnType;
    private int columnTypeCount;
    private String columnValue;
    
    public SqlColumn(String columnName, String columnType, int columnTypeCount, String columnValue)
    {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnTypeCount = columnTypeCount;
        this.columnValue = columnValue;
    }
    
    @Override
    public String toString()
    {
        return columnName + "::" + SqlColumnType.getType(columnType, columnTypeCount) + "::" + SqlColumnValue.getSqlColumnValue(columnValue);
    }
    
    public static String getSqlColumnString(String columnName, String columnType, int columnTypeCount, String columnValue)
    {
        return new SqlColumn(columnName, columnType, columnTypeCount, columnValue).toString();
    }
}
