package net2.wimpi.telnetd.io.toolkit;

import java.io.IOException;
import net2.wimpi.telnetd.io.BasicTerminalIO;
import net2.wimpi.telnetd.io.terminal.ColorHelper;

public class Label
  extends InertComponent
{
  private String m_Content;
  
  public Label(BasicTerminalIO paramBasicTerminalIO, String paramString1, String paramString2)
  {
    super(paramBasicTerminalIO, paramString1);
    setText(paramString2);
  }
  
  public Label(BasicTerminalIO paramBasicTerminalIO, String paramString)
  {
    super(paramBasicTerminalIO, paramString);
    setText(paramString);
  }
  
  public void setText(String paramString)
  {
    this.m_Content = paramString;
    this.m_Dim = new Dimension((int)ColorHelper.getVisibleLength(paramString), 1);
  }
  
  public String getText()
  {
    return this.m_Content;
  }
  
  public void draw()
    throws IOException
  {
    if (this.m_Position == null)
    {
      this.m_IO.write(this.m_Content);
    }
    else
    {
      this.m_IO.storeCursor();
      this.m_IO.setCursor(this.m_Position.getRow(), this.m_Position.getColumn());
      this.m_IO.write(this.m_Content);
      this.m_IO.restoreCursor();
      this.m_IO.flush();
    }
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.Label

 * JD-Core Version:    0.7.0.1

 */