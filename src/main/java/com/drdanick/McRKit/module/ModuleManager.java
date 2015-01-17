// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ModuleManager.java

package com.drdanick.McRKit.module;

import com.drdanick.McRKit.HookEvent;
import com.drdanick.McRKit.ToolkitEvent;
import java.io.File;
import java.io.PrintStream;
import java.util.*;

// Referenced classes of package com.drdanick.McRKit.module:
//            ModuleManagerException, InvalidModuleException, ModuleLoader, Module

public class ModuleManager
{

    private ModuleManager()
    {
        moduleManagerInstance = this;
    }

    public static ModuleManager createModuleManager()
        throws ModuleManagerException
    {
        if(moduleManagerInstance == null)
            return new ModuleManager();
        else
            throw new ModuleManagerException("ModuleManager is already instantiated.");
    }

    public static ModuleManager getInstance()
    {
        return moduleManagerInstance;
    }

    public void loadModules(File file)
    {
        if(!file.exists())
            file.mkdirs();
        File afile[] = file.listFiles();
        File afile1[] = afile;
        int i = afile1.length;
        for(int j = 0; j < i; j++)
        {
            File file1 = afile1[j];
            Module module = null;
            try
            {
                module = loadModule(file1);
            }
            catch(InvalidModuleException invalidmoduleexception)
            {
                System.err.println((new StringBuilder()).append("ERROR: Could not load Toolkit Module ").append(file1.getPath()).append(" in ").append(file.getPath()).append(": ").append(invalidmoduleexception.getMessage()).toString());
            }
            if(module != null)
                modules.add(module);
        }

    }

    private Module loadModule(File file)
        throws InvalidModuleException
    {
        ModuleLoader moduleloader = new ModuleLoader();
        Module module = moduleloader.loadModule(file);
        return module;
    }

    public List getModules()
    {
        return modules;
    }

    public void handleToolkitStateEvent(ToolkitEvent toolkitevent)
    {
        Iterator iterator = modules.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            Module module = (Module)iterator.next();
            if(module.getStartEvent() == toolkitevent)
                module.startModule();
            else
            if(module.getStopEvent() == toolkitevent)
                module.stopModule();
        } while(true);
    }

    public void startModules()
    {
        Module module;
        for(Iterator iterator = modules.iterator(); iterator.hasNext(); module.startModule())
            module = (Module)iterator.next();

    }

    public void stopModules()
    {
        Module module;
        for(Iterator iterator = modules.iterator(); iterator.hasNext(); module.stopModule())
            module = (Module)iterator.next();

    }

    public void handleHookEvent(HookEvent hookevent)
    {
        if(hookevent.getType() == com.drdanick.McRKit.HookEvent.Type.STDIN)
        {
            Iterator iterator = modules.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                Module module1 = (Module)iterator.next();
                if(module1.isEnabled())
                    module1.onStdinString((String)hookevent.getData());
            } while(true);
        } else
        if(hookevent.getType() == com.drdanick.McRKit.HookEvent.Type.STDOUT)
        {
            Iterator iterator1 = modules.iterator();
            do
            {
                if(!iterator1.hasNext())
                    break;
                Module module2 = (Module)iterator1.next();
                if(module2.isEnabled())
                    module2.onStdoutString((String)hookevent.getData());
            } while(true);
        } else
        if(hookevent.getType() == com.drdanick.McRKit.HookEvent.Type.STDERR)
        {
            Iterator iterator2 = modules.iterator();
            do
            {
                if(!iterator2.hasNext())
                    break;
                Module module3 = (Module)iterator2.next();
                if(module3.isEnabled())
                    module3.onStderrString((String)hookevent.getData());
            } while(true);
        } else
        if(hookevent.getType() == com.drdanick.McRKit.HookEvent.Type.COMMAND)
        {
            Module module = hookevent.getModule();
            if(module.isEnabled())
            {
                String as[] = ((String)hookevent.getData()).split(" ", 2);
                if(as != null && as.length > 1)
                    module.onConsoleCommand(as[0], as[1]);
                else
                    module.onConsoleCommand((String)hookevent.getData(), "");
            }
        }
    }

    private final ArrayList modules = new ArrayList();
    private static ModuleManager moduleManagerInstance = null;

}
