package net2.wimpi.telnetd.net;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Properties;
import net2.wimpi.telnetd.BootException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PortListener
  implements Runnable
{
  private static Log log = LogFactory.getLog(PortListener.class);
  private String m_Name;
  private int m_Port;
  private InetAddress m_address = null;
  private int m_FloodProtection;
  private ServerSocket m_ServerSocket = null;
  private Thread m_Thread;
  private ConnectionManager m_ConnectionManager;
  private boolean m_Stopping = false;
  private boolean m_Available;
  private static final String logmsg = "Listening to Port {0,number,integer} with a connectivity queue size of {1,number,integer}.";
  
  private PortListener(String paramString, int paramInt1, int paramInt2)
  {
    this.m_Name = paramString;
    this.m_Available = false;
    this.m_Port = paramInt1;
    this.m_FloodProtection = paramInt2;
  }
  
  public String getName()
  {
    return this.m_Name;
  }
  
  public boolean isAvailable()
  {
    return this.m_Available;
  }
  
  public void setAvailable(boolean paramBoolean)
  {
    this.m_Available = paramBoolean;
  }
  
  public void setBindAddress(String paramString)
  {
    try
    {
      this.m_address = InetAddress.getByName(paramString);
    }
    catch (UnknownHostException localUnknownHostException)
    {
      System.err.println("ERROR: Cannot bind Telnet server to host '" + paramString + "': " + localUnknownHostException);
    }
  }
  
  public InetAddress getBindAddress()
  {
    return this.m_address;
  }
  
  public void start()
  {
    log.debug("start()");
    this.m_Thread = new Thread(this);
    this.m_Thread.start();
    this.m_Available = true;
  }
  
  public void stop()
  {
    log.debug("stop()::" + toString());
    this.m_Stopping = true;
    this.m_Available = false;
    this.m_ConnectionManager.stop();
    try
    {
      this.m_ServerSocket.close();
    }
    catch (IOException localIOException)
    {
      log.error("stop()", localIOException);
    }
    try
    {
      this.m_Thread.join();
    }
    catch (InterruptedException localInterruptedException)
    {
      log.error("stop()", localInterruptedException);
    }
    log.info("stop()::Stopped " + toString());
  }
  
  public void run()
  {
    try
    {
      Object localObject;
      if (this.m_address == null)
      {
        this.m_ServerSocket = new ServerSocket(this.m_Port, this.m_FloodProtection);
        localObject = new Object[] { new Integer(this.m_Port), new Integer(this.m_FloodProtection) };
        log.info(MessageFormat.format("Listening to Port {0,number,integer} with a connectivity queue size of {1,number,integer}.", (Object[])localObject));
      }
      else
      {
        this.m_ServerSocket = new ServerSocket(this.m_Port, this.m_FloodProtection, this.m_address);
        localObject = new Object[] { new Integer(this.m_Port), new Integer(this.m_FloodProtection), this.m_address };
        log.info(MessageFormat.format("Listening to Port {0,number,integer} with a connectivity queue size of {1,number,integer}.", (Object[])localObject));
      }
      do
      {
        try
        {
          localObject = this.m_ServerSocket.accept();
          if (this.m_Available) {
            this.m_ConnectionManager.makeConnection((Socket)localObject);
          } else {
            ((Socket)localObject).close();
          }
        }
        catch (SocketException localSocketException)
        {
          if (this.m_Stopping) {
            log.debug("run(): ServerSocket closed by stop()");
          } else {
            log.error("run()", localSocketException);
          }
        }
      } while (!this.m_Stopping);
    }
    catch (IOException localIOException)
    {
      log.error("run()", localIOException);
    }
    log.debug("run(): returning.");
  }
  
  public ConnectionManager getConnectionManager()
  {
    return this.m_ConnectionManager;
  }
  
  public static PortListener createPortListener(String paramString, Properties paramProperties)
    throws BootException
  {
    PortListener localPortListener = null;
    try
    {
      int i = Integer.parseInt(paramProperties.getProperty(paramString + ".port"));
      int j = Integer.parseInt(paramProperties.getProperty(paramString + ".floodprotection"));
      if (new Boolean(paramProperties.getProperty(paramString + ".secure")).booleanValue()) {}
      localPortListener = new PortListener(paramString, i, j);
    }
    catch (Exception localException1)
    {
      log.error("createPortListener()", localException1);
      throw new BootException("Failure while creating PortListener instance:\n" + localException1.getMessage());
    }
    if (localPortListener.m_ConnectionManager == null)
    {
      localPortListener.m_ConnectionManager = ConnectionManager.createConnectionManager(paramString, paramProperties);
      try
      {
        localPortListener.m_ConnectionManager.start();
      }
      catch (Exception localException2)
      {
        log.error("createPortListener()", localException2);
        throw new BootException("Failure while starting ConnectionManager watchdog thread:\n" + localException2.getMessage());
      }
    }
    return localPortListener;
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.net.PortListener

 * JD-Core Version:    0.7.0.1

 */