package net2.wimpi.telnetd.io.toolkit;

import java.io.IOException;
import net2.wimpi.telnetd.io.BasicTerminalIO;

public class Checkbox
  extends ActiveComponent
{
  private String m_Text = "";
  private boolean m_Selected = false;
  private String m_Mark;
  private String m_LeftBracket;
  private String m_RightBracket;
  public static final int SMALL_CHECKMARK = 10;
  public static final int LARGE_CHECKMARK = 11;
  public static final int SQUARED_BOXSTYLE = 1;
  public static final int ROUND_BOXSTYLE = 2;
  public static final int EDGED_BOXSTYLE = 3;
  private static final int SPACE = 32;
  
  public Checkbox(BasicTerminalIO paramBasicTerminalIO, String paramString)
  {
    super(paramBasicTerminalIO, paramString);
    setBoxStyle(1);
    setMarkStyle(11);
  }
  
  public void setSelected(boolean paramBoolean)
    throws IOException
  {
    this.m_Selected = paramBoolean;
    drawMark();
  }
  
  public boolean isSelected()
  {
    return this.m_Selected;
  }
  
  public void setText(String paramString)
  {
    this.m_Text = paramString;
  }
  
  public void setBoxStyle(int paramInt)
  {
    switch (paramInt)
    {
    case 2: 
      this.m_LeftBracket = "(";
      this.m_RightBracket = ")";
      break;
    case 3: 
      this.m_LeftBracket = "<";
      this.m_RightBracket = ">";
      break;
    case 1: 
    default: 
      this.m_LeftBracket = "[";
      this.m_RightBracket = "]";
    }
  }
  
  public void setMarkStyle(int paramInt)
  {
    switch (paramInt)
    {
    case 10: 
      this.m_Mark = "x";
      break;
    case 11: 
    default: 
      this.m_Mark = "X";
    }
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
      case 32: 
        setSelected(!this.m_Selected);
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
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(" ");
    localStringBuffer.append(this.m_LeftBracket);
    if (this.m_Selected) {
      localStringBuffer.append(this.m_Mark);
    } else {
      localStringBuffer.append(" ");
    }
    localStringBuffer.append(this.m_RightBracket);
    localStringBuffer.append(" ");
    localStringBuffer.append(this.m_Text);
    if (this.m_Position != null) {
      this.m_IO.setCursor(this.m_Position.getRow(), this.m_Position.getColumn());
    }
    this.m_IO.write(localStringBuffer.toString());
    this.m_IO.moveLeft(3 + this.m_Text.length());
    this.m_IO.flush();
  }
  
  private void drawMark()
    throws IOException
  {
    if (this.m_Position != null)
    {
      this.m_IO.storeCursor();
      this.m_IO.setCursor(this.m_Position.getRow(), this.m_Position.getColumn());
      this.m_IO.moveRight(2);
    }
    if (this.m_Selected) {
      this.m_IO.write(this.m_Mark);
    } else {
      this.m_IO.write(" ");
    }
    if (this.m_Position == null) {
      this.m_IO.moveLeft(1);
    } else {
      this.m_IO.restoreCursor();
    }
    this.m_IO.flush();
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.Checkbox

 * JD-Core Version:    0.7.0.1

 */