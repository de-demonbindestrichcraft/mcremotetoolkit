package net2.wimpi.telnetd.io.toolkit;

import java.io.IOException;
import java.util.Vector;
import net2.wimpi.telnetd.io.BasicTerminalIO;

public class Editarea
  extends ActiveComponent
{
  private int m_ColCursor = 0;
  private int m_RowCursor = 0;
  private int m_Rows = 0;
  private boolean m_Firstrun = true;
  private int m_FirstVisibleRow = 0;
  private int m_LastCursor = 0;
  private String m_Hardwrap = "\n";
  private String m_Softwrap = " ";
  private Vector lines = new Vector();
  private Editline line;
  
  public Editarea(BasicTerminalIO paramBasicTerminalIO, String paramString, int paramInt1, int paramInt2)
  {
    super(paramBasicTerminalIO, paramString);
    this.m_Rows = paramInt2;
    this.m_Firstrun = true;
    this.m_FirstVisibleRow = 0;
    setDimension(new Dimension(this.m_IO.getColumns(), paramInt1));
  }
  
  public int getSize()
  {
    int i = 0;
    return i;
  }
  
  public void setHardwrapString(String paramString)
  {
    this.m_Hardwrap = paramString;
  }
  
  public String getHardwrapString()
  {
    return this.m_Hardwrap;
  }
  
  public void setSoftwrapString(String paramString)
  {
    this.m_Softwrap = paramString;
  }
  
  public String getSoftwrapString()
  {
    return this.m_Softwrap;
  }
  
  public String getValue()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Editline localEditline = null;
    for (int i = 0; i < this.lines.size(); i++)
    {
      localEditline = getLine(i);
      localStringBuffer.append(localEditline.getValue()).append(localEditline.isHardwrapped() ? this.m_Hardwrap : this.m_Softwrap);
    }
    return localStringBuffer.toString();
  }
  
  public void setValue(String paramString)
    throws BufferOverflowException
  {
    this.lines.removeAllElements();
    this.m_RowCursor = 0;
    this.m_ColCursor = 0;
  }
  
  public void clear()
    throws IOException
  {
    this.lines.removeAllElements();
    this.m_RowCursor = 0;
    this.m_ColCursor = 0;
    draw();
  }
  
  public void run()
    throws IOException
  {
    int i = 0;
    int j = 0;
    this.m_IO.setAutoflushing(false);
    if (this.m_Firstrun)
    {
      this.m_Firstrun = false;
      this.line = createLine();
      appendLine(this.line);
    }
    do
    {
      String str;
      switch (this.line.run())
      {
      case 1001: 
        if (this.m_RowCursor > 0)
        {
          if (this.m_FirstVisibleRow == this.m_RowCursor) {
            scrollUp();
          } else {
            cursorUp();
          }
        }
        else {
          this.m_IO.bell();
        }
        break;
      case 1002: 
        if (this.m_RowCursor < this.lines.size() - 1)
        {
          if (this.m_RowCursor == this.m_FirstVisibleRow + (this.m_Dim.getHeight() - 1)) {
            scrollDown();
          } else {
            cursorDown();
          }
        }
        else {
          this.m_IO.bell();
        }
        break;
      case 10: 
        if (this.m_RowCursor == this.m_Rows - 1)
        {
          j = 1;
        }
        else if (!hasLineSpace())
        {
          this.m_IO.bell();
        }
        else
        {
          str = this.line.getHardwrap();
          this.line.setHardwrapped(true);
          if (this.m_RowCursor == this.lines.size() - 1) {
            appendNewLine();
          } else {
            insertNewLine();
          }
          this.m_RowCursor += 1;
          activateLine(this.m_RowCursor);
          try
          {
            this.line.setValue(str);
            this.line.setCursorPosition(0);
            this.m_IO.moveLeft(this.line.size());
          }
          catch (Exception localException1) {}
        }
        break;
      case 1301: 
        j = 1;
        break;
      case 1004: 
        if (this.m_RowCursor > 0)
        {
          if (this.m_FirstVisibleRow == this.m_RowCursor)
          {
            scrollUp();
            this.line.setCursorPosition(this.line.size());
            this.m_IO.moveRight(this.line.size());
          }
          else
          {
            this.m_RowCursor -= 1;
            activateLine(this.m_RowCursor);
            this.line.setCursorPosition(this.line.size());
            this.m_IO.moveUp(1);
            this.m_IO.moveRight(this.line.size());
          }
        }
        else {
          this.m_IO.bell();
        }
        break;
      case 1003: 
        if (this.m_RowCursor < this.lines.size() - 1)
        {
          if (this.m_RowCursor == this.m_FirstVisibleRow + (this.m_Dim.getHeight() - 1))
          {
            this.line.setCursorPosition(0);
            this.m_IO.moveLeft(this.line.size());
            scrollDown();
          }
          else
          {
            this.m_RowCursor += 1;
            this.m_IO.moveLeft(this.line.size());
            activateLine(this.m_RowCursor);
            this.line.setCursorPosition(0);
            this.m_IO.moveDown(1);
          }
        }
        else {
          this.m_IO.bell();
        }
        break;
      case 1303: 
        if ((this.m_RowCursor == 0) || (this.line.size() != 0) || (this.m_RowCursor == this.m_FirstVisibleRow)) {
          this.m_IO.bell();
        } else {
          removeLine();
        }
        break;
      default: 
        if (!hasLineSpace())
        {
          this.m_IO.bell();
        }
        else
        {
          str = this.line.getSoftwrap();
          this.line.setHardwrapped(false);
          if (this.m_RowCursor == this.lines.size() - 1) {
            appendNewLine();
          } else {
            insertNewLine();
          }
          this.m_RowCursor += 1;
          activateLine(this.m_RowCursor);
          try
          {
            this.line.setValue(str);
          }
          catch (Exception localException2) {}
        }
        break;
      }
      this.m_IO.flush();
    } while (j == 0);
  }
  
  private void scrollUp()
    throws IOException
  {
    int i = this.line.getCursorPosition();
    this.m_FirstVisibleRow -= 1;
    this.m_RowCursor -= 1;
    activateLine(this.m_RowCursor);
    this.line.setCursorPosition(i);
    int j = i;
    int k = 0;
    for (int m = this.m_FirstVisibleRow; (m < this.m_FirstVisibleRow + this.m_Dim.getHeight()) && (m < this.lines.size()); m++)
    {
      this.m_IO.moveLeft(j);
      Editline localEditline = (Editline)this.lines.elementAt(m);
      j = localEditline.size();
      this.m_IO.eraseToEndOfLine();
      this.m_IO.write(localEditline.getValue());
      this.m_IO.moveDown(1);
      k++;
    }
    this.m_IO.moveUp(k);
    if (j > i) {
      this.m_IO.moveLeft(j - i);
    } else if (j < i) {
      this.m_IO.moveRight(i - j);
    }
    if (i > this.line.getCursorPosition()) {
      this.m_IO.moveLeft(i - this.line.getCursorPosition());
    }
  }
  
  private void cursorUp()
    throws IOException
  {
    int i = this.line.getCursorPosition();
    this.m_RowCursor -= 1;
    activateLine(this.m_RowCursor);
    this.line.setCursorPosition(i);
    this.m_IO.moveUp(1);
    if (i > this.line.getCursorPosition()) {
      this.m_IO.moveLeft(i - this.line.getCursorPosition());
    }
  }
  
  private void scrollDown()
    throws IOException
  {
    int i = this.line.getCursorPosition();
    this.m_FirstVisibleRow += 1;
    this.m_RowCursor += 1;
    activateLine(this.m_RowCursor);
    this.line.setCursorPosition(i);
    this.m_IO.moveUp(this.m_Dim.getHeight() - 1);
    int j = i;
    for (int k = this.m_FirstVisibleRow; k < this.m_FirstVisibleRow + this.m_Dim.getHeight(); k++)
    {
      this.m_IO.moveLeft(j);
      Editline localEditline = (Editline)this.lines.elementAt(k);
      j = localEditline.size();
      this.m_IO.eraseToEndOfLine();
      this.m_IO.write(localEditline.getValue());
      this.m_IO.moveDown(1);
    }
    this.m_IO.moveUp(1);
    if (j > i) {
      this.m_IO.moveLeft(j - i);
    } else if (j < i) {
      this.m_IO.moveRight(i - j);
    }
    if (i > this.line.getCursorPosition()) {
      this.m_IO.moveLeft(i - this.line.getCursorPosition());
    }
  }
  
  private void cursorDown()
    throws IOException
  {
    int i = this.line.getCursorPosition();
    this.m_RowCursor += 1;
    activateLine(this.m_RowCursor);
    this.line.setCursorPosition(i);
    this.m_IO.moveDown(1);
    if (i > this.line.getCursorPosition()) {
      this.m_IO.moveLeft(i - this.line.getCursorPosition());
    }
  }
  
  private void appendNewLine()
    throws IOException
  {
    appendLine(createLine());
    if (this.m_RowCursor == this.m_FirstVisibleRow + (this.m_Dim.getHeight() - 1))
    {
      this.m_FirstVisibleRow += 1;
      this.m_IO.moveUp(this.m_Dim.getHeight() - 1);
      this.m_IO.moveLeft(this.line.getCursorPosition());
      int i = this.line.getCursorPosition();
      for (int j = this.m_FirstVisibleRow; j < this.m_FirstVisibleRow + this.m_Dim.getHeight(); j++)
      {
        Editline localEditline = (Editline)this.lines.elementAt(j);
        this.m_IO.eraseToEndOfLine();
        this.m_IO.write(localEditline.getValue());
        this.m_IO.moveLeft(localEditline.size());
        this.m_IO.moveDown(1);
      }
      this.m_IO.moveUp(1);
    }
    else
    {
      this.m_IO.moveLeft(this.line.getCursorPosition());
      this.m_IO.moveDown(1);
    }
  }
  
  private void insertNewLine()
    throws IOException
  {
    insertLine(this.m_RowCursor + 1, createLine());
    int i;
    int j;
    Editline localEditline;
    if (this.m_RowCursor == this.m_FirstVisibleRow + (this.m_Dim.getHeight() - 1))
    {
      this.m_FirstVisibleRow += 1;
      this.m_IO.moveUp(this.m_Dim.getHeight() - 1);
      i = this.line.getCursorPosition();
      for (j = this.m_FirstVisibleRow; j < this.m_FirstVisibleRow + this.m_Dim.getHeight(); j++)
      {
        this.m_IO.moveLeft(i);
        localEditline = (Editline)this.lines.elementAt(j);
        i = localEditline.size();
        this.m_IO.eraseToEndOfLine();
        this.m_IO.write(localEditline.getValue());
        this.m_IO.moveDown(1);
      }
      this.m_IO.moveUp(1);
    }
    else
    {
      this.m_IO.moveDown(1);
      this.m_IO.moveLeft(this.line.getCursorPosition());
      i = 0;
      for (j = this.m_RowCursor + 1; (j < this.m_FirstVisibleRow + this.m_Dim.getHeight()) && (j < this.lines.size()); j++)
      {
        this.m_IO.eraseToEndOfLine();
        localEditline = (Editline)this.lines.elementAt(j);
        this.m_IO.write(localEditline.getValue());
        this.m_IO.moveLeft(localEditline.size());
        this.m_IO.moveDown(1);
        i++;
      }
      this.m_IO.moveUp(i);
    }
  }
  
  private void removeLine()
    throws IOException
  {
    deleteLine(this.m_RowCursor);
    activateLine(this.m_RowCursor - 1);
    this.m_RowCursor -= 1;
    int i = 0;
    int j = 0;
    for (int k = this.m_RowCursor + 1; k < this.m_FirstVisibleRow + this.m_Dim.getHeight(); k++) {
      if (k < this.lines.size())
      {
        this.m_IO.eraseToEndOfLine();
        Editline localEditline = (Editline)this.lines.elementAt(k);
        this.m_IO.write(localEditline.getValue());
        this.m_IO.moveLeft(localEditline.size());
        this.m_IO.moveDown(1);
        j++;
      }
      else
      {
        this.m_IO.eraseToEndOfLine();
        this.m_IO.moveDown(1);
        j++;
      }
    }
    this.m_IO.moveUp(j + 1);
    this.line.setCursorPosition(this.line.size());
    this.m_IO.moveRight(this.line.size());
  }
  
  public void draw()
    throws IOException
  {
    if (this.m_Position != null)
    {
      this.m_IO.setCursor(this.m_Position.getRow(), this.m_Position.getColumn());
      int i = 0;
      for (int j = this.m_FirstVisibleRow; (j < this.m_FirstVisibleRow + this.m_Dim.getHeight()) && (j < this.lines.size()); j++)
      {
        this.m_IO.eraseToEndOfLine();
        Editline localEditline = (Editline)this.lines.elementAt(j);
        this.m_IO.write(localEditline.getValue());
        this.m_IO.moveLeft(localEditline.size());
        this.m_IO.moveDown(1);
        i++;
      }
      int j = this.m_FirstVisibleRow + i - this.m_RowCursor;
      if (j > 0) {
        this.m_IO.moveUp(j);
      }
    }
    this.m_IO.flush();
  }
  
  private void activateLine(int paramInt)
  {
    this.line = getLine(paramInt);
  }
  
  private boolean hasLineSpace()
  {
    return this.lines.size() < this.m_Rows;
  }
  
  private Editline createLine()
  {
    return new Editline(this.m_IO);
  }
  
  private void deleteLine(int paramInt)
  {
    this.lines.removeElementAt(paramInt);
  }
  
  private void insertLine(int paramInt, Editline paramEditline)
  {
    this.lines.insertElementAt(paramEditline, paramInt);
  }
  
  private void appendLine(Editline paramEditline)
  {
    this.lines.addElement(paramEditline);
  }
  
  private Editline getLine(int paramInt)
  {
    return (Editline)this.lines.elementAt(paramInt);
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.Editarea

 * JD-Core Version:    0.7.0.1

 */