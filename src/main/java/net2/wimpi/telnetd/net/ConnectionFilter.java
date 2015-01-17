package net2.wimpi.telnetd.net;

import java.net.InetAddress;
import java.util.Properties;

public abstract interface ConnectionFilter
{
  public abstract void initialize(Properties paramProperties);
  
  public abstract boolean isAllowed(InetAddress paramInetAddress);
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.net.ConnectionFilter

 * JD-Core Version:    0.7.0.1

 */