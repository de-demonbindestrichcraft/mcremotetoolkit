package net2.wimpi.telnetd.io.toolkit;

import java.util.Vector;
import net2.wimpi.telnetd.io.BasicTerminalIO;

public class Form
  extends ActiveComponent
{
  protected Vector myComponents;
  
  public Form(BasicTerminalIO paramBasicTerminalIO, String paramString)
  {
    super(paramBasicTerminalIO, paramString);
    setLocation(new Point(0, 0));
    setDimension(new Dimension(this.m_IO.getColumns(), this.m_IO.getRows()));
  }
  
  public void run() {}
  
  public void draw() {}
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.Form

 * JD-Core Version:    0.7.0.1

 */