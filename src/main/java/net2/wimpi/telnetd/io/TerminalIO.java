package net2.wimpi.telnetd.io;

import java.io.IOException;
import net2.wimpi.telnetd.io.terminal.Terminal;
import net2.wimpi.telnetd.io.terminal.TerminalManager;
import net2.wimpi.telnetd.net.Connection;
import net2.wimpi.telnetd.net.ConnectionData;
import net2.wimpi.telnetd.net.ConnectionEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TerminalIO
  implements BasicTerminalIO
{
  private static Log log = LogFactory.getLog(TerminalIO.class);
  private TelnetIO m_TelnetIO;
  private Connection m_Connection;
  private ConnectionData m_ConnectionData;
  private Terminal m_Terminal;
  private boolean m_AcousticSignalling;
  private boolean m_Autoflush;
  private boolean m_ForceBold;
  private boolean m_LineWrapping;
  public static final int[] HOME = { 0, 0 };
  public static final int IOERROR = -1;
  public static final int UP = 1001;
  public static final int DOWN = 1002;
  public static final int RIGHT = 1003;
  public static final int LEFT = 1004;
  public static final int STORECURSOR = 1051;
  public static final int RESTORECURSOR = 1052;
  public static final int EEOL = 1100;
  public static final int EBOL = 1101;
  public static final int EEL = 1103;
  public static final int EEOS = 1104;
  public static final int EBOS = 1105;
  public static final int EES = 1106;
  public static final int ESCAPE = 1200;
  public static final int BYTEMISSING = 1201;
  public static final int UNRECOGNIZED = 1202;
  public static final int ENTER = 1300;
  public static final int TABULATOR = 1301;
  public static final int DELETE = 1302;
  public static final int BACKSPACE = 1303;
  public static final int COLORINIT = 1304;
  public static final int HANDLED = 1305;
  public static final int LOGOUTREQUEST = 1306;
  public static final int LineUpdate = 475;
  public static final int CharacterUpdate = 476;
  public static final int ScreenpartUpdate = 477;
  public static final int EditBuffer = 575;
  public static final int LineEditBuffer = 576;
  public static final int BEL = 7;
  public static final int BS = 8;
  public static final int DEL = 127;
  public static final int CR = 13;
  public static final int LF = 10;
  public static final int FCOLOR = 10001;
  public static final int BCOLOR = 10002;
  public static final int STYLE = 10003;
  public static final int RESET = 10004;
  public static final int BOLD = 1;
  public static final int BOLD_OFF = 22;
  public static final int ITALIC = 3;
  public static final int ITALIC_OFF = 23;
  public static final int BLINK = 5;
  public static final int BLINK_OFF = 25;
  public static final int UNDERLINED = 4;
  public static final int UNDERLINED_OFF = 24;
  public static final int DEVICERESET = 10005;
  public static final int LINEWRAP = 10006;
  public static final int NOLINEWRAP = 10007;
  
  public TerminalIO(Connection paramConnection)
  {
    this.m_Connection = paramConnection;
    this.m_AcousticSignalling = true;
    this.m_Autoflush = true;
    this.m_ConnectionData = this.m_Connection.getConnectionData();
    try
    {
      this.m_TelnetIO = new TelnetIO();
      this.m_TelnetIO.setConnection(paramConnection);
      this.m_TelnetIO.initIO();
    }
    catch (Exception localException1) {}
    try
    {
      setDefaultTerminal();
    }
    catch (Exception localException2)
    {
      log.error("TerminalIO()", localException2);
      throw new RuntimeException();
    }
  }
  
  public int read()
    throws IOException
  {
    int i = this.m_TelnetIO.read();
    i = this.m_Terminal.translateControlCharacter(i);
    if (i == 1306)
    {
      this.m_Connection.processConnectionEvent(new ConnectionEvent(this.m_Connection, 102));
      i = 1305;
    }
    else if ((i > 256) && (i == 1200))
    {
      i = handleEscapeSequence(i);
    }
    return i;
  }
  
  public void write(byte paramByte)
    throws IOException
  {
    this.m_TelnetIO.write(paramByte);
    if (this.m_Autoflush) {
      flush();
    }
  }
  
  public void write(char paramChar)
    throws IOException
  {
    this.m_TelnetIO.write(paramChar);
    if (this.m_Autoflush) {
      flush();
    }
  }
  
  public void write(String paramString)
    throws IOException
  {
    if (this.m_ForceBold) {
      this.m_TelnetIO.write(this.m_Terminal.formatBold(paramString));
    } else {
      this.m_TelnetIO.write(this.m_Terminal.format(paramString));
    }
    if (this.m_Autoflush) {
      flush();
    }
  }
  
  public void eraseToEndOfLine()
    throws IOException
  {
    doErase(1100);
  }
  
  public void eraseToBeginOfLine()
    throws IOException
  {
    doErase(1101);
  }
  
  public void eraseLine()
    throws IOException
  {
    doErase(1103);
  }
  
  public void eraseToEndOfScreen()
    throws IOException
  {
    doErase(1104);
  }
  
  public void eraseToBeginOfScreen()
    throws IOException
  {
    doErase(1105);
  }
  
  public void eraseScreen()
    throws IOException
  {
    doErase(1106);
  }
  
  private void doErase(int paramInt)
    throws IOException
  {
    this.m_TelnetIO.write(this.m_Terminal.getEraseSequence(paramInt));
    if (this.m_Autoflush) {
      flush();
    }
  }
  
  public void moveCursor(int paramInt1, int paramInt2)
    throws IOException
  {
    this.m_TelnetIO.write(this.m_Terminal.getCursorMoveSequence(paramInt1, paramInt2));
    if (this.m_Autoflush) {
      flush();
    }
  }
  
  public synchronized void moveLeft(int paramInt)
    throws IOException
  {
    moveCursor(1004, paramInt);
  }
  
  public synchronized void moveRight(int paramInt)
    throws IOException
  {
    moveCursor(1003, paramInt);
  }
  
  public synchronized void moveUp(int paramInt)
    throws IOException
  {
    moveCursor(1001, paramInt);
  }
  
  public synchronized void moveDown(int paramInt)
    throws IOException
  {
    moveCursor(1002, paramInt);
  }
  
  public synchronized void setCursor(int paramInt1, int paramInt2)
    throws IOException
  {
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = paramInt1;
    arrayOfInt[1] = paramInt2;
    this.m_TelnetIO.write(this.m_Terminal.getCursorPositioningSequence(arrayOfInt));
    if (this.m_Autoflush) {
      flush();
    }
  }
  
  public synchronized void homeCursor()
    throws IOException
  {
    this.m_TelnetIO.write(this.m_Terminal.getCursorPositioningSequence(HOME));
    if (this.m_Autoflush) {
      flush();
    }
  }
  
  public synchronized void storeCursor()
    throws IOException
  {
    this.m_TelnetIO.write(this.m_Terminal.getSpecialSequence(1051));
  }
  
  public synchronized void restoreCursor()
    throws IOException
  {
    this.m_TelnetIO.write(this.m_Terminal.getSpecialSequence(1052));
  }
  
  public synchronized void setSignalling(boolean paramBoolean)
  {
    this.m_AcousticSignalling = paramBoolean;
  }
  
  public synchronized boolean isSignalling()
  {
    return this.m_AcousticSignalling;
  }
  
  public synchronized void bell()
    throws IOException
  {
    if (this.m_AcousticSignalling) {
      this.m_TelnetIO.write(7);
    }
    if (this.m_Autoflush) {
      flush();
    }
  }
  
  public synchronized boolean defineScrollRegion(int paramInt1, int paramInt2)
    throws IOException
  {
    if (this.m_Terminal.supportsScrolling())
    {
      this.m_TelnetIO.write(this.m_Terminal.getScrollMarginsSequence(paramInt1, paramInt2));
      flush();
      return true;
    }
    return false;
  }
  
  public synchronized void setForegroundColor(int paramInt)
    throws IOException
  {
    if (this.m_Terminal.supportsSGR())
    {
      this.m_TelnetIO.write(this.m_Terminal.getGRSequence(10001, paramInt));
      if (this.m_Autoflush) {
        flush();
      }
    }
  }
  
  public synchronized void setBackgroundColor(int paramInt)
    throws IOException
  {
    if (this.m_Terminal.supportsSGR())
    {
      this.m_TelnetIO.write(this.m_Terminal.getGRSequence(10002, paramInt + 10));
      if (this.m_Autoflush) {
        flush();
      }
    }
  }
  
  public synchronized void setBold(boolean paramBoolean)
    throws IOException
  {
    if (this.m_Terminal.supportsSGR())
    {
      if (paramBoolean) {
        this.m_TelnetIO.write(this.m_Terminal.getGRSequence(10003, 1));
      } else {
        this.m_TelnetIO.write(this.m_Terminal.getGRSequence(10003, 22));
      }
      if (this.m_Autoflush) {
        flush();
      }
    }
  }
  
  public synchronized void forceBold(boolean paramBoolean)
  {
    this.m_ForceBold = paramBoolean;
  }
  
  public synchronized void setUnderlined(boolean paramBoolean)
    throws IOException
  {
    if (this.m_Terminal.supportsSGR())
    {
      if (paramBoolean) {
        this.m_TelnetIO.write(this.m_Terminal.getGRSequence(10003, 4));
      } else {
        this.m_TelnetIO.write(this.m_Terminal.getGRSequence(10003, 24));
      }
      if (this.m_Autoflush) {
        flush();
      }
    }
  }
  
  public synchronized void setItalic(boolean paramBoolean)
    throws IOException
  {
    if (this.m_Terminal.supportsSGR())
    {
      if (paramBoolean) {
        this.m_TelnetIO.write(this.m_Terminal.getGRSequence(10003, 3));
      } else {
        this.m_TelnetIO.write(this.m_Terminal.getGRSequence(10003, 23));
      }
      if (this.m_Autoflush) {
        flush();
      }
    }
  }
  
  public synchronized void setBlink(boolean paramBoolean)
    throws IOException
  {
    if (this.m_Terminal.supportsSGR())
    {
      if (paramBoolean) {
        this.m_TelnetIO.write(this.m_Terminal.getGRSequence(10003, 5));
      } else {
        this.m_TelnetIO.write(this.m_Terminal.getGRSequence(10003, 25));
      }
      if (this.m_Autoflush) {
        flush();
      }
    }
  }
  
  public synchronized void resetAttributes()
    throws IOException
  {
    if (this.m_Terminal.supportsSGR()) {
      this.m_TelnetIO.write(this.m_Terminal.getGRSequence(10004, 0));
    }
  }
  
  private int handleEscapeSequence(int paramInt)
    throws IOException
  {
    if (paramInt == 1200)
    {
      int[] arrayOfInt = new int[this.m_Terminal.getAtomicSequenceLength()];
      for (int i = 0; i < arrayOfInt.length; i++) {
        arrayOfInt[i] = this.m_TelnetIO.read();
      }
      return this.m_Terminal.translateEscapeSequence(arrayOfInt);
    }
    if (paramInt == 1201) {}
    return 1305;
  }
  
  public boolean isAutoflushing()
  {
    return this.m_Autoflush;
  }
  
  public synchronized void resetTerminal()
    throws IOException
  {
    this.m_TelnetIO.write(this.m_Terminal.getSpecialSequence(10005));
  }
  
  public synchronized void setLinewrapping(boolean paramBoolean)
    throws IOException
  {
    if ((paramBoolean) && (!this.m_LineWrapping))
    {
      this.m_TelnetIO.write(this.m_Terminal.getSpecialSequence(10006));
      this.m_LineWrapping = true;
      return;
    }
    if ((!paramBoolean) && (this.m_LineWrapping))
    {
      this.m_TelnetIO.write(this.m_Terminal.getSpecialSequence(10007));
      this.m_LineWrapping = false;
      return;
    }
  }
  
  public boolean isLineWrapping()
  {
    return this.m_LineWrapping;
  }
  
  public synchronized void setAutoflushing(boolean paramBoolean)
  {
    this.m_Autoflush = paramBoolean;
  }
  
  public synchronized void flush()
    throws IOException
  {
    this.m_TelnetIO.flush();
  }
  
  public synchronized void close()
  {
    this.m_TelnetIO.closeOutput();
    this.m_TelnetIO.closeInput();
  }
  
  public Terminal getTerminal()
  {
    return this.m_Terminal;
  }
  
  public void setDefaultTerminal()
    throws IOException
  {
    setTerminal(this.m_ConnectionData.getNegotiatedTerminalType());
  }
  
  public void setTerminal(String paramString)
    throws IOException
  {
    this.m_Terminal = TerminalManager.getReference().getTerminal(paramString);
    initTerminal();
    log.debug("Set terminal to " + this.m_Terminal.toString());
  }
  
  private synchronized void initTerminal()
    throws IOException
  {
    this.m_TelnetIO.write(this.m_Terminal.getInitSequence());
    flush();
  }
  
  public int getRows()
  {
    return this.m_ConnectionData.getTerminalRows();
  }
  
  public int getColumns()
  {
    return this.m_ConnectionData.getTerminalColumns();
  }
  
  public boolean isTerminalGeometryChanged()
  {
    return this.m_ConnectionData.isTerminalGeometryChanged();
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.TerminalIO

 * JD-Core Version:    0.7.0.1

 */