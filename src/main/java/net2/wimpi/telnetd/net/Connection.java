package net2.wimpi.telnetd.net;

import java.net.Socket;
import java.util.Vector;
import net2.wimpi.telnetd.io.BasicTerminalIO;
import net2.wimpi.telnetd.io.TerminalIO;
import net2.wimpi.telnetd.shell.Shell;
import net2.wimpi.telnetd.shell.ShellManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Connection
  extends Thread
{
  private static Log log = LogFactory.getLog(Connection.class);
  private static int m_Number;
  private boolean m_Dead;
  private Vector m_Listeners;
  private ConnectionData m_ConnectionData;
  private BasicTerminalIO m_TerminalIO;
  private Shell m_NextShell = null;
  
  public Connection(ThreadGroup paramThreadGroup, ConnectionData paramConnectionData)
  {
    super(paramThreadGroup, "Connection" + ++m_Number);
    this.m_ConnectionData = paramConnectionData;
    this.m_Listeners = new Vector(3);
    this.m_TerminalIO = new TerminalIO(this);
    this.m_Dead = false;
  }
  
  public void run()
  {
    int i = 0;
    try
    {
      Shell localShell = ShellManager.getReference().getShell(this.m_ConnectionData.getLoginShell());
      do
      {
        localShell.run(this);
        if (this.m_Dead)
        {
          i = 1;
          break;
        }
        localShell = getNextShell();
        if (localShell == null) {
          i = 1;
        }
      } while ((i == 0) || (this.m_Dead));
    }
    catch (Exception localException)
    {
      log.error("run()", localException);
    }
    finally
    {
      if (!this.m_Dead) {
        close();
      }
    }
    log.debug("run():: Returning from " + toString());
  }
  
  public ConnectionData getConnectionData()
  {
    return this.m_ConnectionData;
  }
  
  public BasicTerminalIO getTerminalIO()
  {
    return this.m_TerminalIO;
  }
  
  public boolean setNextShell(String paramString)
  {
    this.m_NextShell = ShellManager.getReference().getShell(paramString);
    return this.m_NextShell != null;
  }
  
  private Shell getNextShell()
  {
    Shell localShell = this.m_NextShell;
    if (localShell != null)
    {
      this.m_NextShell = null;
      return localShell;
    }
    return null;
  }
  
  public synchronized void close()
  {
    if (this.m_Dead) {
      return;
    }
    try
    {
      this.m_Dead = true;
      this.m_TerminalIO.close();
    }
    catch (Exception localException1)
    {
      log.error("close()", localException1);
    }
    try
    {
      this.m_ConnectionData.getSocket().close();
    }
    catch (Exception localException2)
    {
      log.error("close()", localException2);
    }
    try
    {
      this.m_ConnectionData.getManager().registerClosedConnection(this);
    }
    catch (Exception localException3)
    {
      log.error("close()", localException3);
    }
    try
    {
      interrupt();
    }
    catch (Exception localException4)
    {
      log.error("close()", localException4);
    }
    log.debug("Closed " + toString() + " and inactive.");
  }
  
  public boolean isActive()
  {
    return !this.m_Dead;
  }
  
  public void addConnectionListener(ConnectionListener paramConnectionListener)
  {
    this.m_Listeners.addElement(paramConnectionListener);
  }
  
  public void removeConnectionListener(ConnectionListener paramConnectionListener)
  {
    this.m_Listeners.removeElement(paramConnectionListener);
  }
  
  public void processConnectionEvent(ConnectionEvent paramConnectionEvent)
  {
    for (int i = 0; i < this.m_Listeners.size(); i++)
    {
      ConnectionListener localConnectionListener = (ConnectionListener)this.m_Listeners.elementAt(i);
      if (paramConnectionEvent.isType(100)) {
        localConnectionListener.connectionIdle(paramConnectionEvent);
      } else if (paramConnectionEvent.isType(101)) {
        localConnectionListener.connectionTimedOut(paramConnectionEvent);
      } else if (paramConnectionEvent.isType(102)) {
        localConnectionListener.connectionLogoutRequest(paramConnectionEvent);
      } else if (paramConnectionEvent.isType(104)) {
        localConnectionListener.connectionSentBreak(paramConnectionEvent);
      }
    }
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.net.Connection

 * JD-Core Version:    0.7.0.1

 */