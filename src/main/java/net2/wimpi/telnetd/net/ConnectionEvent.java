package net2.wimpi.telnetd.net;

public class ConnectionEvent
{
  private int m_Type;
  private Connection m_Source;
  public static final int CONNECTION_IDLE = 100;
  public static final int CONNECTION_TIMEDOUT = 101;
  public static final int CONNECTION_LOGOUTREQUEST = 102;
  public static final int CONNECTION_BROKEN = 103;
  public static final int CONNECTION_BREAK = 104;
  
  public ConnectionEvent(Connection paramConnection, int paramInt)
  {
    this.m_Type = paramInt;
    this.m_Source = paramConnection;
  }
  
  public Connection getSource()
  {
    return this.m_Source;
  }
  
  /**
   * @deprecated
   */
  public Connection getConnection()
  {
    return this.m_Source;
  }
  
  public boolean isType(int paramInt)
  {
    return this.m_Type == paramInt;
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.net.ConnectionEvent

 * JD-Core Version:    0.7.0.1

 */