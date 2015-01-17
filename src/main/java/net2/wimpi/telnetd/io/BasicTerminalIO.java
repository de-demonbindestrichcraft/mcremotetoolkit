package net2.wimpi.telnetd.io;

import java.io.IOException;

public abstract interface BasicTerminalIO
{
  public static final int UP = 1001;
  public static final int DOWN = 1002;
  public static final int RIGHT = 1003;
  public static final int LEFT = 1004;
  public static final int TABULATOR = 1301;
  public static final int DELETE = 1302;
  public static final int BACKSPACE = 1303;
  public static final int ENTER = 10;
  public static final int COLORINIT = 1304;
  public static final int LOGOUTREQUEST = 1306;
  public static final int BLACK = 30;
  public static final int RED = 31;
  public static final int GREEN = 32;
  public static final int YELLOW = 33;
  public static final int BLUE = 34;
  public static final int MAGENTA = 35;
  public static final int CYAN = 36;
  public static final int WHITE = 37;
  public static final String CRLF = "\r\n";
  
  public abstract int read()
    throws IOException;
  
  public abstract void write(byte paramByte)
    throws IOException;
  
  public abstract void write(char paramChar)
    throws IOException;
  
  public abstract void write(String paramString)
    throws IOException;
  
  public abstract void setCursor(int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void moveCursor(int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void moveRight(int paramInt)
    throws IOException;
  
  public abstract void moveLeft(int paramInt)
    throws IOException;
  
  public abstract void moveUp(int paramInt)
    throws IOException;
  
  public abstract void moveDown(int paramInt)
    throws IOException;
  
  public abstract void homeCursor()
    throws IOException;
  
  public abstract void storeCursor()
    throws IOException;
  
  public abstract void restoreCursor()
    throws IOException;
  
  public abstract void eraseToEndOfLine()
    throws IOException;
  
  public abstract void eraseToBeginOfLine()
    throws IOException;
  
  public abstract void eraseLine()
    throws IOException;
  
  public abstract void eraseToEndOfScreen()
    throws IOException;
  
  public abstract void eraseToBeginOfScreen()
    throws IOException;
  
  public abstract void eraseScreen()
    throws IOException;
  
  public abstract void setForegroundColor(int paramInt)
    throws IOException;
  
  public abstract void setBackgroundColor(int paramInt)
    throws IOException;
  
  public abstract void setBold(boolean paramBoolean)
    throws IOException;
  
  public abstract void forceBold(boolean paramBoolean);
  
  public abstract void setItalic(boolean paramBoolean)
    throws IOException;
  
  public abstract void setUnderlined(boolean paramBoolean)
    throws IOException;
  
  public abstract void setBlink(boolean paramBoolean)
    throws IOException;
  
  public abstract void resetAttributes()
    throws IOException;
  
  public abstract void bell()
    throws IOException;
  
  public abstract void flush()
    throws IOException;
  
  public abstract void close()
    throws IOException;
  
  public abstract void setTerminal(String paramString)
    throws IOException;
  
  public abstract void setDefaultTerminal()
    throws IOException;
  
  public abstract int getRows();
  
  public abstract int getColumns();
  
  public abstract void setSignalling(boolean paramBoolean);
  
  public abstract boolean isSignalling();
  
  public abstract void setAutoflushing(boolean paramBoolean);
  
  public abstract boolean isAutoflushing();
  
  public abstract void resetTerminal()
    throws IOException;
  
  public abstract void setLinewrapping(boolean paramBoolean)
    throws IOException;
  
  public abstract boolean isLineWrapping()
    throws IOException;
  
  public abstract boolean defineScrollRegion(int paramInt1, int paramInt2)
    throws IOException;
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.BasicTerminalIO

 * JD-Core Version:    0.7.0.1

 */