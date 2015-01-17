/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql;

/**
 *
 * @author ABC
 */
public class SqlWrapper {
    public static SqlInterface getSql(String host, String port, String datenbank, String username, String password, SqlType sqlType)
    {
        switch(sqlType)
        {
            case SQLLITE:
            {
                return new SqlLite(host, port, datenbank, username, password);
            }
                
            case MYSQL:
            {
                return new MySql(host, port, datenbank, username, password);
            }
                
            default: return null;
        }
    }
}
