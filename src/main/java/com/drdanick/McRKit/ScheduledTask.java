// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Scheduler.java

package com.drdanick.McRKit;

import java.util.TimerTask;

// Referenced classes of package com.drdanick.McRKit:
//            ScheduledEvent

class ScheduledTask extends TimerTask
{

    public ScheduledTask(ScheduledEvent scheduledevent)
    {
        e = scheduledevent;
    }

    public ScheduledEvent getEvent()
    {
        return e;
    }

    public void run()
    {
    }

    protected ScheduledEvent e;
}
