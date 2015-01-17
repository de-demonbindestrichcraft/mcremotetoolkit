package net2.wimpi.telnetd.io.terminal;

public abstract class BasicTerminal
  implements Terminal
{
  protected Colorizer m_Colorizer = Colorizer.getReference();
  
  public int translateControlCharacter(int paramInt)
  {
    switch (paramInt)
    {
    case 127: 
      return 1302;
    case 8: 
      return 1303;
    case 9: 
      return 1301;
    case 27: 
      return 1200;
    case 1: 
      return 1304;
    case 4: 
      return 1306;
    }
    return paramInt;
  }
  
  public int translateEscapeSequence(int[] paramArrayOfInt)
  {
    try
    {
      if (paramArrayOfInt[0] == 91) {
        switch (paramArrayOfInt[1])
        {
        case 65: 
          return 1001;
        case 66: 
          return 1002;
        case 67: 
          return 1003;
        case 68: 
          return 1004;
        }
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      return 1201;
    }
    return 1202;
  }
  
  public byte[] getCursorMoveSequence(int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = null;
    if (paramInt2 == 1) {
      arrayOfByte = new byte[3];
    } else {
      arrayOfByte = new byte[paramInt2 * 3];
    }
    for (int i = 0; i < paramInt2 * 3; i++)
    {
      arrayOfByte[i] = 27;
      arrayOfByte[(i + 1)] = 91;
      switch (paramInt1)
      {
      case 1001: 
        arrayOfByte[(i + 2)] = 65;
        break;
      case 1002: 
        arrayOfByte[(i + 2)] = 66;
        break;
      case 1003: 
        arrayOfByte[(i + 2)] = 67;
        break;
      case 1004: 
        arrayOfByte[(i + 2)] = 68;
        break;
      }
      i += 2;
    }
    return arrayOfByte;
  }
  
  public byte[] getCursorPositioningSequence(int[] paramArrayOfInt)
  {
    byte[] arrayOfByte1 = null;
    if ((paramArrayOfInt[0] == net2.wimpi.telnetd.io.TerminalIO.HOME[0]) && (paramArrayOfInt[1] == net2.wimpi.telnetd.io.TerminalIO.HOME[1]))
    {
      arrayOfByte1 = new byte[3];
      arrayOfByte1[0] = 27;
      arrayOfByte1[1] = 91;
      arrayOfByte1[2] = 72;
    }
    else
    {
      byte[] arrayOfByte2 = translateIntToDigitCodes(paramArrayOfInt[0]);
      byte[] arrayOfByte3 = translateIntToDigitCodes(paramArrayOfInt[1]);
      int i = 0;
      arrayOfByte1 = new byte[4 + arrayOfByte2.length + arrayOfByte3.length];
      arrayOfByte1[0] = 27;
      arrayOfByte1[1] = 91;
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 2, arrayOfByte2.length);
      i = 2 + arrayOfByte2.length;
      arrayOfByte1[i] = 59;
      i++;
      System.arraycopy(arrayOfByte3, 0, arrayOfByte1, i, arrayOfByte3.length);
      i += arrayOfByte3.length;
      arrayOfByte1[i] = 72;
    }
    return arrayOfByte1;
  }
  
  public byte[] getEraseSequence(int paramInt)
  {
    byte[] arrayOfByte = null;
    switch (paramInt)
    {
    case 1100: 
      arrayOfByte = new byte[3];
      arrayOfByte[0] = 27;
      arrayOfByte[1] = 91;
      arrayOfByte[2] = 75;
      break;
    case 1101: 
      arrayOfByte = new byte[4];
      arrayOfByte[0] = 27;
      arrayOfByte[1] = 91;
      arrayOfByte[2] = 49;
      arrayOfByte[3] = 75;
      break;
    case 1103: 
      arrayOfByte = new byte[4];
      arrayOfByte[0] = 27;
      arrayOfByte[1] = 91;
      arrayOfByte[2] = 50;
      arrayOfByte[3] = 75;
      break;
    case 1104: 
      arrayOfByte = new byte[3];
      arrayOfByte[0] = 27;
      arrayOfByte[1] = 91;
      arrayOfByte[2] = 74;
      break;
    case 1105: 
      arrayOfByte = new byte[4];
      arrayOfByte[0] = 27;
      arrayOfByte[1] = 91;
      arrayOfByte[2] = 49;
      arrayOfByte[3] = 74;
      break;
    case 1106: 
      arrayOfByte = new byte[4];
      arrayOfByte[0] = 27;
      arrayOfByte[1] = 91;
      arrayOfByte[2] = 50;
      arrayOfByte[3] = 74;
      break;
    }
    return arrayOfByte;
  }
  
  public byte[] getSpecialSequence(int paramInt)
  {
    byte[] arrayOfByte = null;
    switch (paramInt)
    {
    case 1051: 
      arrayOfByte = new byte[2];
      arrayOfByte[0] = 27;
      arrayOfByte[1] = 55;
      break;
    case 1052: 
      arrayOfByte = new byte[2];
      arrayOfByte[0] = 27;
      arrayOfByte[1] = 56;
      break;
    case 10005: 
      arrayOfByte = new byte[2];
      arrayOfByte[0] = 27;
      arrayOfByte[1] = 99;
      break;
    case 10006: 
      arrayOfByte = new byte[4];
      arrayOfByte[0] = 27;
      arrayOfByte[1] = 91;
      arrayOfByte[2] = 55;
      arrayOfByte[3] = 104;
      break;
    case 10007: 
      arrayOfByte = new byte[4];
      arrayOfByte[0] = 27;
      arrayOfByte[1] = 91;
      arrayOfByte[2] = 55;
      arrayOfByte[3] = 108;
    }
    return arrayOfByte;
  }
  
  public byte[] getGRSequence(int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte1 = new byte[0];
    int i = 0;
    switch (paramInt1)
    {
    case 10001: 
    case 10002: 
      byte[] arrayOfByte2 = translateIntToDigitCodes(paramInt2);
      arrayOfByte1 = new byte[3 + arrayOfByte2.length];
      arrayOfByte1[0] = 27;
      arrayOfByte1[1] = 91;
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 2, arrayOfByte2.length);
      i = 2 + arrayOfByte2.length;
      arrayOfByte1[i] = 109;
      break;
    case 10003: 
      byte[] arrayOfByte3 = translateIntToDigitCodes(paramInt2);
      arrayOfByte1 = new byte[3 + arrayOfByte3.length];
      arrayOfByte1[0] = 27;
      arrayOfByte1[1] = 91;
      System.arraycopy(arrayOfByte3, 0, arrayOfByte1, 2, arrayOfByte3.length);
      i = 2 + arrayOfByte3.length;
      arrayOfByte1[i] = 109;
      break;
    case 10004: 
      arrayOfByte1 = new byte[5];
      arrayOfByte1[0] = 27;
      arrayOfByte1[1] = 91;
      arrayOfByte1[2] = 52;
      arrayOfByte1[3] = 56;
      arrayOfByte1[4] = 109;
    }
    return arrayOfByte1;
  }
  
  public byte[] getScrollMarginsSequence(int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte1 = new byte[0];
    if (supportsScrolling())
    {
      byte[] arrayOfByte2 = translateIntToDigitCodes(paramInt1);
      byte[] arrayOfByte3 = translateIntToDigitCodes(paramInt2);
      int i = 0;
      arrayOfByte1 = new byte[4 + arrayOfByte2.length + arrayOfByte3.length];
      arrayOfByte1[0] = 27;
      arrayOfByte1[1] = 91;
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 2, arrayOfByte2.length);
      i = 2 + arrayOfByte2.length;
      arrayOfByte1[i] = 59;
      i++;
      System.arraycopy(arrayOfByte3, 0, arrayOfByte1, i, arrayOfByte3.length);
      i += arrayOfByte3.length;
      arrayOfByte1[i] = 114;
    }
    return arrayOfByte1;
  }
  
  public String format(String paramString)
  {
    return this.m_Colorizer.colorize(paramString, supportsSGR(), false);
  }
  
  public String formatBold(String paramString)
  {
    return this.m_Colorizer.colorize(paramString, supportsSGR(), true);
  }
  
  public byte[] getInitSequence()
  {
    byte[] arrayOfByte = new byte[0];
    return arrayOfByte;
  }
  
  public int getAtomicSequenceLength()
  {
    return 2;
  }
  
  public byte[] translateIntToDigitCodes(int paramInt)
  {
    return Integer.toString(paramInt).getBytes();
  }
  
  public abstract boolean supportsSGR();
  
  public abstract boolean supportsScrolling();
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.terminal.BasicTerminal

 * JD-Core Version:    0.7.0.1

 */