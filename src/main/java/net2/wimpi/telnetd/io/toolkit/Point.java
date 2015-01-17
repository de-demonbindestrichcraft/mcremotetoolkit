package net2.wimpi.telnetd.io.toolkit;

public class Point
{
  private int m_Row;
  private int m_Col;
  
  public Point()
  {
    this.m_Col = 0;
    this.m_Row = 0;
  }
  
  public Point(int paramInt1, int paramInt2)
  {
    this.m_Col = paramInt1;
    this.m_Row = paramInt2;
  }
  
  public void setLocation(int paramInt1, int paramInt2)
  {
    this.m_Col = paramInt1;
    this.m_Row = paramInt2;
  }
  
  public void move(int paramInt1, int paramInt2)
  {
    this.m_Col = paramInt1;
    this.m_Row = paramInt2;
  }
  
  public int getColumn()
  {
    return this.m_Col;
  }
  
  public void setColumn(int paramInt)
  {
    this.m_Col = paramInt;
  }
  
  public int getRow()
  {
    return this.m_Row;
  }
  
  public void setRow(int paramInt)
  {
    this.m_Row = paramInt;
  }
}



/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar

 * Qualified Name:     net.wimpi.telnetd.io.toolkit.Point

 * JD-Core Version:    0.7.0.1

 */