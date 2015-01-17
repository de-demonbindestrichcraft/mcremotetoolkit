 package com.drdanick.McRKit.auth;
 
 class User
 {
   private String name;
   private String passwordHash;
   private boolean persistant;
   
   User(String paramString1, String paramString2)
   {
     this.name = paramString1;
     this.passwordHash = paramString2;
     this.persistant = true;
   }
   
   User(String paramString1, String paramString2, boolean paramBoolean)
   {
     this.name = paramString1;
     this.passwordHash = paramString2;
     this.persistant = paramBoolean;
   }
   
   String getPasswordHash()
   {
     return this.passwordHash;
   }
   
   String getName()
   {
     return this.name;
   }
   
   boolean isPersistant()
   {
     return this.persistant;
   }
   
   void setPasswordHash(String paramString)
   {
     this.passwordHash = paramString;
   }
   
   void setName(String paramString)
   {
     this.name = paramString;
   }
   
   void setPersistant(boolean paramBoolean)
   {
     this.persistant = paramBoolean;
   }
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.drdanick.McRKit.auth...User
 * JD-Core Version:    0.7.0.1
 */