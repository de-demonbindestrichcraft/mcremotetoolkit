package net2.wimpi.telnetd.io.toolkit;

import java.io.IOException;
import net2.wimpi.telnetd.io.BasicTerminalIO;
import net2.wimpi.telnetd.io.terminal.ColorHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Titlebar
  extends InertComponent
{
  private static Log log = LogFactory.getLog(Titlebar.class);
  private String m_Title;
  private int m_Align;
  private String m_BgColor;
  private String m_FgColor;
  public static final int ALIGN_RIGHT = 1;
  public static final int ALIGN_LEFT = 2;
  public static final int ALIGN_CENTER = 3;
  
  public Titlebar(BasicTerminalIO paramBasicTerminalIO, String paramString)
  {
    super(paramBasicTerminalIO, paramString);
  }
  
  public void setTitleText(String paramString)
  {
    this.m_Title = paramString;
  }
  
  public String getTitleText()
  {
    return this.m_Title;
  }
  
  public void setAlignment(int paramInt)
  {
    if ((paramInt < 1) || (paramInt > 3)) {
      paramInt = 2;
    } else {
      this.m_Align = paramInt;
    }
  }
  
  public void setForegroundColor(String paramString)
  {
    this.m_FgColor = paramString;
  }
  
  public void setBackgroundColor(String paramString)
  {
    this.m_BgColor = paramString;
  }
  
  public void draw()
    throws IOException
  {
    this.m_IO.storeCursor();
    this.m_IO.homeCursor();
    this.m_IO.write(getBar());
    this.m_IO.restoreCursor();
  }
  
  private String getBar()
  {
    String str = this.m_Title;
    int i = this.m_IO.getColumns() - 1;
    int j = (int)ColorHelper.getVisibleLength(this.m_Title);
    if (j > i) {
      str = this.m_Title.substring(0, i);
    }
    j = (int)ColorHelper.getVisibleLength(str);
    StringBuffer localStringBuffer = new StringBuffer(i + j);
    switch (this.m_Align)
    {
    case 2: 
      localStringBuffer.append(str);
      appendSpaceString(localStringBuffer, i - j);
      break;
    case 1: 
      appendSpaceString(localStringBuffer, i - j);
      localStringBuffer.append(str);
      break;
    case 3: 
      int k = i - j != 0 ? (i - j) / 2 : 0;
      int m = i - j - k;
      appendSpaceString(localStringBuffer, k);
      localStringBuffer.append(str);
      appendSpaceString(localStringBuffer, m);
    }
    return ColorHelper.boldcolorizeText(localStringBuffer.toString(), this.m_FgColor, this.m_BgColor);
  }
  
  private void appendSpaceString(StringBuffer paramStringBuffer, int paramInt)
  {
    for (int i = 0; i < paramInt; i++) {
      paramStringBuffer.append(" ");
    }
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.Titlebar

 * JD-Core Version:    0.7.0.1

 */