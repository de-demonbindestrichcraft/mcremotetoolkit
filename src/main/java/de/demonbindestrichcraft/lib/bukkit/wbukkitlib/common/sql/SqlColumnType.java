/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql;

import java.util.Locale;

/**
 *
 * @author ABC
 */
public enum SqlColumnType {
    INT,VARCHAR;
    
    public String getType(int count)
    {
        return getType(this.name(), count);
    }
    
    public static String getType(String columnType, int count)
    {
        return columnType.toLowerCase() + "(" + count + ")";
    }
}
