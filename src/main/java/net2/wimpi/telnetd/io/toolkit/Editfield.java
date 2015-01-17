package net2.wimpi.telnetd.io.toolkit;

import java.io.IOException;
import net2.wimpi.telnetd.io.BasicTerminalIO;

public class Editfield
  extends ActiveComponent
{
  private InputFilter m_InputFilter = null;
  private InputValidator m_InputValidator = null;
  private Buffer m_Buffer;
  private int m_Cursor = 0;
  private boolean m_InsertMode = true;
  private int m_LastSize = 0;
  private boolean m_PasswordField = false;
  private boolean m_JustBackspace;
  
  public Editfield(BasicTerminalIO paramBasicTerminalIO, String paramString, int paramInt)
  {
    super(paramBasicTerminalIO, paramString);
    this.m_Buffer = new Buffer(paramInt);
    setDimension(new Dimension(paramInt, 1));
    this.m_Cursor = 0;
    this.m_InsertMode = true;
  }
  
  public int getLength()
  {
    return this.m_Dim.getWidth();
  }
  
  public int getSize()
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
    clear();
    append(paramString);
  }
  
  public void clear()
    throws IOException
  {
    positionCursorAtBegin();
    for (int i = 0; i < this.m_Buffer.size(); i++) {
      this.m_IO.write(' ');
    }
    positionCursorAtBegin();
    this.m_Buffer.clear();
    this.m_Cursor = 0;
    this.m_LastSize = 0;
    this.m_IO.flush();
  }
  
  public char getCharAt(int paramInt)
    throws IndexOutOfBoundsException
  {
    return this.m_Buffer.getCharAt(paramInt);
  }
  
  public void setCharAt(int paramInt, char paramChar)
    throws IndexOutOfBoundsException, IOException
  {
    this.m_Buffer.setCharAt(paramInt, paramChar);
    draw();
  }
  
  public void insertCharAt(int paramInt, char paramChar)
    throws BufferOverflowException, IndexOutOfBoundsException, IOException
  {
    storeSize();
    this.m_Buffer.ensureSpace(1);
    this.m_Buffer.insertCharAt(paramInt, paramChar);
    if (this.m_Cursor >= paramInt) {
      moveRight();
    }
    draw();
  }
  
  public void removeCharAt(int paramInt)
    throws IndexOutOfBoundsException, IOException
  {
    storeSize();
    this.m_Buffer.removeCharAt(paramInt);
    if (this.m_Cursor > paramInt) {
      moveLeft();
    }
    draw();
  }
  
  public void insertStringAt(int paramInt, String paramString)
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
    if (!this.m_PasswordField) {
      this.m_IO.write(paramChar);
    } else {
      this.m_IO.write('.');
    }
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
    if (!this.m_PasswordField)
    {
      this.m_IO.write(paramString);
    }
    else
    {
      StringBuffer localStringBuffer = new StringBuffer();
      for (int j = 0; j < paramString.length(); j++) {
        localStringBuffer.append('.');
      }
      this.m_IO.write(localStringBuffer.toString());
    }
  }
  
  public int getCursorPosition()
  {
    return this.m_Cursor;
  }
  
  public boolean isJustBackspace()
  {
    return this.m_JustBackspace;
  }
  
  public void setJustBackspace(boolean paramBoolean)
  {
    this.m_JustBackspace = true;
  }
  
  public void registerInputFilter(InputFilter paramInputFilter)
  {
    this.m_InputFilter = paramInputFilter;
  }
  
  public void registerInputValidator(InputValidator paramInputValidator)
  {
    this.m_InputValidator = paramInputValidator;
  }
  
  public boolean isInInsertMode()
  {
    return this.m_InsertMode;
  }
  
  public void setInsertMode(boolean paramBoolean)
  {
    this.m_InsertMode = paramBoolean;
  }
  
  public boolean isPasswordField()
  {
    return this.m_PasswordField;
  }
  
  public void setPasswordField(boolean paramBoolean)
  {
    this.m_PasswordField = paramBoolean;
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
      if ((this.m_JustBackspace) && (i == 1302)) {
        i = 1303;
      }
      if (this.m_InputFilter != null) {
        i = this.m_InputFilter.filterInput(i);
      }
      switch (i)
      {
      case -1: 
        this.m_Buffer.clear();
        break;
      case -2000: 
        break;
      case -2001: 
        this.m_IO.bell();
        break;
      case 1004: 
        moveLeft();
        break;
      case 1003: 
        moveRight();
        break;
      case 1001: 
      case 1002: 
        this.m_IO.bell();
        break;
      case 10: 
        if (this.m_InputValidator != null)
        {
          if (this.m_InputValidator.validate(this.m_Buffer.toString())) {
            i = -1;
          } else {
            this.m_IO.bell();
          }
        }
        else {
          i = -1;
        }
        break;
      case 1303: 
        try
        {
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
      case 1301: 
        i = -1;
        break;
      default: 
        handleCharInput(i);
      }
      this.m_IO.flush();
    } while (i != -1);
  }
  
  public void draw()
    throws IOException
  {
    int i = this.m_LastSize - this.m_Buffer.size();
    String str = this.m_Buffer.toString();
    StringBuffer localStringBuffer;
    int j;
    if (this.m_PasswordField)
    {
      localStringBuffer = new StringBuffer();
      for (j = 0; j < str.length(); j++) {
        localStringBuffer.append('.');
      }
      str = localStringBuffer.toString();
    }
    if (i > 0)
    {
      localStringBuffer = new StringBuffer();
      localStringBuffer.append(str);
      for (j = 0; j < i; j++) {
        localStringBuffer.append(" ");
      }
      str = localStringBuffer.toString();
    }
    if (this.m_Position != null) {
      this.m_IO.setCursor(this.m_Position.getRow(), this.m_Position.getColumn());
    } else {
      this.m_IO.moveLeft(this.m_Cursor);
    }
    this.m_IO.write(str);
    if (this.m_Cursor < str.length()) {
      this.m_IO.moveLeft(str.length() - this.m_Cursor);
    }
  }
  
  private void moveRight()
    throws IOException
  {
    if (this.m_Cursor < this.m_Buffer.size())
    {
      this.m_Cursor += 1;
      this.m_IO.moveRight(1);
    }
    else
    {
      this.m_IO.bell();
    }
  }
  
  private void moveLeft()
    throws IOException
  {
    if (this.m_Cursor > 0)
    {
      this.m_Cursor -= 1;
      this.m_IO.moveLeft(1);
    }
    else
    {
      this.m_IO.bell();
    }
  }
  
  private void positionCursorAtBegin()
    throws IOException
  {
    if (this.m_Position == null) {
      this.m_IO.moveLeft(this.m_Cursor);
    } else {
      this.m_IO.setCursor(this.m_Position.getRow(), this.m_Position.getColumn());
    }
  }
  
  private boolean isCursorAtEnd()
  {
    return this.m_Cursor == this.m_Buffer.size();
  }
  
  private void handleCharInput(int paramInt)
    throws IOException
  {
    if (isCursorAtEnd()) {
      try
      {
        append((char)paramInt);
      }
      catch (BufferOverflowException localBufferOverflowException1)
      {
        this.m_IO.bell();
      }
    } else if (isInInsertMode()) {
      try
      {
        insertCharAt(this.m_Cursor, (char)paramInt);
      }
      catch (BufferOverflowException localBufferOverflowException2)
      {
        this.m_IO.bell();
      }
    } else {
      try
      {
        setCharAt(this.m_Cursor, (char)paramInt);
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        this.m_IO.bell();
      }
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

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.Editfield

 * JD-Core Version:    0.7.0.1

 */