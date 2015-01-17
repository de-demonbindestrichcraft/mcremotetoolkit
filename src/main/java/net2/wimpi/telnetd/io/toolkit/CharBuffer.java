package net2.wimpi.telnetd.io.toolkit;

import java.util.Vector;

class CharBuffer
{
  private Vector m_Buffer;
  private int m_Size;
  
  public CharBuffer(int paramInt)
  {
    this.m_Buffer = new Vector(paramInt);
    this.m_Size = paramInt;
  }
  
  public char getCharAt(int paramInt)
    throws IndexOutOfBoundsException
  {
    return ((Character)this.m_Buffer.elementAt(paramInt)).charValue();
  }
  
  public void setCharAt(int paramInt, char paramChar)
    throws IndexOutOfBoundsException
  {
    this.m_Buffer.setElementAt(new Character(paramChar), paramInt);
  }
  
  public void insertCharAt(int paramInt, char paramChar)
    throws BufferOverflowException, IndexOutOfBoundsException
  {
    this.m_Buffer.insertElementAt(new Character(paramChar), paramInt);
  }
  
  public void append(char paramChar)
    throws BufferOverflowException
  {
    this.m_Buffer.addElement(new Character(paramChar));
  }
  
  public void append(String paramString)
    throws BufferOverflowException
  {
    for (int i = 0; i < paramString.length(); i++) {
      append(paramString.charAt(i));
    }
  }
  
  public void removeCharAt(int paramInt)
    throws IndexOutOfBoundsException
  {
    this.m_Buffer.removeElementAt(paramInt);
  }
  
  public void clear()
  {
    this.m_Buffer.removeAllElements();
  }
  
  public int size()
  {
    return this.m_Buffer.size();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < this.m_Buffer.size(); i++) {
      localStringBuffer.append(((Character)this.m_Buffer.elementAt(i)).charValue());
    }
    return localStringBuffer.toString();
  }
  
  public void ensureSpace(int paramInt)
    throws BufferOverflowException
  {
    if (paramInt > this.m_Size - this.m_Buffer.size()) {
      throw new BufferOverflowException();
    }
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.CharBuffer

 * JD-Core Version:    0.7.0.1

 */