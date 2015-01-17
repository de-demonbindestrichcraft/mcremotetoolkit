package net2.wimpi.telnetd.io.terminal;

public class vt100
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

 * Qualified Name:     net.wimpi.telnetd.io.terminal.vt100

 * JD-Core Version:    0.7.0.1

 */