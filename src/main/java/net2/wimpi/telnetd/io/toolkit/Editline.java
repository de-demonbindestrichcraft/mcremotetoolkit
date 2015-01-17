package net2.wimpi.telnetd.io.toolkit;

import java.io.IOException;
import net2.wimpi.telnetd.io.BasicTerminalIO;

class Editline
{
  private Buffer m_Buffer;
  private BasicTerminalIO m_IO;
  private int m_Cursor = 0;
  private boolean m_InsertMode = true;
  private int m_LastSize = 0;
  private boolean m_HardWrapped = false;
  private char m_LastRead;
  private int m_LastCursPos = 0;
  
  public Editline(BasicTerminalIO paramBasicTerminalIO)
  {
    this.m_IO = paramBasicTerminalIO;
    this.m_Buffer = new Buffer(this.m_IO.getColumns() - 1);
    this.m_Cursor = 0;
    this.m_InsertMode = true;
  }
  
  public int size()
  {
    return this.m_Buffer.size();
  }
  
  public String getValue()
  {
    return this.m_Buffer.toString();
  }
  
  public void setValue(String paramString)
    throws BufferOverflowException, IOException
  {
    storeSize();
    this.m_Buffer.clear();
    this.m_Cursor = 0;
    this.m_IO.moveLeft(this.m_LastSize);
    this.m_IO.eraseToEndOfLine();
    append(paramString);
  }
  
  public void clear()
    throws IOException
  {
    storeSize();
    this.m_Buffer.clear();
    this.m_Cursor = 0;
    draw();
  }
  
  public String getSoftwrap()
    throws IndexOutOfBoundsException, IOException
  {
    String str = this.m_Buffer.toString();
    int i = str.lastIndexOf(" ");
    if (i == -1)
    {
      str = "";
    }
    else
    {
      str = str.substring(i + 1, str.length());
      this.m_Cursor = size();
      this.m_Cursor -= str.length();
      for (int j = 0; j < str.length(); j++) {
        this.m_Buffer.removeCharAt(this.m_Cursor);
      }
      this.m_IO.moveLeft(str.length());
      this.m_IO.eraseToEndOfLine();
    }
    return str + getLastRead();
  }
  
  public String getHardwrap()
    throws IndexOutOfBoundsException, IOException
  {
    String str = this.m_Buffer.toString();
    str = str.substring(this.m_Cursor, str.length());
    int i = this.m_Buffer.size();
    for (int j = this.m_Cursor; j < i; j++) {
      this.m_Buffer.removeCharAt(this.m_Cursor);
    }
    this.m_IO.eraseToEndOfLine();
    return str;
  }
  
  private void setCharAt(int paramInt, char paramChar)
    throws IndexOutOfBoundsException, IOException
  {
    this.m_Buffer.setCharAt(paramInt, paramChar);
    draw();
  }
  
  private void insertCharAt(int paramInt, char paramChar)
    throws BufferOverflowException, IndexOutOfBoundsException, IOException
  {
    storeSize();
    this.m_Buffer.ensureSpace(1);
    this.m_Buffer.insertCharAt(paramInt, paramChar);
    if (this.m_Cursor >= paramInt) {
      this.m_Cursor += 1;
    }
    draw();
  }
  
  private void removeCharAt(int paramInt)
    throws IndexOutOfBoundsException, IOException
  {
    storeSize();
    this.m_Buffer.removeCharAt(paramInt);
    if (this.m_Cursor > paramInt) {
      this.m_Cursor -= 1;
    }
    draw();
  }
  
  private void insertStringAt(int paramInt, String paramString)
    throws BufferOverflowException, IndexOutOfBoundsException, IOException
  {
    storeSize();
    this.m_Buffer.ensureSpace(paramString.length());
    for (int i = 0; i < paramString.length(); i++)
    {
      this.m_Buffer.insertCharAt(paramInt, paramString.charAt(i));
      this.m_Cursor += 1;
    }
    draw();
  }
  
  public void append(char paramChar)
    throws BufferOverflowException, IOException
  {
    storeSize();
    this.m_Buffer.ensureSpace(1);
    this.m_Buffer.append(paramChar);
    this.m_Cursor += 1;
    this.m_IO.write(paramChar);
  }
  
  public void append(String paramString)
    throws BufferOverflowException, IOException
  {
    storeSize();
    this.m_Buffer.ensureSpace(paramString.length());
    for (int i = 0; i < paramString.length(); i++)
    {
      this.m_Buffer.append(paramString.charAt(i));
      this.m_Cursor += 1;
    }
    this.m_IO.write(paramString);
  }
  
  public int getCursorPosition()
  {
    return this.m_Cursor;
  }
  
  public void setCursorPosition(int paramInt)
  {
    if (this.m_Buffer.size() < paramInt) {
      this.m_Cursor = this.m_Buffer.size();
    } else {
      this.m_Cursor = paramInt;
    }
  }
  
  private char getLastRead()
  {
    return this.m_LastRead;
  }
  
  private void setLastRead(char paramChar)
  {
    this.m_LastRead = paramChar;
  }
  
  public boolean isInInsertMode()
  {
    return this.m_InsertMode;
  }
  
  public void setInsertMode(boolean paramBoolean)
  {
    this.m_InsertMode = paramBoolean;
  }
  
  public boolean isHardwrapped()
  {
    return this.m_HardWrapped;
  }
  
  public void setHardwrapped(boolean paramBoolean)
  {
    this.m_HardWrapped = paramBoolean;
  }
  
  public int run()
    throws IOException
  {
    int i = 0;
    for (;;)
    {
      i = this.m_IO.read();
      this.m_LastCursPos = this.m_Cursor;
      switch (i)
      {
      case 1004: 
        if (!moveLeft()) {
          return i;
        }
        break;
      case 1003: 
        if (!moveRight()) {
          return i;
        }
        break;
      case 1303: 
        try
        {
          if (this.m_Cursor == 0) {
            return i;
          }
          removeCharAt(this.m_Cursor - 1);
        }
        catch (IndexOutOfBoundsException localIndexOutOfBoundsException1)
        {
          this.m_IO.bell();
        }
      case 1302: 
        try
        {
          removeCharAt(this.m_Cursor);
        }
        catch (IndexOutOfBoundsException localIndexOutOfBoundsException2)
        {
          this.m_IO.bell();
        }
      case 10: 
      case 1001: 
      case 1002: 
      case 1301: 
        return i;
      default: 
        try
        {
          handleCharInput(i);
        }
        catch (BufferOverflowException localBufferOverflowException)
        {
          setLastRead((char)i);
          return i;
        }
      }
      this.m_IO.flush();
    }
  }
  
  public void draw()
    throws IOException
  {
    this.m_IO.moveLeft(this.m_LastCursPos);
    this.m_IO.eraseToEndOfLine();
    this.m_IO.write(this.m_Buffer.toString());
    if (this.m_Cursor < this.m_Buffer.size()) {
      this.m_IO.moveLeft(this.m_Buffer.size() - this.m_Cursor);
    }
  }
  
  private boolean moveRight()
    throws IOException
  {
    if (this.m_Cursor < this.m_Buffer.size())
    {
      this.m_Cursor += 1;
      this.m_IO.moveRight(1);
      return true;
    }
    return false;
  }
  
  private boolean moveLeft()
    throws IOException
  {
    if (this.m_Cursor > 0)
    {
      this.m_Cursor -= 1;
      this.m_IO.moveLeft(1);
      return true;
    }
    return false;
  }
  
  private boolean isCursorAtEnd()
  {
    return this.m_Cursor == this.m_Buffer.size();
  }
  
  private void handleCharInput(int paramInt)
    throws BufferOverflowException, IOException
  {
    if (isCursorAtEnd()) {
      append((char)paramInt);
    } else if (isInInsertMode()) {
      try
      {
        insertCharAt(this.m_Cursor, (char)paramInt);
      }
      catch (BufferOverflowException localBufferOverflowException)
      {
        this.m_IO.bell();
      }
    } else {
      setCharAt(this.m_Cursor, (char)paramInt);
    }
  }
  
  private void storeSize()
  {
    this.m_LastSize = this.m_Buffer.size();
  }
  
  class Buffer
    extends CharBuffer
  {
    public Buffer(int paramInt)
    {
      super(paramInt);
    }
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.Editline

 * JD-Core Version:    0.7.0.1

 */