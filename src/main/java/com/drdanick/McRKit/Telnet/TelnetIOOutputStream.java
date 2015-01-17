// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TelnetIOOutputStream.java

package com.drdanick.McRKit.Telnet;

import java.io.IOException;
import java.io.OutputStream;
import net.wimpi.telnetd.io.BasicTerminalIO;

public class TelnetIOOutputStream extends OutputStream
{

    public TelnetIOOutputStream(BasicTerminalIO basicterminalio)
    {
        tio = basicterminalio;
    }

    public synchronized void write(int i)
        throws IOException
    {
        tio.write((char)i);
    }

    public synchronized void writeLine(String s)
        throws IOException
    {
        tio.write((new StringBuilder()).append(s).append("\r\n").toString());
    }

    public synchronized void write(String s)
        throws IOException
    {
        tio.write(s);
    }

    public synchronized void flush()
        throws IOException
    {
        tio.flush();
    }

    private BasicTerminalIO tio;
}
