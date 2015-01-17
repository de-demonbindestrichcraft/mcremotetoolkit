package net2.wimpi.telnetd.io.terminal;

public abstract interface Terminal
{
  public static final byte EOT = 4;
  public static final byte BS = 8;
  public static final byte DEL = 127;
  public static final byte HT = 9;
  public static final byte FF = 12;
  public static final byte SGR = 1;
  public static final byte CAN = 24;
  public static final byte ESC = 27;
  public static final byte LSB = 91;
  public static final byte SEMICOLON = 59;
  public static final byte A = 65;
  public static final byte B = 66;
  public static final byte C = 67;
  public static final byte D = 68;
  public static final byte E = 69;
  public static final byte H = 72;
  public static final byte f = 102;
  public static final byte r = 114;
  public static final byte LE = 75;
  public static final byte SE = 74;
  
  public abstract int translateControlCharacter(int paramInt);
  
  public abstract int translateEscapeSequence(int[] paramArrayOfInt);
  
  public abstract byte[] getEraseSequence(int paramInt);
  
  public abstract byte[] getCursorMoveSequence(int paramInt1, int paramInt2);
  
  public abstract byte[] getCursorPositioningSequence(int[] paramArrayOfInt);
  
  public abstract byte[] getSpecialSequence(int paramInt);
  
  public abstract byte[] getScrollMarginsSequence(int paramInt1, int paramInt2);
  
  public abstract byte[] getGRSequence(int paramInt1, int paramInt2);
  
  public abstract String format(String paramString);
  
  public abstract String formatBold(String paramString);
  
  public abstract byte[] getInitSequence();
  
  public abstract boolean supportsSGR();
  
  public abstract boolean supportsScrolling();
  
  public abstract int getAtomicSequenceLength();
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.terminal.Terminal

 * JD-Core Version:    0.7.0.1

 */