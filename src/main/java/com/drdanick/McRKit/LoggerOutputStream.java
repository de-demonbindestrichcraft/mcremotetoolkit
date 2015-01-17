// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LoggerOutputStream.java
package com.drdanick.McRKit;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jline.console.ConsoleReader;
import jline.console.CursorBuffer;

public class LoggerOutputStream extends ByteArrayOutputStream {

    public LoggerOutputStream(Logger logger1, Level level1, PrintStream printstream, ConsoleReader consolereader) {
        buffer = new StringBuilder(4096);
        logger = logger1;
        level = level1;
        out = printstream;
        reader = consolereader;
    }

    public void flush()
            throws IOException {
        ConsoleReader consolereader = reader;
        String s;
        super.flush();
        s = toString();
        super.reset();
        buffer.append(s);
        if (!s.endsWith("\n")) {
            return;
        }
        try {
            s = buffer.toString();
            buffer.delete(0, buffer.length());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            if (reader != null) {
                reader.print("\r");
                reader.flush();
                out.print(s);
                try {
                    reader.drawLine();
                } catch (Throwable throwable) {
                    reader.getCursorBuffer().clear();
                }
                reader.flush();
            } else {
                out.print(s);
            }
        } catch (IOException ioexception) {
        }
    }
    private final String separator = System.getProperty("line.separator");
    private final Logger logger;
    private final Level level;
    private PrintStream out;
    private ConsoleReader reader;
    private StringBuilder buffer;
}
