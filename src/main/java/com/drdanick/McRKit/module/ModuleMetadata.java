 package com.drdanick.McRKit.module;
 
 import java.io.InputStream;
 import java.util.HashMap;
 import java.util.Scanner;
 
 public final class ModuleMetadata
   extends HashMap<String, String>
 {
   public ModuleMetadata(InputStream paramInputStream)
   {
     load(paramInputStream);
   }
   
   public void load(InputStream paramInputStream)
   {
     Scanner localScanner = new Scanner(paramInputStream);
     while (localScanner.hasNextLine())
     {
       String str = localScanner.nextLine().trim();
       if (!str.startsWith("#"))
       {
         String[] arrayOfString = str.split(":", 2);
         if (arrayOfString.length == 2) {
           put(arrayOfString[0], arrayOfString[1]);
         }
       }
     }
     localScanner.close();
   }
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.drdanick.McRKit.module...ModuleMetadata
 * JD-Core Version:    0.7.0.1
 */