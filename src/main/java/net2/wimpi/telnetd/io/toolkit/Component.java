package net2.wimpi.telnetd.io.toolkit;

import java.io.IOException;
import net2.wimpi.telnetd.io.BasicTerminalIO;

public abstract class Component
{
  protected String m_Name;
  protected BasicTerminalIO m_IO;
  protected Point m_Position;
  protected Dimension m_Dim;
  
  public Component(BasicTerminalIO paramBasicTerminalIO, String paramString)
  {
    this.m_IO = paramBasicTerminalIO;
    this.m_Name = paramString;
  }
  
  public abstract void draw()
    throws IOException;
  
  public String getName()
  {
    return this.m_Name;
  }
  
  public Point getLocation()
  {
    return this.m_Position;
  }
  
  public void setLocation(Point paramPoint)
  {
    this.m_Position = paramPoint;
  }
  
  public void setLocation(int paramInt1, int paramInt2)
  {
    if (this.m_Position != null)
    {
      this.m_Position.setColumn(paramInt1);
      this.m_Position.setRow(paramInt2);
    }
    else
    {
      this.m_Position = new Point(paramInt1, paramInt2);
    }
  }
  
  public Dimension getDimension()
  {
    return this.m_Dim;
  }
  
  protected void setDimension(Dimension paramDimension)
  {
    this.m_Dim = paramDimension;
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.Component

 * JD-Core Version:    0.7.0.1

 */