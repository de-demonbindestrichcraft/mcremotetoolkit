package net2.wimpi.telnetd.io.terminal;

public class Windoof
  extends BasicTerminal
{
  public boolean supportsSGR()
  {
    return false;
  }
  
  public boolean supportsScrolling()
  {
    return true;
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.terminal.Windoof

 * JD-Core Version:    0.7.0.1

 */