/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql;

/**
 *
 * @author ABC
 */
public enum SqlColumnValue {

    DEFAULT_NULL;

    public String getSqlColumnValue() {
        return getSqlColumnValue(this.name());
    }

    public static String getSqlColumnValue(String columnValue) {
        if(columnValue == null)
            return "";
        if(columnValue.isEmpty())
            return "";
        String newColumnValue = columnValue.toLowerCase();
        if (newColumnValue.contains(" ")) {
            return newColumnValue;
        }

        if (newColumnValue.contains("_")) {
            String[] split = newColumnValue.split("_");
            newColumnValue = split[0] + " " + split[1];
            return newColumnValue;
        }
        return newColumnValue;
    }
}
