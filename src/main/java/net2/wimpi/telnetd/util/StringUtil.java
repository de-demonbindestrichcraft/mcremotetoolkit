package net2.wimpi.telnetd.util;

import java.util.StringTokenizer;

public final class StringUtil
{
  public static String[] split(String paramString1, String paramString2)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString1, paramString2);
    String[] arrayOfString = new String[localStringTokenizer.countTokens()];
    for (int i = 0; i < arrayOfString.length; i++) {
      arrayOfString[i] = localStringTokenizer.nextToken();
    }
    return arrayOfString;
  }
  
  public static String[] split(String paramString, char paramChar)
  {
    return split(paramString, String.valueOf(paramChar));
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.util.StringUtil

 * JD-Core Version:    0.7.0.1

 */