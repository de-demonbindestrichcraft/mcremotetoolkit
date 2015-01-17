 package com.drdanick.McRKit.module;
 
 public class InvalidModuleException
   extends Exception
 {
   private final Throwable cause;
   
   public InvalidModuleException(Throwable paramThrowable)
   {
     this.cause = paramThrowable;
   }
   
   public InvalidModuleException(String paramString)
   {
     super(paramString);
     this.cause = this;
   }
   
   public Throwable getCause()
   {
     return this.cause;
   }
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.drdanick.McRKit.module...InvalidModuleException
 * JD-Core Version:    0.7.0.1
 */