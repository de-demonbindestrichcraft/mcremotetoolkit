 package com.drdanick.McRKit.auth;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.io.PrintWriter;
 import java.io.UnsupportedEncodingException;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.util.HashMap;
 import java.util.Set;
 
 public class UserManager
 {
   private File userFile;
   private MessageDigest md;
   private HashMap<String, User> users;
   private static UserManager userManagerInstance = null;
   private String salt = "";
   
   private UserManager(File paramFile)
   {
     userManagerInstance = this;
     this.userFile = paramFile;
     try
     {
       this.md = MessageDigest.getInstance("SHA1");
     }
     catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
     {
       localNoSuchAlgorithmException.printStackTrace();
     }
     this.users = new HashMap();
     if (paramFile.exists()) {
       load();
     }
   }
   
   public static UserManager constructUserManager(String paramString)
     throws UserManagerException
   {
     if (userManagerInstance == null) {
       return new UserManager(new File(paramString));
     }
     throw new UserManagerException("UserManager is already instantiated.");
   }
   
   public boolean addUser(String paramString1, String paramString2)
   {
     paramString1 = paramString1.trim();
     if (this.users.containsKey(paramString1.trim())) {
       return false;
     }
     paramString2 = paramString2.trim() + this.salt;
     this.md.reset();
     try
     {
       byte[] arrayOfByte = this.md.digest(paramString2.getBytes("UTF-8"));
       this.users.put(paramString1, new User(paramString1, convertToHexString(arrayOfByte, "UTF-8")));
     }
     catch (UnsupportedEncodingException localUnsupportedEncodingException)
     {
       localUnsupportedEncodingException.printStackTrace();
       return false;
     }
     return true;
   }
   
   public static UserManager getInstance()
   {
     return userManagerInstance;
   }
   
   public boolean addUser(String paramString1, String paramString2, boolean paramBoolean)
   {
     paramString1 = paramString1.trim();
     if (this.users.containsKey(paramString1.trim())) {
       return false;
     }
     paramString2 = paramString2.trim() + this.salt;
     this.md.reset();
     try
     {
       byte[] arrayOfByte = this.md.digest(paramString2.getBytes("UTF-8"));
       this.users.put(paramString1, new User(paramString1, convertToHexString(arrayOfByte, "UTF-8"), paramBoolean));
     }
     catch (UnsupportedEncodingException localUnsupportedEncodingException)
     {
       localUnsupportedEncodingException.printStackTrace();
       return false;
     }
     return true;
   }
   
   public boolean removeUser(String paramString)
   {
     return this.users.remove(paramString.trim()) != null;
   }
   
   public boolean addUserWithHash(String paramString1, String paramString2)
   {
     paramString1 = paramString1.trim();
     if (this.users.containsKey(paramString1.trim())) {
       return false;
     }
     paramString2 = paramString2.trim();
     this.users.put(paramString1, new User(paramString1, paramString2));
     return true;
   }
   
   public boolean userExists(String paramString)
   {
     paramString = paramString.trim();
     return this.users.containsKey(paramString);
   }
   
   public User findUserByHashedName(String paramString)
   {
     for (User localUser : this.users.values()) {
       try
       {
         this.md.reset();
         byte[] arrayOfByte = this.md.digest((localUser.getName() + this.salt).getBytes("UTF-8"));
         if (convertToHexString(arrayOfByte, "UTF-8").equals(paramString)) {
           return localUser;
         }
       }
       catch (UnsupportedEncodingException localUnsupportedEncodingException)
       {
         localUnsupportedEncodingException.printStackTrace();
         return null;
       }
     }
     return null;
   }
   
   public boolean isValidLogon(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
   {
     paramString1 = paramString1.trim();
     paramString2 = paramString2.trim();
     this.md.reset();
     String str = "";
     User localUser;
     if (paramBoolean1) {
       localUser = findUserByHashedName(paramString1);
     } else {
       localUser = (User)this.users.get(paramString1);
     }
     this.md.reset();
     if (!paramBoolean2) {
       try
       {
         byte[] arrayOfByte = this.md.digest((paramString2 + this.salt).getBytes("UTF-8"));
         str = convertToHexString(arrayOfByte, "UTF-8");
       }
       catch (UnsupportedEncodingException localUnsupportedEncodingException)
       {
         localUnsupportedEncodingException.printStackTrace();
         return false;
       }
     } else {
       str = paramString2;
     }
     if (localUser == null) {
       return false;
     }
     if (localUser.getPasswordHash().equals(str)) {
       return true;
     }
     return false;
   }
   
   public void setSalt(String paramString)
   {
     this.salt = paramString;
   }
   
   public Set<String> getUserKeys()
   {
     return this.users.keySet();
   }
   
   private void load()
   {
     BufferedReader localBufferedReader = null;
     try
     {
       localBufferedReader = new BufferedReader(new FileReader(this.userFile));
       String str = "";
       while ((str = localBufferedReader.readLine()) != null) {
         if ((!str.startsWith("#")) && (!str.startsWith("[")))
         {
           String[] arrayOfString = str.trim().split(":");
           if (arrayOfString.length == 2) {
             addUserWithHash(arrayOfString[0], arrayOfString[1]);
           }
         }
       }
       return;
     }
     catch (IOException localIOException2)
     {
       System.err.println("IOException reading users.txt!");
       localIOException2.printStackTrace();
     }
     finally
     {
       if (localBufferedReader != null) {
         try
         {
           localBufferedReader.close();
         }
         catch (IOException localIOException4) {}
       }
     }
   }
   
   public void save()
   {
     PrintWriter localPrintWriter = null;
     try
     {
       localPrintWriter = new PrintWriter(this.userFile);
       localPrintWriter.println("#You should not need to edit anything in this file!");
       localPrintWriter.println("[users]");
       for (User localUser : this.users.values()) {
         if (localUser.isPersistant()) {
           localPrintWriter.println(localUser.getName() + ":" + localUser.getPasswordHash());
         }
       }
     }
     catch (IOException localIOException)
     {
       System.err.println("IOException writing to users.txt!");
       localIOException.printStackTrace();
     }
     finally
     {
       if (localPrintWriter != null) {
         localPrintWriter.close();
       }
     }
   }
   
   private static final byte[] HEX = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
   
   private static String convertToHexString(byte[] paramArrayOfByte, String paramString)
     throws UnsupportedEncodingException
   {
     byte[] arrayOfByte1 = new byte[paramArrayOfByte.length * 2];
     int i = 0;
     for (int m : paramArrayOfByte)
     {
       arrayOfByte1[(i++)] = HEX[((m & 0xF0) >>> 4)];
       arrayOfByte1[(i++)] = HEX[(m & 0xF)];
     }
     return new String(arrayOfByte1, paramString);
   }
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.drdanick.McRKit.auth...UserManager
 * JD-Core Version:    0.7.0.1
 */