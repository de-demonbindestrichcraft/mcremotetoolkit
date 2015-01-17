package net2.wimpi.telnetd.io.toolkit;

public class Dimension
{
  private int m_Height;
  private int m_Width;
  
  public Dimension()
  {
    this.m_Height = 0;
    this.m_Width = 0;
  }
  
  public Dimension(int paramInt1, int paramInt2)
  {
    this.m_Height = paramInt2;
    this.m_Width = paramInt1;
  }
  
  public int getWidth()
  {
    return this.m_Width;
  }
  
  public void setWidth(int paramInt)
  {
    this.m_Width = paramInt;
  }
  
  public int getHeight()
  {
    return this.m_Height;
  }
  
  public void setHeight(int paramInt)
  {
    this.m_Height = paramInt;
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.Dimension

 * JD-Core Version:    0.7.0.1

 */