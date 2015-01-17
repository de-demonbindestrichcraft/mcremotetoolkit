// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BackupManager.java
package com.drdanick.McRKit.Backup;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

// Referenced classes of package com.drdanick.McRKit.Backup:
//            BackupIO
public class BackupManager {

    public BackupManager(String s) {
        world = s;
    }

    private String getDateTime() {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return simpledateformat.format(date);
    }

    public void runBackup() {
        try {
            if (!(new File((new StringBuilder()).append(world).append("/backups").toString())).exists()) {
                return;
            }
            BufferedReader bufferedreader;
            bufferedreader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream((new StringBuilder()).append(world).append("/backups/meta.dat").toString()))));
            if (!bufferedreader.readLine().trim().equals(world.trim())) {
                System.out.println("Error: world directory name mismatch!");
                return;
            }
            int ai[] = new int[Integer.parseInt(bufferedreader.readLine().trim())];
            for (int i = 0; i < ai.length; i++) {
                ai[i] = Integer.parseInt(bufferedreader.readLine().trim());
            }

            int j = ai[ai.length - 1];
            bufferedreader.close();
            BufferedReader bufferedreader1 = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream((new StringBuilder()).append(world).append("/backups/").append(j).append("/backup.dat").toString()))));
            HashMap hashmap = new HashMap();
            String s = "";
            bufferedreader1.readLine();
            bufferedreader1.readLine();
            while ((s = bufferedreader1.readLine()) != null) {
                String as[] = s.trim().split(":");
                hashmap.put(as[0], (new StringBuilder()).append(as[1]).append(":").append(as[2]).toString());
            }
            bufferedreader1.close();
            j++;
            PrintWriter printwriter2 = new PrintWriter(new GZIPOutputStream(new FileOutputStream((new StringBuilder()).append(world).append("/backups/meta.dat").toString())), true);
            printwriter2.println(world);
            printwriter2.println(ai.length + 1);
            int ai1[] = ai;
            int k = ai1.length;
            for (int l = 0; l < k; l++) {
                int i1 = ai1[l];
                printwriter2.println(i1);
            }

            printwriter2.println(j);
            printwriter2.close();
            LinkedList linkedlist1 = new LinkedList();
            (new File((new StringBuilder()).append(world).append("/backups/").append(j).toString())).mkdirs();
            BackupIO.loadFolderIntoBackup((new StringBuilder()).append(world).append("/backups/").append(j).toString(), world, hashmap, linkedlist1, j);
            File file2 = new File((new StringBuilder()).append(world).append("/backups/").append(j).append("/backup.dat").toString());
            PrintWriter printwriter3 = new PrintWriter(new GZIPOutputStream(new FileOutputStream(file2)), true);
            printwriter3.println(getDateTime());
            printwriter3.println(linkedlist1.size());
            String s2;
            for (Iterator iterator1 = linkedlist1.iterator(); iterator1.hasNext(); printwriter3.println(s2)) {
                s2 = (String) iterator1.next();
            }

            printwriter3.close();
        } catch (Exception exception) {
            System.out.println((new StringBuilder()).append("Error on creating a backup: ").append(exception).toString());
            exception.printStackTrace();
        }

        try {
            File file = BackupIO.createPathTo((new StringBuilder()).append(world).append("/backups").toString(), "meta.dat");
            PrintWriter printwriter = new PrintWriter(new GZIPOutputStream(new FileOutputStream(file)), true);
            printwriter.println(world);
            printwriter.println("1");
            printwriter.println("0");
            printwriter.close();
            LinkedList linkedlist = new LinkedList();
            BackupIO.loadFolderIntoBackup((new StringBuilder()).append(world).append("/backups/0").toString(), world, null, linkedlist, 0);
            File file1 = new File((new StringBuilder()).append(world).append("/backups/0/backup.dat").toString());
            PrintWriter printwriter1 = new PrintWriter(new GZIPOutputStream(new FileOutputStream(file1)), true);
            printwriter1.println(getDateTime());
            printwriter1.println(linkedlist.size());
            String s1;
            for (Iterator iterator = linkedlist.iterator(); iterator.hasNext(); printwriter1.println(s1)) {
                s1 = (String) iterator.next();
            }

            printwriter1.close();
        } catch (Exception exception1) {
            System.out.println((new StringBuilder()).append("Error on creating an initial backup: ").append(exception1).toString());
            exception1.printStackTrace();
        }
    }

    public void restoreBackup(String s, String s1, int i) {
        try {
            BufferedReader bufferedreader;
            LinkedList linkedlist;
            bufferedreader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream((new StringBuilder()).append(s).append("/backups/").append(i).append("/backup.dat").toString()))));
            linkedlist = new LinkedList();
            String s2 = "";
            String s3;
            while ((s3 = bufferedreader.readLine()) != null) {
                linkedlist.add(s3);
            }
            BackupIO.restoreBackup((new StringBuilder()).append(s).append("/backups").toString(), s1, s, linkedlist);
            bufferedreader.close();

            bufferedreader.close();
        } catch (IOException ex) {
            Logger.getLogger(BackupManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println((new StringBuilder()).append("Error on restoring backup: ").append(ex).toString());
        }
    }

    public void listBackups() {
        try {
            ArrayList arraylist = new ArrayList();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream((new StringBuilder()).append(world).append("/backups/meta.dat").toString()))));
            File file = new File((new StringBuilder()).append(world).append("/backups/meta.dat").toString());
            System.out.println((new StringBuilder()).append("READING BACKUP INDEX IN: ").append(file.getAbsolutePath()).toString());
            System.out.println((new StringBuilder()).append("READING BACKUP INDEX IN: ").append(file.getCanonicalPath()).toString());
            String s = bufferedreader.readLine();
            System.out.println((new StringBuilder()).append("Backups for \"").append(s).append("\":").toString());
            int i = Integer.parseInt(bufferedreader.readLine());
            for (int j = 0; j < i; j++) {
                int k = Integer.parseInt(bufferedreader.readLine());
                BufferedReader bufferedreader1 = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream((new StringBuilder()).append(s).append("/backups/").append(k).append("/backup.dat").toString()))));
                System.out.println((new StringBuilder()).append("Backup #").append(k + 1).append(" @ ").append(bufferedreader1.readLine()).append(" -> ").append(bufferedreader1.readLine()).append(" files.").toString());
                bufferedreader1.close();
            }

        } catch (Exception exception) {
            System.out.println((new StringBuilder()).append("Error: ").append(exception).toString());
            exception.printStackTrace();
        }
    }
    private String world;
}
