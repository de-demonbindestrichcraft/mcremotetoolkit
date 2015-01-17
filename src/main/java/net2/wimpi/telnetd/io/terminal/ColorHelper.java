package net2.wimpi.telnetd.io.terminal;

public class ColorHelper
{
  public static final String INTERNAL_MARKER = "\001";
  public static final int MARKER_CODE = 1;
  public static final String BLACK = "S";
  public static final String RED = "R";
  public static final String GREEN = "G";
  public static final String YELLOW = "Y";
  public static final String BLUE = "B";
  public static final String MAGENTA = "M";
  public static final String CYAN = "C";
  public static final String WHITE = "W";
  public static final String BOLD = "f";
  public static final String BOLD_OFF = "d";
  public static final String ITALIC = "i";
  public static final String ITALIC_OFF = "j";
  public static final String UNDERLINED = "u";
  public static final String UNDERLINED_OFF = "v";
  public static final String BLINK = "e";
  public static final String BLINK_OFF = "n";
  public static final String RESET_ALL = "a";
  
  public static String colorizeText(String paramString1, String paramString2)
  {
    return "\001" + paramString2 + paramString1 + "\001" + "a";
  }
  
  public static String colorizeText(String paramString1, String paramString2, boolean paramBoolean)
  {
    if (paramBoolean) {
      return colorizeText(paramString1, paramString2);
    }
    return "\001" + paramString2 + paramString1;
  }
  
  public static String colorizeBackground(String paramString1, String paramString2)
  {
    return "\001" + paramString2.toLowerCase() + paramString1 + "\001" + "a";
  }
  
  public static String colorizeBackground(String paramString1, String paramString2, boolean paramBoolean)
  {
    if (paramBoolean) {
      return colorizeBackground(paramString1, paramString2);
    }
    return "\001" + paramString2.toLowerCase() + paramString1;
  }
  
  public static String colorizeText(String paramString1, String paramString2, String paramString3)
  {
    return "\001" + paramString2 + "\001" + paramString3.toLowerCase() + paramString1 + "\001" + "a";
  }
  
  public static String colorizeText(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (paramBoolean) {
      return colorizeText(paramString1, paramString2, paramString3);
    }
    return "\001" + paramString2 + "\001" + paramString3.toLowerCase() + paramString1;
  }
  
  public static String boldcolorizeText(String paramString1, String paramString2)
  {
    return "\001f\001" + paramString2 + paramString1 + "\001" + "a";
  }
  
  public static String boldcolorizeText(String paramString1, String paramString2, String paramString3)
  {
    return "\001f\001" + paramString2 + "\001" + paramString3.toLowerCase() + paramString1 + "\001" + "a";
  }
  
  public static String boldText(String paramString)
  {
    return "\001f" + paramString + "\001" + "d";
  }
  
  public static String italicText(String paramString)
  {
    return "\001i" + paramString + "\001" + "j";
  }
  
  public static String underlinedText(String paramString)
  {
    return "\001u" + paramString + "\001" + "v";
  }
  
  public static String blinkingText(String paramString)
  {
    return "\001e" + paramString + "\001" + "n";
  }
  
  public static long getVisibleLength(String paramString)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    while (m == 0)
    {
      k = paramString.indexOf(1, j);
      if (k != -1)
      {
        i++;
        j = k + 1;
      }
      else
      {
        m = 1;
      }
    }
    return paramString.length() - i * 2;
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.terminal.ColorHelper

 * JD-Core Version:    0.7.0.1

 */