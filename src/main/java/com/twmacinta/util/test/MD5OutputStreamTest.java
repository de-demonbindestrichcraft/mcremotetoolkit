 package com.twmacinta.util.test;
 
 import com.twmacinta.io.NullOutputStream;
 import com.twmacinta.util.MD5;
 import com.twmacinta.util.MD5OutputStream;
 import java.io.BufferedOutputStream;
 import java.io.BufferedReader;
 import java.io.FilterOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.util.Random;
 import java.util.StringTokenizer;
 
 public class MD5OutputStreamTest
 {
   public static void main(String[] arg)
   {
     try
     {
       long seed = System.currentTimeMillis();
       if ((arg.length > 0) && 
         (!arg[0].equals("time"))) {
         seed = Long.parseLong(arg[0]);
       }
       long max_data = 21474836480L;
       if (arg.length > 1) {
         max_data = Long.parseLong(arg[1]);
       }
       String[] propNames = { "os.name", "os.arch" };
       for (int i = 0; i < propNames.length; i++)
       {
         System.out.print(propNames[i]);
         System.out.print(": ");
         System.out.println(System.getProperty(propNames[i]));
       }
       if (MD5.initNativeLibrary()) {
         System.out.println("Successfully loaded native library");
       } else {
         System.out.println("WARNING: Native library NOT loaded");
       }
       String md5Binary = null;
       String[] toTry = { "md5sum", "md5" };
       for (int nameIndex = 0; nameIndex < toTry.length; nameIndex++) {
         try
         {
           String binName = toTry[nameIndex];
           Process proc = Runtime.getRuntime().exec(binName);
           proc.getOutputStream().close();
           proc.getInputStream().close();
           md5Binary = binName;
         }
         catch (Exception e) {}
       }
       if (md5Binary == null) {
         throw new Exception("No md5sum binary or alternative found");
       }
       Random ran = new Random(seed);
       for (;;)
       {
         System.out.print("seed:  " + seed + "  \t");
         long data_size = ran.nextLong();
         if (data_size < 0L) {
           data_size = -data_size;
         }
         data_size %= (max_data + 1L);
         System.out.println("size:  " + data_size);
         runTest(data_size, ran, md5Binary);
         seed = ran.nextLong();
         ran.setSeed(seed);
       }
     }
     catch (Exception e)
     {
       e.printStackTrace();
     }
   }
   
   private static void runTest(long data_size, Random ran, String md5Binary)
     throws IOException
   {
     Process proc = Runtime.getRuntime().exec(md5Binary);
     MD5OutputStream out1 = new MD5OutputStream(new NullOutputStream());
     OutputStream out2 = new BufferedOutputStream(proc.getOutputStream());
     while (data_size > 0L)
     {
       int output_type = ran.nextInt() % 100;
       
       output_type -= 5;
       if (output_type < 0)
       {
         outputSingleByte(ran, out1, out2);
         data_size -= 1L;
       }
       else
       {
         output_type -= 25;
         if (output_type < 0) {
           data_size -= outputFullBuffer(ran, out1, out2, data_size);
         } else {
           data_size -= outputPartialBuffer(ran, out1, out2, data_size);
         }
       }
     }
     BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
     out2.flush();
     out2.close();
     
     String native_sum = new StringTokenizer(in.readLine()).nextToken();
     in.close();
     String java_sum = MD5.asHex(out1.hash());
     if (!native_sum.equals(java_sum))
     {
       out1.close();
       System.out.println("ERROR");
       System.out.println("java:   " + java_sum);
       System.out.println("native: " + native_sum);
       System.exit(1);
     }
     out1.close();
   }
   
   private static void outputSingleByte(Random ran, OutputStream out1, OutputStream out2)
     throws IOException
   {
     int b = ran.nextInt() & 0xFF;
     out1.write(b);
     out2.write(b);
   }
   
   private static long outputFullBuffer(Random ran, OutputStream out1, OutputStream out2, long max_bytes)
     throws IOException
   {
     int b_len = ran.nextInt();
     if (b_len < 0) {
       b_len = -b_len;
     }
     b_len %= 131072;
     if (b_len > max_bytes) {
       b_len = (int)max_bytes;
     }
     byte[] b = new byte[b_len];
     ran.nextBytes(b);
     out1.write(b);
     out2.write(b);
     return b_len;
   }
   
   private static long outputPartialBuffer(Random ran, OutputStream out1, OutputStream out2, long max_bytes)
     throws IOException
   {
     int b_len = ran.nextInt();
     if (b_len < 0) {
       b_len = -b_len;
     }
     b_len %= 524288;
     if (b_len > max_bytes) {
       b_len = (int)max_bytes;
     }
     if (b_len == 0) {
       return 0L;
     }
     byte[] b = new byte[b_len];
     ran.nextBytes(b);
     
     int off = ran.nextInt();
     if (off < 0) {
       off = -off;
     }
     off %= b_len;
     if (off == b_len) {
       return 0L;
     }
     int len = ran.nextInt();
     if (len < 0) {
       len = -len;
     }
     len %= (b_len - off);
     
     out1.write(b, off, len);
     out2.write(b, off, len);
     return len;
   }
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.twmacinta.util...test.MD5OutputStreamTest
 * JD-Core Version:    0.7.0.1
 */