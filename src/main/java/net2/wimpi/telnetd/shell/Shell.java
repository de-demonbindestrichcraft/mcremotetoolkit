package net2.wimpi.telnetd.shell;

import net2.wimpi.telnetd.net.Connection;
import net2.wimpi.telnetd.net.ConnectionListener;

public abstract interface Shell
  extends ConnectionListener
{
  public abstract void run(Connection paramConnection);
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.shell.Shell

 * JD-Core Version:    0.7.0.1

 */