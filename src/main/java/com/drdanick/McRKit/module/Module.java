 package com.drdanick.McRKit.module;
 
 import com.drdanick.McRKit.ToolkitEvent;
 import java.io.PrintStream;
 import java.util.HashMap;
 import java.util.Map;
 
 public abstract class Module
 {
   private ModuleMetadata meta;
   private ModuleLoader moduleLoader;
   private ClassLoader cLoader;
   private ToolkitEvent startEvent;
   private ToolkitEvent stopEvent;
   private boolean enabled;
   private final HashMap<String, String> commandMap = new HashMap();
   
   protected Module(ModuleMetadata paramModuleMetadata, ModuleLoader paramModuleLoader, ClassLoader paramClassLoader, ToolkitEvent paramToolkitEvent1, ToolkitEvent paramToolkitEvent2)
   {
     this.meta = paramModuleMetadata;
     this.moduleLoader = paramModuleLoader;
     this.cLoader = paramClassLoader;
     this.startEvent = paramToolkitEvent1;
     this.stopEvent = paramToolkitEvent2;
     this.enabled = false;
   }
   
   public final ModuleMetadata getMetadata()
   {
     return this.meta;
   }
   
   public final ModuleLoader getModuleLoader()
   {
     return this.moduleLoader;
   }
   
   public final ClassLoader getClassLoader()
   {
     return this.cLoader;
   }
   
   public final ToolkitEvent getStartEvent()
   {
     return this.startEvent;
   }
   
   public final ToolkitEvent getStopEvent()
   {
     return this.stopEvent;
   }
   
   public boolean isEnabled()
   {
     return this.enabled;
   }
   
   public final void startModule()
   {
     if (this.enabled) {
       return;
     }
     try
     {
       onEnable();
       this.enabled = true;
     }
     catch (Throwable localThrowable)
     {
       System.err.println("Error enabling Toolkit module " + (String)this.meta.get("name") + ": ");
       localThrowable.printStackTrace();
     }
   }
   
   public final void stopModule()
   {
     if (!this.enabled) {
       return;
     }
     try
     {
       onDisable();
       this.enabled = false;
     }
     catch (Throwable localThrowable)
     {
       System.err.println("Error disabling Toolkit module " + (String)this.meta.get("name") + ": ");
       localThrowable.printStackTrace();
     }
   }
   
   public Map<String, String> getCommandMap()
   {
     return this.commandMap;
   }
   
   public boolean hasCommand(String paramString)
   {
     return this.commandMap.containsKey(paramString);
   }
   
   protected void registerCommand(String paramString1, String paramString2)
   {
     this.commandMap.put(paramString1, paramString2);
   }
   
   protected void deregisterCommand(String paramString)
   {
     this.commandMap.remove(paramString);
   }
   
   protected abstract void onEnable();
   
   protected abstract void onDisable();
   
   public void onStdinString(String paramString) {}
   
   public void onStdoutString(String paramString) {}
   
   public void onStderrString(String paramString) {}
   
   public void onConsoleCommand(String paramString1, String paramString2) {}
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.drdanick.McRKit.module...Module
 * JD-Core Version:    0.7.0.1
 */