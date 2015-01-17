 package com.twmacinta.util;
 
 import com.twmacinta.io.NullOutputStream;
 import java.io.BufferedInputStream;
 import java.io.FileInputStream;
 import java.io.FilterOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.PrintStream;
 
 public class MD5OutputStream
   extends FilterOutputStream
 {
   private MD5 md5;
   
   public MD5OutputStream(OutputStream out)
   {
     super(out);
     
     this.md5 = new MD5();
   }
   
   public void write(int b)
     throws IOException
   {
     this.out.write(b);
     this.md5.Update((byte)b);
   }
   
   public void write(byte[] b, int off, int len)
     throws IOException
   {
     this.out.write(b, off, len);
     this.md5.Update(b, off, len);
   }
   
   public byte[] hash()
   {
     return this.md5.Final();
   }
   
   public MD5 getMD5()
   {
     return this.md5;
   }
   
   public static void main(String[] arg)
   {
     try
     {
       MD5OutputStream out = new MD5OutputStream(new NullOutputStream());
       InputStream in = new BufferedInputStream(new FileInputStream(arg[0]));
       byte[] buf = new byte[65536];
       
       long total_read = 0L;
       int num_read;
       while ((num_read = in.read(buf)) != -1)
       {
         total_read += num_read;
         out.write(buf, 0, num_read);
       }
       System.out.println(MD5.asHex(out.hash()) + "  " + arg[0]);
       in.close();
       out.close();
     }
     catch (Exception e)
     {
       e.printStackTrace();
     }
   }
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.twmacinta.util...MD5OutputStream
 * JD-Core Version:    0.7.0.1
 */