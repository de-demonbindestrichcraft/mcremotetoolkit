package net2.wimpi.telnetd.io.terminal;

import java.io.PrintStream;

public final class Colorizer
{
  private static Object c_Self;
  private int[] m_ColorMapping = new int[''];
  private static int testcount = 0;
  private static Colorizer myColorizer;
  private static final int S = 30;
  private static final int s = 40;
  private static final int R = 31;
  private static final int r = 41;
  private static final int G = 32;
  private static final int g = 42;
  private static final int Y = 33;
  private static final int y = 43;
  private static final int B = 34;
  private static final int b = 44;
  private static final int M = 35;
  private static final int m = 45;
  private static final int C = 36;
  private static final int c = 46;
  private static final int W = 37;
  private static final int w = 47;
  private static final int f = 1;
  private static final int d = 22;
  private static final int i = 3;
  private static final int j = 23;
  private static final int u = 4;
  private static final int v = 24;
  private static final int e = 5;
  private static final int n = 25;
  private static final int h = 8;
  private static final int a = 0;
  
  private Colorizer()
  {
    this.m_ColorMapping[83] = 30;
    this.m_ColorMapping[82] = 31;
    this.m_ColorMapping[71] = 32;
    this.m_ColorMapping[89] = 33;
    this.m_ColorMapping[66] = 34;
    this.m_ColorMapping[77] = 35;
    this.m_ColorMapping[67] = 36;
    this.m_ColorMapping[87] = 37;
    this.m_ColorMapping[115] = 40;
    this.m_ColorMapping[114] = 41;
    this.m_ColorMapping[103] = 42;
    this.m_ColorMapping[121] = 43;
    this.m_ColorMapping[98] = 44;
    this.m_ColorMapping[109] = 45;
    this.m_ColorMapping[99] = 46;
    this.m_ColorMapping[119] = 47;
    this.m_ColorMapping[102] = 1;
    this.m_ColorMapping[100] = 22;
    this.m_ColorMapping[105] = 3;
    this.m_ColorMapping[106] = 23;
    this.m_ColorMapping[117] = 4;
    this.m_ColorMapping[118] = 24;
    this.m_ColorMapping[101] = 5;
    this.m_ColorMapping[110] = 25;
    this.m_ColorMapping[104] = 8;
    this.m_ColorMapping[97] = 0;
    c_Self = this;
  }
  
  public String colorize(String paramString, boolean paramBoolean)
  {
    return colorize(paramString, paramBoolean, false);
  }
  
  public String colorize(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramString.length() + 20);
    int k = 0;
    int i1 = 0;
    int i2 = 0;
    while (i2 == 0)
    {
      i1 = paramString.indexOf(1, k);
      if (i1 != -1)
      {
        localStringBuffer.append(paramString.substring(k, i1));
        if (paramBoolean1) {
          localStringBuffer.append(addEscapeSequence(paramString.substring(i1 + 1, i1 + 2), paramBoolean2));
        }
        k = i1 + 2;
      }
      else
      {
        localStringBuffer.append(paramString.substring(k, paramString.length()));
        i2 = 1;
      }
    }
    if (paramBoolean1) {
      localStringBuffer.append(addEscapeSequence("a", false));
    }
    return localStringBuffer.toString();
  }
  
  private String addEscapeSequence(String paramString, boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer(10);
    byte[] arrayOfByte = paramString.getBytes();
    int k = arrayOfByte[0];
    localStringBuffer.append('\033');
    localStringBuffer.append('[');
    int i1 = this.m_ColorMapping[k];
    localStringBuffer.append(i1);
    if ((paramBoolean) && (i1 != 1))
    {
      localStringBuffer.append(';');
      localStringBuffer.append(1);
    }
    localStringBuffer.append('m');
    return localStringBuffer.toString();
  }
  
  public static Colorizer getReference()
  {
    if (c_Self != null) {
      return (Colorizer)c_Self;
    }
    return new Colorizer();
  }
  
  private static void announceResult(boolean paramBoolean)
  {
    if (paramBoolean) {
      System.out.println("[#" + testcount + "] ok.");
    } else {
      System.out.println("[#" + testcount + "] failed (see possible StackTrace).");
    }
  }
  
  private static void announceTest(String paramString)
  {
    testcount += 1;
    System.out.println("Test #" + testcount + " [" + paramString + "]:");
  }
  
  private static void bfcolorTest(String paramString)
  {
    System.out.println("->" + myColorizer.colorize(ColorHelper.boldcolorizeText("COLOR", paramString), true) + "<-");
  }
  
  private static void fcolorTest(String paramString)
  {
    System.out.println("->" + myColorizer.colorize(ColorHelper.colorizeText("COLOR", paramString), true) + "<-");
  }
  
  private static void bcolorTest(String paramString)
  {
    System.out.println("->" + myColorizer.colorize(ColorHelper.colorizeBackground("     ", paramString), true) + "<-");
  }
  
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      announceTest("Instantiation");
      myColorizer = getReference();
      announceResult(true);
      announceTest("Textcolor Tests");
      fcolorTest("S");
      fcolorTest("R");
      fcolorTest("G");
      fcolorTest("Y");
      fcolorTest("B");
      fcolorTest("M");
      fcolorTest("C");
      fcolorTest("W");
      announceResult(true);
      announceTest("Bold textcolor Tests");
      bfcolorTest("S");
      bfcolorTest("R");
      bfcolorTest("G");
      bfcolorTest("Y");
      bfcolorTest("B");
      bfcolorTest("M");
      bfcolorTest("C");
      bfcolorTest("W");
      announceResult(true);
      announceTest("Background Tests");
      bcolorTest("S");
      bcolorTest("R");
      bcolorTest("G");
      bcolorTest("Y");
      bcolorTest("B");
      bcolorTest("M");
      bcolorTest("C");
      bcolorTest("W");
      announceResult(true);
      announceTest("Mixed Color Tests");
      System.out.println("->" + myColorizer.colorize(ColorHelper.colorizeText("COLOR", "W", "B"), true) + "<-");
      System.out.println("->" + myColorizer.colorize(ColorHelper.colorizeText("COLOR", "Y", "G"), true) + "<-");
      System.out.println("->" + myColorizer.colorize(ColorHelper.boldcolorizeText("COLOR", "W", "B"), true) + "<-");
      System.out.println("->" + myColorizer.colorize(ColorHelper.boldcolorizeText("COLOR", "Y", "G"), true) + "<-");
      announceResult(true);
      announceTest("Style Tests");
      System.out.println("->" + myColorizer.colorize(ColorHelper.boldText("Bold"), true) + "<-");
      System.out.println("->" + myColorizer.colorize(ColorHelper.italicText("Italic"), true) + "<-");
      System.out.println("->" + myColorizer.colorize(ColorHelper.underlinedText("Underlined"), true) + "<-");
      System.out.println("->" + myColorizer.colorize(ColorHelper.blinkingText("Blinking"), true) + "<-");
      announceResult(true);
      announceTest("Mixed Color/Style Tests");
      System.out.println("->" + myColorizer.colorize(ColorHelper.boldText(new StringBuffer().append(ColorHelper.colorizeText("RED", "R", false)).append(ColorHelper.colorizeText("BLUE", "B", false)).append(ColorHelper.colorizeText("GREEN", "G", false)).toString()), true) + "<-");
      System.out.println("->" + myColorizer.colorize(ColorHelper.boldText(new StringBuffer().append(ColorHelper.colorizeBackground("RED", "R", false)).append(ColorHelper.colorizeBackground("BLUE", "B", false)).append(ColorHelper.colorizeBackground("GREEN", "G", false)).toString()), true) + "<-");
      System.out.println("->" + myColorizer.colorize(ColorHelper.boldText(new StringBuffer().append(ColorHelper.colorizeText("RED", "W", "R", false)).append(ColorHelper.colorizeText("BLUE", "W", "B", false)).append(ColorHelper.colorizeText("GREEN", "W", "G", false)).toString()), true) + "<-");
      announceResult(true);
      announceTest("Visible length test");
      String str = ColorHelper.boldcolorizeText("STRING", "Y");
      System.out.println("->" + myColorizer.colorize(str, true) + "<-");
      System.out.println("Visible length=" + ColorHelper.getVisibleLength(str));
      str = ColorHelper.boldcolorizeText("BANNER", "W", "B") + ColorHelper.colorizeText("COLOR", "W", "B") + ColorHelper.underlinedText("UNDER");
      System.out.println("->" + myColorizer.colorize(str, true) + "<-");
      System.out.println("Visible length=" + ColorHelper.getVisibleLength(str));
      announceResult(true);
      System.out.println("Forcing bold");
      System.out.println(myColorizer.colorize(ColorHelper.colorizeText("RED", "R"), true, true));
    }
    catch (Exception localException)
    {
      announceResult(false);
      localException.printStackTrace();
    }
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.terminal.Colorizer

 * JD-Core Version:    0.7.0.1

 */