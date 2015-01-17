package net2.wimpi.telnetd.net;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;

public class ConnectionData
{
  private ConnectionManager m_CM;
  private Socket m_Socket;
  private InetAddress m_IP;
  private HashMap m_Environment;
  private String m_HostName;
  private String m_HostAddress;
  private int m_Port;
  private Locale m_Locale;
  private long m_LastActivity;
  private boolean m_Warned;
  private String m_NegotiatedTerminalType;
  private int[] m_TerminalGeometry;
  private boolean m_TerminalGeometryChanged = true;
  private String m_LoginShell;
  private boolean m_LineMode = false;
  private String m_EchoMode = "server";
  
  public ConnectionData(Socket paramSocket, ConnectionManager paramConnectionManager)
  {
    this.m_Socket = paramSocket;
    this.m_CM = paramConnectionManager;
    this.m_IP = paramSocket.getInetAddress();
    setHostName();
    setHostAddress();
    setLocale();
    this.m_Port = paramSocket.getPort();
    this.m_TerminalGeometry = new int[2];
    this.m_TerminalGeometry[0] = 80;
    this.m_TerminalGeometry[1] = 25;
    this.m_NegotiatedTerminalType = "default";
    this.m_Environment = new HashMap(20);
    activity();
  }
  
  public ConnectionManager getManager()
  {
    return this.m_CM;
  }
  
  public Socket getSocket()
  {
    return this.m_Socket;
  }
  
  public int getPort()
  {
    return this.m_Port;
  }
  
  public String getHostName()
  {
    return this.m_HostName;
  }
  
  public String getHostAddress()
  {
    return this.m_HostAddress;
  }
  
  public InetAddress getInetAddress()
  {
    return this.m_IP;
  }
  
  public Locale getLocale()
  {
    return this.m_Locale;
  }
  
  public long getLastActivity()
  {
    return this.m_LastActivity;
  }
  
  public void activity()
  {
    this.m_Warned = false;
    this.m_LastActivity = System.currentTimeMillis();
  }
  
  public void setWarned(boolean paramBoolean)
  {
    this.m_Warned = paramBoolean;
    if (!paramBoolean) {
      this.m_LastActivity = System.currentTimeMillis();
    }
  }
  
  public boolean isWarned()
  {
    return this.m_Warned;
  }
  
  public void setTerminalGeometry(int paramInt1, int paramInt2)
  {
    this.m_TerminalGeometry[0] = paramInt1;
    this.m_TerminalGeometry[1] = paramInt2;
    this.m_TerminalGeometryChanged = true;
  }
  
  public int[] getTerminalGeometry()
  {
    if (this.m_TerminalGeometryChanged) {
      this.m_TerminalGeometryChanged = false;
    }
    return this.m_TerminalGeometry;
  }
  
  public int getTerminalColumns()
  {
    return this.m_TerminalGeometry[0];
  }
  
  public int getTerminalRows()
  {
    return this.m_TerminalGeometry[1];
  }
  
  public boolean isTerminalGeometryChanged()
  {
    return this.m_TerminalGeometryChanged;
  }
  
  public void setNegotiatedTerminalType(String paramString)
  {
    this.m_NegotiatedTerminalType = paramString;
  }
  
  public String getNegotiatedTerminalType()
  {
    return this.m_NegotiatedTerminalType;
  }
  
  public HashMap getEnvironment()
  {
    return this.m_Environment;
  }
  
  public void setLoginShell(String paramString)
  {
    this.m_LoginShell = paramString;
  }
  
  public String getLoginShell()
  {
    return this.m_LoginShell;
  }
  
  public boolean isLineMode()
  {
    return this.m_LineMode;
  }
  
  public void setLineMode(boolean paramBoolean)
  {
    this.m_LineMode = paramBoolean;
  }
  
  private void setHostName()
  {
    this.m_HostName = this.m_IP.getHostName();
  }
  
  private void setHostAddress()
  {
    this.m_HostAddress = this.m_IP.getHostAddress();
  }
  
  private void setLocale()
  {
    String str = getHostName();
    try
    {
      str = str.substring(str.lastIndexOf(".") + 1);
      if (str.equals("at"))
      {
        this.m_Locale = new Locale("de", "AT");
        return;
      }
      if (str.equals("de"))
      {
        this.m_Locale = new Locale("de", "DE");
        return;
      }
      if (str.equals("mx"))
      {
        this.m_Locale = new Locale("es", "MX");
        return;
      }
      if (str.equals("es"))
      {
        this.m_Locale = new Locale("es", "ES");
        return;
      }
      if (str.equals("it"))
      {
        this.m_Locale = Locale.ITALY;
        return;
      }
      if (str.equals("fr"))
      {
        this.m_Locale = Locale.FRANCE;
        return;
      }
      if (str.equals("uk"))
      {
        this.m_Locale = new Locale("en", "GB");
        return;
      }
      if (str.equals("arpa"))
      {
        this.m_Locale = Locale.US;
        return;
      }
      if (str.equals("com"))
      {
        this.m_Locale = Locale.US;
        return;
      }
      if (str.equals("edu"))
      {
        this.m_Locale = Locale.US;
        return;
      }
      if (str.equals("gov"))
      {
        this.m_Locale = Locale.US;
        return;
      }
      if (str.equals("org"))
      {
        this.m_Locale = Locale.US;
        return;
      }
      if (str.equals("mil"))
      {
        this.m_Locale = Locale.US;
        return;
      }
      this.m_Locale = Locale.ENGLISH;
    }
    catch (Exception localException)
    {
      this.m_Locale = Locale.ENGLISH;
    }
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.net.ConnectionData

 * JD-Core Version:    0.7.0.1

 */