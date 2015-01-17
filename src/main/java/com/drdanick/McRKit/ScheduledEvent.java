// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ScheduledEvent.java

package com.drdanick.McRKit;


// Referenced classes of package com.drdanick.McRKit:
//            Scheduler

public class ScheduledEvent
{

    public ScheduledEvent(Scheduler.Type type1, String s)
    {
        type = type1;
        message = s;
    }

    public Scheduler.Type getType()
    {
        return type;
    }

    public String getMessage()
    {
        return message;
    }

    private Scheduler.Type type;
    private String message;
}
