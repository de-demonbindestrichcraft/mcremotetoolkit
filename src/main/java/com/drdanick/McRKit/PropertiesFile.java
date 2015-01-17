// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PropertiesFile.java

package com.drdanick.McRKit;

import java.io.*;
import java.util.Properties;

public final class PropertiesFile
{

    public PropertiesFile(Properties properties1)
    {
        properties = properties1;
        fileName = null;
    }

    public void load()
    {
        try
        {
            properties.load(new FileInputStream(fileName));
        }
        catch(IOException ioexception)
        {
            System.err.println((new StringBuilder()).append("Unnable to load ").append(fileName).append(": ").append(ioexception.toString()).toString());
        }
    }

    public void save()
    {
        try
        {
            properties.store(new FileOutputStream(fileName), "Minecraft Remote Toolkit Properties File");
        }
        catch(IOException ioexception)
        {
            System.err.println((new StringBuilder()).append("Unnable to save ").append(fileName).append(": ").append(ioexception.toString()).toString());
        }
    }

    public boolean keyExists(String s)
    {
        return properties.containsKey(s);
    }

    public String getString(String s)
    {
        return properties.getProperty(s);
    }

    public String getString(String s, String s1)
    {
        if(properties.containsKey(s))
        {
            return properties.getProperty(s);
        } else
        {
            setString(s, s1);
            return s1;
        }
    }

    public void setString(String s, String s1)
    {
        properties.setProperty(s, s1);
    }

    public int getInt(String s)
    {
        return Integer.parseInt(properties.getProperty(s));
    }

    public int getInt(String s, int i)
    {
        if(properties.containsKey(s))
        {
            return Integer.parseInt(properties.getProperty(s));
        } else
        {
            setInt(s, i);
            return i;
        }
    }

    public void setInt(String s, int i)
    {
        properties.setProperty(s, String.valueOf(i));
    }

    public long getLong(String s)
    {
        return Long.parseLong(properties.getProperty(s));
    }

    public long getLong(String s, long l)
    {
        if(properties.containsKey(s))
        {
            return Long.parseLong(properties.getProperty(s));
        } else
        {
            setLong(s, l);
            return l;
        }
    }

    public void setLong(String s, long l)
    {
        properties.setProperty(s, String.valueOf(l));
    }

    public boolean getBoolean(String s)
    {
        return Boolean.parseBoolean(properties.getProperty(s));
    }

    public boolean getBoolean(String s, boolean flag)
    {
        if(properties.containsKey(s))
        {
            return Boolean.parseBoolean(properties.getProperty(s));
        } else
        {
            setBoolean(s, flag);
            return flag;
        }
    }

    public void setBoolean(String s, boolean flag)
    {
        properties.setProperty(s, String.valueOf(flag));
    }

    public Properties getProperties()
    {
        return properties;
    }

    private Properties properties;
    private String fileName;
}
