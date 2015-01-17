 package com.twmacinta.io;
 
 import java.io.IOException;
 import java.io.OutputStream;
 
 public class NullOutputStream
   extends OutputStream
 {
   private boolean closed = false;
   
   public void close()
   {
     this.closed = true;
   }
   
   public void flush()
     throws IOException
   {
     if (this.closed) {
       _throwClosed();
     }
   }
   
   private void _throwClosed()
     throws IOException
   {
     throw new IOException("This OutputStream has been closed");
   }
   
   public void write(byte[] b)
     throws IOException
   {
     if (this.closed) {
       _throwClosed();
     }
   }
   
   public void write(byte[] b, int offset, int len)
     throws IOException
   {
     if (this.closed) {
       _throwClosed();
     }
   }
   
   public void write(int b)
     throws IOException
   {
     if (this.closed) {
       _throwClosed();
     }
   }
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.twmacinta.io...NullOutputStream
 * JD-Core Version:    0.7.0.1
 */