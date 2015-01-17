// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ToolkitConsoleHandler.java

package com.drdanick.McRKit;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import jline.console.ConsoleReader;
import jline.console.CursorBuffer;

public class ToolkitConsoleHandler extends ConsoleHandler
{

    public ToolkitConsoleHandler(ConsoleReader consolereader)
    {
        reader = consolereader;
    }

    public synchronized void flush()
    {
        try
        {
            if(reader != null)
            {
                reader.print("\r");
                reader.flush();
                super.flush();
                try
                {
                    reader.drawLine();
                }
                catch(Throwable throwable)
                {
                    reader.getCursorBuffer().clear();
                }
                reader.flush();
            } else
            {
                super.flush();
            }
        }
        catch(IOException ioexception) { }
    }

    private ConsoleReader reader;
}
