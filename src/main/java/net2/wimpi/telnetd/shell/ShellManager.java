package net2.wimpi.telnetd.shell;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import net2.wimpi.telnetd.BootException;
import net2.wimpi.telnetd.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ShellManager
{
  private static Log log = LogFactory.getLog(ShellManager.class);
  private static ShellManager c_Self;
  private HashMap m_Shells;
  
  private ShellManager() {}
  
  private ShellManager(HashMap paramHashMap)
  {
    c_Self = this;
    this.m_Shells = new HashMap(paramHashMap.size());
    setupShells(paramHashMap);
  }
  
  public Shell getShell(String paramString)
  {
    Shell localShell = null;
    try
    {
      if (!this.m_Shells.containsKey(paramString)) {
        return null;
      }
      Class localClass = (Class)this.m_Shells.get(paramString);
      Method localMethod = localClass.getMethod("createShell", null);
      log.debug("[Factory Method] " + localMethod.toString());
      localShell = (Shell)localMethod.invoke(localClass, null);
    }
    catch (Exception localException)
    {
      log.error("getShell()", localException);
    }
    return localShell;
  }
  
  private void setupShells(HashMap paramHashMap)
  {
    String str1 = "";
    String str2 = "";
    HashMap localHashMap = new HashMap(paramHashMap.size());
    Iterator localIterator = paramHashMap.keySet().iterator();
    while (localIterator.hasNext()) {
      try
      {
        str1 = (String)localIterator.next();
        str2 = (String)paramHashMap.get(str1);
        log.debug("Preparing Shell [" + str1 + "] " + str2);
        if (localHashMap.containsKey(str2))
        {
          this.m_Shells.put(str1, localHashMap.get(str2));
          log.debug("Class [" + str2 + "] already loaded, using cached class object.");
        }
        else
        {
          Class localClass = Class.forName(str2);
          this.m_Shells.put(str1, localClass);
          localHashMap.put(str2, localClass);
          log.debug("Class [" + str2 + "] loaded and class object cached.");
        }
      }
      catch (Exception localException)
      {
        log.error("setupShells()", localException);
      }
    }
  }
  
  public static ShellManager createShellManager(Properties paramProperties)
    throws BootException
  {
        String[] localObject;
    try
    {
      log.debug("createShellManager()");
      HashMap localHashMap = new HashMap();
      String str = paramProperties.getProperty("shells");
      if (str != null)
      {
        localObject = StringUtil.split(str, ",");
        for (int i = 0; i < localObject.length; i++)
        {
          str = paramProperties.getProperty("shell." + localObject[i] + ".class");
          if (str == null)
          {
            log.debug("Shell entry named " + localObject[i] + " not found.");
            throw new BootException("Shell " + localObject[i] + " declared but not defined.");
          }
          localHashMap.put(localObject[i], str);
        }
      }
      return new ShellManager(localHashMap);
    }
    catch (Exception localException)
    {
      log.error("createManager()", localException);
      throw new BootException("Creating ShellManager Instance failed:\n" + localException.getMessage());
    }
  }
  
  public static ShellManager getReference()
  {
    return c_Self;
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.shell.ShellManager

 * JD-Core Version:    0.7.0.1

 */