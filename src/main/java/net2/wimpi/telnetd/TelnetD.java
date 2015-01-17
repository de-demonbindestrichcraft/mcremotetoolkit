package net2.wimpi.telnetd;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import net2.wimpi.telnetd.io.terminal.TerminalManager;
import net2.wimpi.telnetd.net.PortListener;
import net2.wimpi.telnetd.shell.ShellManager;
import net2.wimpi.telnetd.util.PropertiesLoader;
import net2.wimpi.telnetd.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TelnetD
{
  private static Log log = LogFactory.getLog(TelnetD.class);
  public static Log debuglog = log;
  public static Log syslog = log;
  private static TelnetD c_Self = null;
  private List m_Listeners;
  private ShellManager m_ShellManager;
  private static final String VERSION = "2.0";
  
  private TelnetD()
  {
    c_Self = this;
    this.m_Listeners = new ArrayList(5);
  }
  
  public void start()
  {
    log.debug("start()");
    for (int i = 0; i < this.m_Listeners.size(); i++)
    {
      PortListener localPortListener = (PortListener)this.m_Listeners.get(i);
      localPortListener.start();
    }
  }
  
  public void stop()
  {
    for (int i = 0; i < this.m_Listeners.size(); i++)
    {
      PortListener localPortListener = (PortListener)this.m_Listeners.get(i);
      localPortListener.stop();
    }
  }
  
  public String getVersion()
  {
    return "2.0";
  }
  
  private void prepareShellManager(Properties paramProperties)
    throws BootException
  {
    this.m_ShellManager = ShellManager.createShellManager(paramProperties);
    if (this.m_ShellManager == null) {
      System.exit(1);
    }
  }
  
  private void prepareListener(String paramString, Properties paramProperties)
    throws BootException
  {
    PortListener localPortListener = PortListener.createPortListener(paramString, paramProperties);
    try
    {
      this.m_Listeners.add(localPortListener);
    }
    catch (Exception localException)
    {
      throw new BootException("Failure while starting PortListener thread: " + localException.getMessage());
    }
  }
  
  private void prepareTerminals(Properties paramProperties)
    throws BootException
  {
    TerminalManager.createTerminalManager(paramProperties);
  }
  
  public PortListener getPortListener(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return null;
    }
    Iterator localIterator = this.m_Listeners.iterator();
    while (localIterator.hasNext())
    {
      PortListener localPortListener = (PortListener)localIterator.next();
      if (localPortListener.getName().equals(paramString)) {
        return localPortListener;
      }
    }
    return null;
  }
  
  public static TelnetD createTelnetD(Properties paramProperties)
    throws BootException
  {
    if (c_Self == null)
    {
      TelnetD localTelnetD = new TelnetD();
      localTelnetD.prepareShellManager(paramProperties);
      localTelnetD.prepareTerminals(paramProperties);
      String[] arrayOfString = StringUtil.split(paramProperties.getProperty("listeners"), ",");
      for (int i = 0; i < arrayOfString.length; i++) {
        localTelnetD.prepareListener(arrayOfString[i], paramProperties);
      }
      return localTelnetD;
    }
    throw new BootException("Singleton already instantiated.");
  }
  
  public static TelnetD createTelnetD(String paramString)
    throws BootException
  {
    try
    {
      return createTelnetD(PropertiesLoader.loadProperties(paramString));
    }
    catch (IOException localIOException)
    {
      log.error(localIOException);
      throw new BootException("Failed to load configuration from given URL.");
    }
  }
  
  public static TelnetD getReference()
  {
    if (c_Self != null) {
      return c_Self;
    }
    return null;
  }
  
  public static void main(String[] paramArrayOfString)
  {
    TelnetD localTelnetD = null;
    try
    {
      if (paramArrayOfString.length == 0)
      {
        System.out.println("\nUsage: java net.wimpi.telnetd.TelnetD urlprefix\n");
        System.out.println("         java net.wimpi.telnetd.TelnetD url\n");
        System.exit(1);
      }
      else
      {
        localTelnetD = createTelnetD(paramArrayOfString[0]);
      }
      localTelnetD.start();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      System.exit(1);
    }
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.TelnetD

 * JD-Core Version:    0.7.0.1

 */