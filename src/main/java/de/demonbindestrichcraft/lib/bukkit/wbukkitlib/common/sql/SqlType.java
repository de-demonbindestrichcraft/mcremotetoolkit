/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql;

/**
 *
 * @author ABC
 */
public enum SqlType {
    SQLLITE,
    MYSQL;
    
    public static SqlType getSqlTypeOutString(String sqlType)
    {
        if(sqlType.equalsIgnoreCase("sqllite"))
            return SqlType.SQLLITE;
        if(sqlType.equalsIgnoreCase("mysql"))
            return SqlType.MYSQL;
        return SqlType.SQLLITE;
    }
}
