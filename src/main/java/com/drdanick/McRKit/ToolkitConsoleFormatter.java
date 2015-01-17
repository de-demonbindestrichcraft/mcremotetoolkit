// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ToolkitConsoleFormatter.java

package com.drdanick.McRKit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ToolkitConsoleFormatter extends Formatter
{

    public ToolkitConsoleFormatter()
    {
    }

    public String format(LogRecord logrecord)
    {
        StringBuilder stringbuilder = new StringBuilder();
        Throwable throwable = logrecord.getThrown();
        stringbuilder.append(formatMessage(logrecord));
        stringbuilder.append('\n');
        if(throwable != null)
        {
            StringWriter stringwriter = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stringwriter));
            stringbuilder.append(stringwriter);
        }
        return stringbuilder.toString();
    }
}
