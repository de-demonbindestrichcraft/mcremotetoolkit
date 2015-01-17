package net2.wimpi.telnetd.io.terminal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import net2.wimpi.telnetd.BootException;
import net2.wimpi.telnetd.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TerminalManager
{
  private static Log log = LogFactory.getLog(TerminalManager.class);
  private static TerminalManager c_Self;
  private HashMap m_Terminals;
  private boolean m_WindoofHack = false;
  
  private TerminalManager()
  {
    c_Self = this;
    this.m_Terminals = new HashMap(25);
  }
  
  public Terminal getTerminal(String paramString)
  {
    Terminal localTerminal = null;
    try
    {
      if ((paramString.equals("ANSI")) && (this.m_WindoofHack))
      {
        localTerminal = (Terminal)this.m_Terminals.get("windoof");
      }
      else
      {
        paramString = paramString.toLowerCase();
        if (!this.m_Terminals.containsKey(paramString)) {
          localTerminal = (Terminal)this.m_Terminals.get("default");
        } else {
          localTerminal = (Terminal)this.m_Terminals.get(paramString);
        }
      }
    }
    catch (Exception localException)
    {
      log.error("getTerminal()", localException);
    }
    return localTerminal;
  }
  
  public String[] getAvailableTerminals()
  {
    String[] arrayOfString = new String[this.m_Terminals.size()];
    int i = 0;
    Iterator localIterator = this.m_Terminals.keySet().iterator();
    while (localIterator.hasNext())
    {
      arrayOfString[i] = ((String)localIterator.next());
      i++;
    }
    return arrayOfString;
  }
  
  private void setWindoofHack(boolean paramBoolean)
  {
    this.m_WindoofHack = paramBoolean;
  }
  
  private void setupTerminals(HashMap paramHashMap)
  {
    String str1 = "";
    String str2 = "";
    Terminal localTerminal = null;
    Object[] arrayOfObject = null;
    Iterator localIterator = paramHashMap.keySet().iterator();
    while (localIterator.hasNext()) {
      try
      {
        str1 = (String)localIterator.next();
        arrayOfObject = (Object[])paramHashMap.get(str1);
        str2 = (String)arrayOfObject[0];
        log.debug("Preparing terminal [" + str1 + "] " + str2);
        localTerminal = (Terminal)Class.forName(str2).newInstance();
        this.m_Terminals.put(str1, localTerminal);
        String[] arrayOfString = (String[])arrayOfObject[1];
        for (int i = 0; i < arrayOfString.length; i++) {
          if (!this.m_Terminals.containsKey(arrayOfString[i])) {
            this.m_Terminals.put(arrayOfString[i], localTerminal);
          }
        }
      }
      catch (Exception localException)
      {
        log.error("setupTerminals()", localException);
      }
    }
    log.debug("Terminals:");
    localIterator = this.m_Terminals.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str3 = (String)localIterator.next();
      log.debug(str3 + "=" + this.m_Terminals.get(str3));
    }
  }
  
  public static TerminalManager createTerminalManager(Properties paramProperties)
    throws BootException
  {
    HashMap localHashMap = new HashMap(20);
    int i = 0;
    TerminalManager localTerminalManager = new TerminalManager();
    try
    {
      log.debug("Creating terminal manager.....");
      boolean bool = new Boolean(paramProperties.getProperty("terminals.windoof")).booleanValue();
      String str = paramProperties.getProperty("terminals");
      if (str == null)
      {
        log.debug("No terminals declared.");
        throw new BootException("No terminals declared.");
      }
      String[] arrayOfString1 = StringUtil.split(str, ",");
      Object[] arrayOfObject = null;
      String[] arrayOfString2 = null;
      for (int j = 0; j < arrayOfString1.length; j++)
      {
        arrayOfObject = new Object[2];
        arrayOfObject[0] = paramProperties.getProperty("term." + arrayOfString1[j] + ".class");
        arrayOfString2 = StringUtil.split(paramProperties.getProperty("term." + arrayOfString1[j] + ".aliases"), ",");
        for (int k = 0; k < arrayOfString2.length; k++) {
          if (arrayOfString2[k].equalsIgnoreCase("default")) {
            if (i == 0) {
              i = 1;
            } else {
              throw new BootException("Only one default can be declared.");
            }
          }
        }
        arrayOfObject[1] = arrayOfString2;
        localHashMap.put(arrayOfString1[j], arrayOfObject);
      }
      if (i == 0) {
        throw new BootException("No default terminal declared.");
      }
      localTerminalManager = new TerminalManager();
      localTerminalManager.setWindoofHack(bool);
      localTerminalManager.setupTerminals(localHashMap);
      return localTerminalManager;
    }
    catch (Exception localException)
    {
      log.error("createManager()", localException);
      throw new BootException("Creating TerminalManager Instance failed:\n" + localException.getMessage());
    }
  }
  
  public static TerminalManager getReference()
  {
    return c_Self;
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.terminal.TerminalManager

 * JD-Core Version:    0.7.0.1

 */