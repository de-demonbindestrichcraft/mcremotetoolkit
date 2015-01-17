/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files;

import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql.SqlInterface;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql.SqlLite;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public final class Config {

    private Map<String, String> properties;
    private File propertiesFile;
    private Map<String, String> copiedProperties;
    
     public Config() {
        properties = new HashMap<String, String>();
        propertiesFile = new File("config.werri.txt");
    }

    public Config(File file) {
        this();
        init(file);
    }

    public Config(File file, Map<String, String> properties) {
        this(file);
        update(properties);
    }
    
    public void init(File file)
    {
        if(!(file instanceof File))
            file = this.propertiesFile;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        propertiesFile = file;
    }
    
    public void updateIncorrectValueMap(Map properties) {
        Map<String,String> temp = new HashMap<String, String>();
        Set keys = new HashSet();
        keys.addAll(properties.keySet());
        for(Object key : keys)
        {
            temp.put(key.toString(), properties.get(key).toString());
        }
        update(temp);
    }
       
    public void update(Map<String, String> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public Map<String, String> getCopyOfProperties() {
        return new HashMap<String, String>(properties);
    }
    
      public Map<String, String> getCopiedOfProperties() {
        if(copiedProperties==null)
            copiedProperties =  new HashMap<String, String>(properties);
        return copiedProperties;
    }

    public String getString(String key) throws NullPointerException, ClassCastException {
        return properties.get(key);
    }

    public int getInt(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Integer.parseInt(properties.get(key));
    }

    public float getFloat(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Float.parseFloat(properties.get(key));
    }

    public double getDouble(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Double.parseDouble(properties.get(key));
    }

    public long getLong(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Long.parseLong(properties.get(key));
    }

    public short getShort(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Short.parseShort(properties.get(key));
    }

    public boolean getBoolean(String key) throws NumberFormatException, NullPointerException, ClassCastException {
        return Boolean.parseBoolean(properties.get(key));
    }
    
    public void saveToDb(SqlInterface sqlInterface, String table_name, String spalte1, String spalte2)
    {
        String[] spalten = new String[2];
        String[] values = new String[2];
        spalten[0] = spalte1+"::VARCHAR(255)::NOT NULL DEFAULT ''";
        spalten[1] = spalte2+"::VARCHAR(255)::NOT NULL DEFAULT ''";
        sqlInterface.executeSqlQuery(sqlInterface.getCreateTableSqlQuery(table_name, false, spalten), false);
        spalten[0] = spalte1;
        spalten[1] = spalte2;
        Map<String,String> copyOfProperties=this.getCopyOfProperties();
        Set<String> keySet = new HashSet<String>(copyOfProperties.keySet());
        Iterator<String> iterator = keySet.iterator();
        String insertIntoTableSqlQuery = "";
        while(iterator.hasNext())
        {
            values[0]=iterator.next();
            values[1]=copyOfProperties.get(values[0]);
            insertIntoTableSqlQuery = sqlInterface.getInsertIntoTableSqlQuery(table_name, spalten, values);
            sqlInterface.executeSqlQuery(insertIntoTableSqlQuery, false);
        }
    }
    
    public void loadFromDb(SqlInterface sqlInterface, String table_name, String spalte1, String spalte2)
    {
        String spalte = spalte1+","+spalte2;
        String selectSqlQuery = sqlInterface.getSelectSqlQuery(table_name, spalte);
        sqlInterface.executeSqlQuery(selectSqlQuery, true);
        Map<String, List<String>> resultSqlEx = sqlInterface.getResultSqlEx(spalte1,spalte2);
        List<String> spalte1List = resultSqlEx.get(spalte1);
        List<String> spalte2List = resultSqlEx.get(spalte2);
        Map<String,String> myProperties=new HashMap<String,String>();
        int length = spalte1List.size();
        if(length!=spalte2List.size())
        {
            return;
        }
        
        for(int i = 0; i < length; i++)
        {
            myProperties.put(spalte1List.get(i), spalte2List.get(i));
        }
        update(myProperties);
    }
    
    public SqlInterface getNewSqlInterface(String name)
    {
        if((!(name instanceof String)) || name.isEmpty())
        {
            name = propertiesFile.getName();
            if(name.isEmpty())
            {
                name = "config.werri.txt";
            }
        }
        return new SqlLite("localhost", "3306",name, "login", "passwort");
    }
    
    public SqlInterface getNewSqlInterface()
    {
        return getNewSqlInterface(null);
    }

    public void save(String splitchar) {
        GenerallyFileManager.FileWrite(properties, propertiesFile, splitchar);
    }
    
    public void load(File file, String splitchar) {
        init(file);
        Map<String,String> content = GenerallyFileManager.AllgemeinFileReader(file, splitchar);
        if(content == null)
            return;
        update(content);
    }
}
