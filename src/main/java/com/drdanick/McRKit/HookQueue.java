// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HookQueue.java

package com.drdanick.McRKit;

import com.drdanick.McRKit.module.ModuleManager;
import java.io.PrintStream;
import java.util.concurrent.LinkedBlockingQueue;

// Referenced classes of package com.drdanick.McRKit:
//            ToolkitException, HookEvent

public class HookQueue extends Thread
{

    private HookQueue()
    {
        hookQueue = new LinkedBlockingQueue();
    }

    private HookQueue(ModuleManager modulemanager)
    {
        super("HookQueue");
        hookQueue = new LinkedBlockingQueue();
        setDaemon(true);
        manager = modulemanager;
        running = true;
    }

    public static HookQueue createHookQueue(ModuleManager modulemanager)
        throws ToolkitException
    {
        if(hookQueueInstance == null)
            return hookQueueInstance = new HookQueue(modulemanager);
        else
            throw new ToolkitException("HookQueue already instantiated");
    }

    public void run()
    {
        do
            try
            {
                manager.handleHookEvent((HookEvent)hookQueue.take());
                sleep(50L);
            }
            catch(Exception exception)
            {
                System.out.println("Remote Toolkit error: Unexpected hook manager exception!");
                exception.printStackTrace();
            }
        while(running);
    }

    public synchronized void put(HookEvent hookevent)
        throws InterruptedException
    {
        hookQueue.put(hookevent);
    }

    public void stopManager()
    {
        running = false;
    }

    public static HookQueue getInstance()
    {
        return hookQueueInstance;
    }

    private LinkedBlockingQueue hookQueue;
    private ModuleManager manager;
    private boolean running;
    private static HookQueue hookQueueInstance = null;

}
