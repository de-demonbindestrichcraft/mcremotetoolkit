package net2.wimpi.telnetd.io.toolkit;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Vector;
import net2.wimpi.telnetd.io.BasicTerminalIO;

public class Pager
{
  private BasicTerminalIO m_IO;
  private StringReader m_Source;
  private String m_Prompt;
  private int m_StopKey;
  private Vector m_Chunks;
  private int m_ChunkPos;
  private int m_LastNewChunk;
  private boolean m_EOS;
  private int m_TermRows;
  private int m_TermCols;
  private boolean m_NoPrompt;
  private boolean m_ShowPos;
  private Statusbar m_Status;
  private static final char DEFAULT_STOPKEY = 's';
  private static final String DEFAULT_PROMPT = "[Cursor Up,Cursor Down,Space,s (stop)] ";
  private static final int SPACE = 32;
  
  public Pager(BasicTerminalIO paramBasicTerminalIO)
  {
    this.m_IO = paramBasicTerminalIO;
    setPrompt("[Cursor Up,Cursor Down,Space,s (stop)] ");
    setStopKey('s');
    this.m_TermRows = this.m_IO.getRows();
    this.m_TermCols = this.m_IO.getColumns();
    this.m_Status = new Statusbar(this.m_IO, "Pager Status");
    this.m_Status.setAlignment(2);
  }
  
  public Pager(BasicTerminalIO paramBasicTerminalIO, String paramString, char paramChar)
  {
    this.m_IO = paramBasicTerminalIO;
    setPrompt(paramString);
    this.m_StopKey = paramChar;
    this.m_TermRows = this.m_IO.getRows();
    this.m_TermCols = this.m_IO.getColumns();
    this.m_Status.setAlignment(2);
  }
  
  public void setStopKey(char paramChar)
  {
    this.m_StopKey = paramChar;
  }
  
  public void setPrompt(String paramString)
  {
    this.m_Prompt = paramString;
  }
  
  private void updateStatus()
  {
    if (this.m_ShowPos) {
      this.m_Status.setStatusText(this.m_Prompt + " [" + (this.m_ChunkPos + 1) + "/" + this.m_Chunks.size() + "]");
    } else {
      this.m_Status.setStatusText(this.m_Prompt);
    }
  }
  
  public void setShowPosition(boolean paramBoolean)
  {
    this.m_ShowPos = paramBoolean;
  }
  
  public void page(String paramString)
    throws IOException
  {
    terminalGeometryChanged();
    boolean bool = this.m_IO.isAutoflushing();
    this.m_IO.setAutoflushing(true);
    this.m_Source = new StringReader(paramString);
    this.m_ChunkPos = 0;
    this.m_LastNewChunk = 0;
    this.m_EOS = false;
    this.m_NoPrompt = false;
    renderChunks();
    if (this.m_Chunks.size() == 1)
    {
      this.m_IO.write((String)this.m_Chunks.elementAt(0));
    }
    else
    {
      this.m_IO.homeCursor();
      this.m_IO.eraseScreen();
      this.m_IO.write((String)this.m_Chunks.elementAt(this.m_ChunkPos));
      updateStatus();
      this.m_Status.draw();
      int i = 0;
      do
      {
        this.m_NoPrompt = false;
        i = this.m_IO.read();
        if (terminalGeometryChanged())
        {
          try
          {
            this.m_Source.reset();
          }
          catch (Exception localException) {}
          renderChunks();
          this.m_ChunkPos = 0;
          this.m_LastNewChunk = 0;
          this.m_EOS = false;
          this.m_NoPrompt = false;
          this.m_IO.homeCursor();
          this.m_IO.eraseScreen();
          this.m_IO.write((String)this.m_Chunks.elementAt(this.m_ChunkPos));
          updateStatus();
          this.m_Status.draw();
        }
        else
        {
          switch (i)
          {
          case 1001: 
            drawPreviousPage();
            break;
          case 1002: 
            drawNextPage();
            break;
          case 32: 
            drawNextPage();
            break;
          default: 
            if (i == this.m_StopKey) {
              i = -1;
            } else {
              this.m_IO.bell();
            }
            break;
          }
          if (this.m_EOS)
          {
            i = -1;
          }
          else if (!this.m_NoPrompt)
          {
            updateStatus();
            this.m_Status.draw();
          }
        }
      } while (i != -1);
      this.m_IO.eraseToEndOfLine();
    }
    this.m_IO.write("\n");
    this.m_Source.close();
    this.m_IO.setAutoflushing(bool);
  }
  
  public void page(InputStream paramInputStream)
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer(3060);
    int i = 0;
    while (i != -1)
    {
      i = paramInputStream.read();
      if (i != -1) {
        localStringBuffer.append((char)i);
      }
    }
    page(localStringBuffer.toString());
  }
  
  private void drawNextPage()
    throws IOException
  {
    if (this.m_ChunkPos == this.m_LastNewChunk)
    {
      drawNewPage();
    }
    else
    {
      this.m_IO.homeCursor();
      this.m_IO.eraseScreen();
      this.m_IO.write((String)this.m_Chunks.elementAt(++this.m_ChunkPos));
    }
  }
  
  private void drawPreviousPage()
    throws IOException
  {
    if (this.m_ChunkPos > 0)
    {
      this.m_IO.homeCursor();
      this.m_IO.eraseScreen();
      this.m_IO.write((String)this.m_Chunks.elementAt(--this.m_ChunkPos));
    }
    else
    {
      this.m_IO.bell();
      this.m_NoPrompt = true;
    }
  }
  
  private void drawNewPage()
    throws IOException
  {
    this.m_ChunkPos += 1;
    this.m_LastNewChunk += 1;
    if (this.m_ChunkPos < this.m_Chunks.size())
    {
      this.m_IO.homeCursor();
      this.m_IO.eraseScreen();
      this.m_IO.write((String)this.m_Chunks.elementAt(this.m_ChunkPos));
    }
    else
    {
      this.m_EOS = true;
      this.m_NoPrompt = true;
    }
  }
  
  private void renderChunks()
  {
    this.m_Chunks = new Vector(20);
    StringBuffer localStringBuffer = new StringBuffer((this.m_TermCols + 25) * 25);
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    do
    {
      if (k == this.m_TermRows - 1)
      {
        this.m_Chunks.addElement(localStringBuffer.toString());
        localStringBuffer = new StringBuffer((this.m_TermCols + 25) * 25);
        j = 0;
        k = 0;
      }
      try
      {
        i = this.m_Source.read();
      }
      catch (IOException localIOException1)
      {
        i = -1;
      }
      if (i == -1)
      {
        this.m_Chunks.addElement(localStringBuffer.toString());
      }
      else if ((i == 1) || (m != 0))
      {
        localStringBuffer.append((char)i);
        if (m == 0) {
          m = 1;
        } else {
          m = 0;
        }
      }
      else if (i == 13)
      {
        k++;
        j = 0;
        localStringBuffer.append("\n");
        try
        {
          i = this.m_Source.read();
        }
        catch (IOException localIOException2)
        {
          i = -1;
        }
        if ((i != -1) && (i != 10)) {
          localStringBuffer.append((char)i);
        }
      }
      else if (i == 10)
      {
        k++;
        j = 0;
        localStringBuffer.append("\n");
      }
      else
      {
        localStringBuffer.append((char)i);
        j++;
        if (j == this.m_TermCols)
        {
          k++;
          localStringBuffer.append("\n");
          j = 0;
        }
      }
    } while (i != -1);
  }
  
  private boolean terminalGeometryChanged()
  {
    if ((this.m_TermRows != this.m_IO.getRows()) || (this.m_TermCols != this.m_IO.getColumns()))
    {
      this.m_TermRows = this.m_IO.getRows();
      this.m_TermCols = this.m_IO.getColumns();
      return true;
    }
    return false;
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.Pager

 * JD-Core Version:    0.7.0.1

 */