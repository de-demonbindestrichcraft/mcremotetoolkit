package net2.wimpi.telnetd.io.toolkit;

import java.io.IOException;

public abstract interface InputFilter
{
  public static final int INPUT_HANDLED = -2000;
  public static final int INPUT_INVALID = -2001;
  
  public abstract int filterInput(int paramInt)
    throws IOException;
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.InputFilter

 * JD-Core Version:    0.7.0.1

 */