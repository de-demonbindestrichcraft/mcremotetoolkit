 package org.fusesource.jansi;
 
 import java.io.OutputStream;
 import java.io.PrintWriter;
 import java.io.Writer;
 import java.util.Locale;
 
 public class AnsiRenderWriter
   extends PrintWriter
 {
   public AnsiRenderWriter(OutputStream out)
   {
     super(out);
   }
   
   public AnsiRenderWriter(OutputStream out, boolean autoFlush)
   {
     super(out, autoFlush);
   }
   
   public AnsiRenderWriter(Writer out)
   {
     super(out);
   }
   
   public AnsiRenderWriter(Writer out, boolean autoFlush)
   {
     super(out, autoFlush);
   }
   
   public void write(String s)
   {
     if (AnsiRenderer.test(s)) {
       super.write(AnsiRenderer.render(s));
     } else {
       super.write(s);
     }
   }
   
   public PrintWriter format(String format, Object... args)
   {
     print(String.format(format, args));
     return this;
   }
   
   public PrintWriter format(Locale l, String format, Object... args)
   {
     print(String.format(l, format, args));
     return this;
   }
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     org.fusesource.jansi.AnsiRenderWriter
 * JD-Core Version:    0.7.0.1
 */