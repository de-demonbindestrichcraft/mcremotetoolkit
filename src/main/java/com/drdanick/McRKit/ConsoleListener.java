// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ConsoleListener.java

package com.drdanick.McRKit;


public interface ConsoleListener
{

    public abstract void queueOutputString(String s)
        throws InterruptedException;
}
