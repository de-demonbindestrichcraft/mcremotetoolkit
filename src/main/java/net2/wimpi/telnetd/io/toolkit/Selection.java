package net2.wimpi.telnetd.io.toolkit;

import java.io.IOException;
import java.util.Vector;
import net2.wimpi.telnetd.io.BasicTerminalIO;

public class Selection
  extends ActiveComponent
{
  private Vector m_Options = new Vector(10, 5);
  private int m_Selected = 0;
  private int m_LastSelected = 0;
  public static final int ALIGN_LEFT = 1;
  public static final int ALIGN_RIGHT = 2;
  
  public Selection(BasicTerminalIO paramBasicTerminalIO, String paramString)
  {
    super(paramBasicTerminalIO, paramString);
  }
  
  public void addOption(String paramString)
  {
    this.m_Options.addElement(paramString);
  }
  
  public void insertOption(String paramString, int paramInt)
  {
    try
    {
      this.m_Options.insertElementAt(paramString, paramInt);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      addOption(paramString);
    }
  }
  
  public void removeOption(String paramString)
  {
    for (int i = 0; i < this.m_Options.size(); i++) {
      if (((String)this.m_Options.elementAt(i)).equals(paramString))
      {
        removeOption(i);
        return;
      }
    }
  }
  
  public void removeOption(int paramInt)
  {
    try
    {
      this.m_Options.removeElementAt(paramInt);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
  }
  
  public String getOption(int paramInt)
  {
    try
    {
      Object localObject = this.m_Options.elementAt(paramInt);
      if (localObject != null) {
        return (String)localObject;
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return null;
  }
  
  public int getSelected()
  {
    return this.m_Selected;
  }
  
  public void setSelected(int paramInt)
    throws IOException
  {
    if ((paramInt < 0) || (paramInt > this.m_Options.size())) {
      return;
    }
    this.m_LastSelected = this.m_Selected;
    this.m_Selected = paramInt;
    draw();
  }
  
  public void run()
    throws IOException
  {
    int i = 0;
    draw();
    this.m_IO.flush();
    do
    {
      i = this.m_IO.read();
      switch (i)
      {
      case 1001: 
      case 1004: 
        if (!selectPrevious()) {
          this.m_IO.bell();
        }
        break;
      case 1002: 
      case 1003: 
        if (!selectNext()) {
          this.m_IO.bell();
        }
        break;
      case 10: 
      case 1301: 
        i = -1;
        break;
      default: 
        this.m_IO.bell();
      }
      this.m_IO.flush();
    } while (i != -1);
  }
  
  public void draw()
    throws IOException
  {
    String str = getOption(this.m_Selected);
    int i = getOption(this.m_LastSelected).length() - str.length();
    if (i > 0)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(str);
      for (int j = 0; j < i; j++) {
        localStringBuffer.append(" ");
      }
      str = localStringBuffer.toString();
    }
    if (this.m_Position != null) {
      this.m_IO.setCursor(this.m_Position.getRow(), this.m_Position.getColumn());
    }
    this.m_IO.write(str);
    this.m_IO.moveLeft(str.length());
  }
  
  private boolean selectNext()
    throws IOException
  {
    if (this.m_Selected < this.m_Options.size() - 1)
    {
      setSelected(this.m_Selected + 1);
      return true;
    }
    return false;
  }
  
  private boolean selectPrevious()
    throws IOException
  {
    if (this.m_Selected > 0)
    {
      setSelected(this.m_Selected - 1);
      return true;
    }
    return false;
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.Selection

 * JD-Core Version:    0.7.0.1

 */