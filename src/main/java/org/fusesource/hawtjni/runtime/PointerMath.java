 package org.fusesource.hawtjni.runtime;
 
 public class PointerMath
 {
   private static final boolean bits32 = Library.getBitModel() == 32;
   
   public static final long add(long ptr, long n)
   {
     if (bits32) {
       return (int)(ptr + n);
     }
     return ptr + n;
   }
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     org.fusesource.hawtjni.runtime.PointerMath
 * JD-Core Version:    0.7.0.1
 */