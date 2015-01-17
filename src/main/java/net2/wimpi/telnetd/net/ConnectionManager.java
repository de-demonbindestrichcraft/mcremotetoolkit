package net2.wimpi.telnetd.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import net2.wimpi.telnetd.BootException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectionManager
  implements Runnable
{
  private static Log log = LogFactory.getLog(ConnectionManager.class);
  private Thread m_Thread;
  private ThreadGroup m_ThreadGroup = new ThreadGroup(toString() + "Connections");
  private List m_OpenConnections = Collections.synchronizedList(new ArrayList(100));
  private Stack m_ClosedConnections = new Stack();
  private ConnectionFilter m_Filter;
  private int m_MaxConnections;
  private int m_WarningTimeout;
  private int m_DisconnectTimeout;
  private int m_HousekeepingInterval;
  private String m_LoginShell;
  private boolean m_LineMode = false;
  private boolean m_Stopping = false;
  
  private ConnectionManager(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ConnectionFilter paramConnectionFilter, String paramString, boolean paramBoolean)
  {
    this.m_Filter = paramConnectionFilter;
    this.m_LoginShell = paramString;
    this.m_LineMode = paramBoolean;
    this.m_MaxConnections = paramInt1;
    this.m_WarningTimeout = paramInt2;
    this.m_DisconnectTimeout = paramInt3;
    this.m_HousekeepingInterval = paramInt4;
  }
  
  public void setConnectionFilter(ConnectionFilter paramConnectionFilter)
  {
    this.m_Filter = paramConnectionFilter;
  }
  
  public ConnectionFilter getConnectionFilter()
  {
    return this.m_Filter;
  }
  
  public int openConnectionCount()
  {
    return this.m_OpenConnections.size();
  }
  
  public Connection getConnection(int paramInt)
  {
    synchronized (this.m_OpenConnections)
    {
      return (Connection)this.m_OpenConnections.get(paramInt);
    }
  }
  
  public Connection[] getConnectionsByAdddress(InetAddress paramInetAddress)
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (this.m_OpenConnections)
    {
      Iterator localIterator = this.m_OpenConnections.iterator();
      while (localIterator.hasNext())
      {
        Connection localConnection = (Connection)localIterator.next();
        if (localConnection.getConnectionData().getInetAddress().equals(paramInetAddress)) {
          localArrayList.add(localConnection);
        }
      }
    }
    return (Connection[])localArrayList.toArray();
  }
  
  public void start()
  {
    this.m_Thread = new Thread(this);
    this.m_Thread.start();
  }
  
  public void stop()
  {
    log.debug("stop()::" + toString());
    this.m_Stopping = true;
    try
    {
      this.m_Thread.join();
    }
    catch (InterruptedException localInterruptedException)
    {
      log.error("stop()", localInterruptedException);
    }
    synchronized (this.m_OpenConnections)
    {
      Iterator localIterator = this.m_OpenConnections.iterator();
      while (localIterator.hasNext()) {
        try
        {
          Connection localConnection = (Connection)localIterator.next();
          localConnection.close();
        }
        catch (Exception localException)
        {
          log.error("stop()", localException);
        }
      }
      this.m_OpenConnections.clear();
    }
    log.debug("stop():: Stopped " + toString());
  }
  
  public void makeConnection(Socket paramSocket)
  {
    log.debug("makeConnection()::" + paramSocket.toString());
    if ((this.m_Filter == null) || ((this.m_Filter != null) && (this.m_Filter.isAllowed(paramSocket.getInetAddress()))))
    {
      ConnectionData localConnectionData = new ConnectionData(paramSocket, this);
      localConnectionData.setLoginShell(this.m_LoginShell);
      localConnectionData.setLineMode(this.m_LineMode);
      if (this.m_OpenConnections.size() < this.m_MaxConnections)
      {
        Connection localConnection = new Connection(this.m_ThreadGroup, localConnectionData);
        Object[] arrayOfObject = { new Integer(this.m_OpenConnections.size() + 1) };
        log.info(MessageFormat.format("connection #{0,number,integer} made.", arrayOfObject));
        synchronized (this.m_OpenConnections)
        {
          this.m_OpenConnections.add(localConnection);
        }
        localConnection.start();
      }
    }
    else
    {
      log.info("makeConnection():: Active Filter blocked incoming connection.");
      try
      {
        paramSocket.close();
      }
      catch (IOException localIOException) {}
    }
  }
  
  public void run()
  {
    try
    {
      do
      {
        cleanupClosed();
        checkOpenConnections();
        Thread.sleep(this.m_HousekeepingInterval);
      } while (!this.m_Stopping);
    }
    catch (Exception localException)
    {
      log.error("run()", localException);
    }
    log.debug("run():: Ran out " + toString());
  }
  
  private void cleanupClosed()
  {
    if (this.m_Stopping) {
      return;
    }
    while (!this.m_ClosedConnections.isEmpty())
    {
      Connection localConnection = (Connection)this.m_ClosedConnections.pop();
      log.info("cleanupClosed():: Removing closed connection " + localConnection.toString());
      synchronized (this.m_OpenConnections)
      {
        this.m_OpenConnections.remove(localConnection);
      }
    }
  }
  
  private void checkOpenConnections()
  {
    if (this.m_Stopping) {
      return;
    }
    synchronized (this.m_OpenConnections)
    {
      Iterator localIterator = this.m_OpenConnections.iterator();
      while (localIterator.hasNext())
      {
        Connection localConnection = (Connection)localIterator.next();
        ConnectionData localConnectionData = localConnection.getConnectionData();
        if (!localConnection.isActive())
        {
          registerClosedConnection(localConnection);
        }
        else
        {
          long l = System.currentTimeMillis() - localConnectionData.getLastActivity();
          if (l > this.m_WarningTimeout) {
            if (l > this.m_DisconnectTimeout + this.m_WarningTimeout)
            {
              log.debug("checkOpenConnections():" + localConnection.toString() + " exceeded total timeout.");
              localConnection.processConnectionEvent(new ConnectionEvent(localConnection, 101));
            }
            else if (!localConnectionData.isWarned())
            {
              log.debug("checkOpenConnections():" + localConnection.toString() + " exceeded warning timeout.");
              localConnectionData.setWarned(true);
              localConnection.processConnectionEvent(new ConnectionEvent(localConnection, 100));
            }
          }
        }
      }
    }
  }
  
  public void registerClosedConnection(Connection paramConnection)
  {
    if (this.m_Stopping) {
      return;
    }
    if (!this.m_ClosedConnections.contains(paramConnection))
    {
      log.debug("registerClosedConnection()::" + paramConnection.toString());
      this.m_ClosedConnections.push(paramConnection);
    }
  }
  
  public static ConnectionManager createConnectionManager(String paramString, Properties paramProperties)
    throws BootException
  {
    try
    {
      int i = Integer.parseInt(paramProperties.getProperty(paramString + ".maxcon"));
      int j = Integer.parseInt(paramProperties.getProperty(paramString + ".time_to_warning"));
      int k = Integer.parseInt(paramProperties.getProperty(paramString + ".time_to_timedout"));
      int m = Integer.parseInt(paramProperties.getProperty(paramString + ".housekeepinginterval"));
      String str1 = paramProperties.getProperty(paramString + ".connectionfilter");
      ConnectionFilter localConnectionFilter = null;
      String str2 = "";
      boolean bool = false;
      if ((str1 != null) && (str1.length() != 0) && (!str1.toLowerCase().equals("none")))
      {
        localConnectionFilter = (ConnectionFilter)Class.forName(str1).newInstance();
        localConnectionFilter.initialize(paramProperties);
      }
      str2 = paramProperties.getProperty(paramString + ".loginshell");
      if ((str2 == null) || (str2.length() == 0))
      {
        log.error("Login shell not specified.");
        throw new BootException("Login shell must be specified.");
      }
      String str3 = paramProperties.getProperty(paramString + ".inputmode");
      if ((str3 == null) || (str3.length() == 0))
      {
        log.info("Input mode not specified using character input as default.");
        bool = false;
      }
      else if (str3.toLowerCase().equals("line"))
      {
        bool = true;
      }
      ConnectionManager localConnectionManager = new ConnectionManager(i, j, k, m, localConnectionFilter, str2, bool);
      return localConnectionManager;
    }
    catch (Exception localException)
    {
      log.error("createConnectionManager():", localException);
      throw new BootException("Failure while creating ConnectionManger instance:\n" + localException.getMessage());
    }
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.net.ConnectionManager

 * JD-Core Version:    0.7.0.1

 */