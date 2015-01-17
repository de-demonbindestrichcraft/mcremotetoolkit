package net2.wimpi.telnetd.io.toolkit;

import net2.wimpi.telnetd.io.BasicTerminalIO;

public abstract class ActiveComponent
  extends Component
{
  public ActiveComponent(BasicTerminalIO paramBasicTerminalIO, String paramString)
  {
    super(paramBasicTerminalIO, paramString);
  }
  
  public abstract void run()
    throws Exception;
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.ActiveComponent

 * JD-Core Version:    0.7.0.1

 */