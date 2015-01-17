package net2.wimpi.telnetd.net;

public abstract interface ConnectionListener
{
  public abstract void connectionIdle(ConnectionEvent paramConnectionEvent);
  
  public abstract void connectionTimedOut(ConnectionEvent paramConnectionEvent);
  
  public abstract void connectionLogoutRequest(ConnectionEvent paramConnectionEvent);
  
  public abstract void connectionSentBreak(ConnectionEvent paramConnectionEvent);
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.net.ConnectionListener

 * JD-Core Version:    0.7.0.1

 */