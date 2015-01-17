// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LegacySupport.java

package com.drdanick.McRKit.plugin;

import java.lang.reflect.Method;
import jline.TerminalSupport;
import jline.UnsupportedTerminal;
import jline.console.ConsoleReader;
import org.bukkit.plugin.java.JavaPlugin;

public class LegacySupport
{

    public LegacySupport()
    {
    }

    public static void initMetrics(JavaPlugin javaplugin)
        throws Throwable
    {
        org.bukkit.Server server = javaplugin.getServer();
        Method method = server.getClass().getMethod("getReader", new Class[0]);
        Object obj = method.invoke(server, new Object[0]);
        if(obj instanceof ConsoleReader)
        {
            jline.Terminal terminal = ((ConsoleReader)obj).getTerminal();
            if(terminal instanceof UnsupportedTerminal)
            {
                Method method1 = TerminalSupport.class.getDeclaredMethod("setAnsiSupported", new Class[] {
                    Boolean.TYPE
                });
                method1.setAccessible(true);
                method1.invoke(terminal, new Object[] {
                    Boolean.valueOf(true)
                });
            }
        }
    }
}
