// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BackupIO.java

package com.drdanick.McRKit.Backup;

import java.io.*;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class BackupIO
{

    public BackupIO()
    {
    }

    public static void loadFolderIntoBackup(String s, String s1, HashMap hashmap, LinkedList linkedlist, int i)
    {
        File file = new File(s1);
        if(!file.exists())
            return;
        File afile[] = file.listFiles();
        File afile1[] = afile;
        int j = afile1.length;
        for(int k = 0; k < j;)
        {
            File file1 = afile1[k];
            if(file1.isDirectory())
            {
                if(!file1.getName().equals("backups"))
                    loadFolderIntoBackup(s, file1.getPath(), hashmap, linkedlist, i);
                continue;
            }
            try
            {
                String s2;
                if(isAChunk(file1.getName()))
                    s2 = getMD5Checksum(file1.getPath(), true);
                else
                    s2 = getMD5Checksum(file1.getPath(), false);
                if(hashmap != null)
                {
                    String s3 = (String)hashmap.get((new StringBuilder()).append(s1).append("/").append(file1.getName()).toString());
                    String s4 = s3.split(":")[1];
                    if(s4 == null || !s4.equals(s2))
                    {
                        System.out.println((new StringBuilder()).append(s1).append("/").append(file1.getName()).append(" has changed!").toString());
                        linkedlist.add((new StringBuilder()).append(s1).append("/").append(file1.getName()).append(":").append(i).append(":").append(s2).toString());
                        copyFile(file1, createPathTo((new StringBuilder()).append(s).append("/").append(s1).append("/").append(file1.getName()).toString()));
                    } else
                    {
                        linkedlist.add((new StringBuilder()).append(s1).append("/").append(file1.getName()).append(":").append(s3).toString());
                    }
                } else
                {
                    linkedlist.add((new StringBuilder()).append(s1).append("/").append(file1.getName()).append(":").append(i).append(":").append(s2).toString());
                    copyFile(file1, createPathTo((new StringBuilder()).append(s).append("/").append(s1).toString(), file1.getName()));
                }
                continue;
            }
            catch(Exception exception)
            {
                System.out.println((new StringBuilder()).append("[ERROR] Exception while backing up ").append(file1.getName()).append(": ").append(exception).toString());
                exception.printStackTrace();
                k++;
            }
        }

    }

    public static void restoreBackup(String s, String s1, String s2, LinkedList linkedlist)
    {
        int i = 1;
        Iterator iterator = linkedlist.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s3 = (String)iterator.next();
            if(i++ > 2)
                try
                {
                    String as[] = s3.split(":");
                    String s4 = (new StringBuilder()).append(as[1]).append("/").append(as[0]).toString();
                    copyFile(new File((new StringBuilder()).append(s).append("/").append(s4).toString()), createPathTo((new StringBuilder()).append(s1).append(as[0]).toString()));
                }
                catch(Exception exception)
                {
                    System.out.println(exception);
                    exception.printStackTrace();
                }
        } while(true);
    }

    public static void copyFile(File file, File file1)
        throws IOException
    {
        FileChannel filechannel;
        FileChannel filechannel1;
        filechannel = (new FileInputStream(file)).getChannel();
        filechannel1 = (new FileOutputStream(file1)).getChannel();
        try
        {
            filechannel.transferTo(0L, filechannel.size(), filechannel1);
        }
        catch(IOException ioexception)
        {
            throw ioexception;
        }
        if(filechannel != null)
            filechannel.close();
        if(filechannel1 != null)
            filechannel1.close();
        if(filechannel != null)
            filechannel.close();
        if(filechannel1 != null)
            filechannel1.close();
    }

    public static File createPathTo(String s, String s1)
    {
        if(!(new File(s)).exists())
            (new File(s)).mkdirs();
        return new File(s, s1);
    }

    public static File createPathTo(String s)
        throws IOException
    {
        if(!(new File(s)).exists())
        {
            File file = new File(s);
            file.mkdirs();
            file.delete();
        }
        return new File(s);
    }

    public static void mapChecksums(String s, HashMap hashmap, int i)
    {
        File file = new File(s);
        if(!file.exists())
            return;
        File afile[] = file.listFiles();
        File afile1[] = afile;
        int j = afile1.length;
        for(int k = 0; k < j; k++)
        {
            File file1 = afile1[k];
            if(file1.isDirectory())
            {
                mapChecksums(file1.getPath(), hashmap, i);
                continue;
            }
            try
            {
                if(isAChunk(file1.getName()))
                    hashmap.put((new StringBuilder()).append(s).append("/").append(file1.getName()).toString(), (new StringBuilder()).append(i).append(":").append(getMD5Checksum(file1.getPath(), true)).toString());
                else
                    hashmap.put((new StringBuilder()).append(s).append("/").append(file1.getName()).toString(), (new StringBuilder()).append(i).append(":").append(getMD5Checksum(file1.getPath(), false)).toString());
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }
        }

    }

    public static String getMD5Checksum(String s, boolean flag)
        throws Exception
    {
        byte abyte0[] = null;
        try
        {
            abyte0 = createChecksum(s, flag);
        }
        catch(Exception exception)
        {
            System.out.println((new StringBuilder()).append("Error while processing ").append(s).append(": ").append(exception).toString());
            System.out.println("Attempting an alternate hashing method...");
            abyte0 = createChecksum(s, !flag);
        }
        return (new BigInteger(abyte0)).toString(36);
    }

    private static byte[] createChecksum(String s, boolean flag)
        throws Exception
    {
        Object obj = null;
        if(!flag)
        {
            obj = new FileInputStream(s);
        } else
        {
            obj = new BufferedInputStream(new GZIPInputStream(new FileInputStream(s)));
            ((InputStream) (obj)).skip(49000L);
            findString(((InputStream) (obj)), "Blocks", s);
        }
        if(!flag)
        {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            byte abyte0[] = new byte[1024];
            int i;
            do
            {
                i = ((InputStream) (obj)).read(abyte0);
                if(i > 0)
                    messagedigest.update(abyte0, 0, i);
            } while(i != -1);
            ((InputStream) (obj)).close();
            return messagedigest.digest();
        }
        MessageDigest messagedigest1 = MessageDigest.getInstance("MD5");
        byte abyte1[] = new byte[32768];
        int j;
        do
        {
            j = ((InputStream) (obj)).read(abyte1);
            if(j > 0)
                messagedigest1.update(abyte1, 0, j);
        } while(j != -1);
        ((InputStream) (obj)).close();
        return messagedigest1.digest();
    }

    public static int findString(InputStream inputstream, String s, String s1)
        throws Exception
    {
        int i = 0;
        inputstream.mark(1);
        for(byte abyte0[] = new byte[s.length()]; inputstream.read(abyte0, 0, abyte0.length) > -1;)
        {
            inputstream.reset();
            inputstream.skip(1L);
            if((new String(abyte0)).equals(s))
            {
                for(int j = 0; j < s.length() + 3; j++);
                return i;
            }
            inputstream.mark(s.length() + 1);
            i++;
        }

        return i;
    }

    public static boolean isAChunk(String s)
    {
        return s.startsWith("c.") && s.endsWith(".dat");
    }

    static final byte HEX_CHAR_TABLE[] = {
        48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 
        97, 98, 99, 100, 101, 102
    };

}
