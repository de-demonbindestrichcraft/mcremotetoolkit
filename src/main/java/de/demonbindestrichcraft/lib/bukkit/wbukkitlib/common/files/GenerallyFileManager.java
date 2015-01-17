package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public class GenerallyFileManager {

    public static boolean CreateNewDirAndFile(String mainDirectory, File file) {
        boolean ReturnOutput = false;
        boolean DirExists = new File(mainDirectory).isDirectory();

        if (!DirExists) {
            boolean DirIsCreate;
            DirIsCreate = new File(mainDirectory).mkdir();
            if (!DirIsCreate) {
                return false;
            }
        }
        DirExists = new File(mainDirectory).isDirectory();

        if (DirExists) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    ReturnOutput = true;
                } catch (IOException ex) {
                    Logger.getLogger(GenerallyFileManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return ReturnOutput;
    }

    public static ArrayList AllgemeinFileReader(File file) throws IOException {
        ArrayList AllgemeinFileList = new ArrayList();
        String temp;
        boolean ALL_SUCCESS = false;
        BufferedReader FileReadBuffer = null;
        FileReader FileRead = null;
        try {
            FileRead = new FileReader(file);
            try {
                FileReadBuffer = new BufferedReader(FileRead);
                int zaehler = 0;
                do {
                    temp = FileReadBuffer.readLine();
                    zaehler++;
                    if (temp != null) {
                        AllgemeinFileList.add(temp);
                    }
                } while (temp != null);
                if (zaehler > 1) {
                    ALL_SUCCESS = true;
                }
            } catch (IOException ex) {
                Logger.getLogger(GenerallyFileManager.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (FileReadBuffer != null) {
                    FileReadBuffer.close();
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerallyFileManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (FileRead != null) {
                FileRead.close();
            }
        }

        if (ALL_SUCCESS == false) {
            AllgemeinFileList.add("error");
            return AllgemeinFileList;
        } else {
            return AllgemeinFileList;
        }
    }

    public static boolean FileWrite(List<String> list, File file) {
        FileWriter FileWrite = null;
        BufferedWriter FileBuffWriter = null;
        PrintWriter FileWritePrint = null;
        try {
            FileWrite = new FileWriter(file);
            FileBuffWriter = new BufferedWriter(FileWrite);
            FileWritePrint = new PrintWriter(FileBuffWriter, false);
            for (String value : list) {
                FileWritePrint.println(value);
            }
            FileWritePrint.flush();
        } catch (IOException ex) {
            Logger.getLogger(GenerallyFileManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (FileWritePrint != null) {
                FileWritePrint.close();
            }
        }
        return true;
    }

    public static boolean FileWrite(Map<String, String> writeMap, File file, String split) {
        FileWriter FileWrite = null;
        BufferedWriter FileBuffWriter = null;
        PrintWriter FileWritePrint = null;
        try {
            FileWrite = new FileWriter(file);
            FileBuffWriter = new BufferedWriter(FileWrite);
            FileWritePrint = new PrintWriter(FileBuffWriter, false);
            Map<String, String> temp = new HashMap<String, String>(writeMap);
            Set<String> keyList = new HashSet<String>(temp.keySet());
            for (String key : keyList) {
                FileWritePrint.println(key + split + temp.get(key));
            }
            FileWritePrint.flush();
        } catch (IOException ex) {
            Logger.getLogger(GenerallyFileManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (FileWritePrint != null) {
                FileWritePrint.close();
            }
        }
        return true;
    }

    public static int ToInt(String ConvertString) {
        //String Is Number
        char[] ConvertStringArray = ConvertString.toCharArray();
        for (int i = 0; i < ConvertString.length(); i++) {
            if (((int) ConvertStringArray[i] - 48) > 9 || ((int) ConvertStringArray[i] - 48) < 0) {
                return -1;
            }
        }
        int malnehmen = 1;
        int ergebnis = 0;
        int ConvertStringLaenge = ConvertString.length() - 1;
        for (int i = 0; i < ConvertStringLaenge; i++) {
            malnehmen = malnehmen * 10;
        }

        ConvertStringLaenge++;
        for (int i = 0; i < ConvertStringLaenge; i++) {
            ergebnis += ((int) ConvertStringArray[i] - 48) * malnehmen;
            malnehmen = malnehmen / 10;
        }
        return ergebnis;
    }

    public static long ToLong(String ConvertString) {
        //String Is Number
        char[] ConvertStringArray = ConvertString.toCharArray();
        String LongToConvert = new String();
        boolean StringHasLong = false;
        for (int i = 0; i < ConvertString.length(); i++) {
            if (((int) ConvertStringArray[i] - 48) > 9 || ((int) ConvertStringArray[i] - 48) < 0) {
                continue;
            }
            LongToConvert += ConvertStringArray[i];
            StringHasLong = true;
        }
        if (StringHasLong == false) {
            return -1;
        }
        long malnehmen = 1;
        long ergebnis = 0;
        int ConvertStringLaenge = LongToConvert.length() - 1;
        for (int i = 0; i < ConvertStringLaenge; i++) {
            malnehmen = malnehmen * 10;
        }

        ConvertStringLaenge++;
        for (int i = 0; i < ConvertStringLaenge; i++) {
            ergebnis += (((int) ConvertStringArray[i]) - 48) * malnehmen;
            malnehmen = malnehmen / 10L;
        }
        return ergebnis;
    }

    public static String[] StringSplitToStringArray(String query, File file, String splitchar) {
        String AllgemeinList = FileReadLine(query, file, splitchar);
        String[] split = null;
        if (AllgemeinList.equals("error")) {
            Logger.getLogger(GenerallyFileManager.class.getName()).log(Level.SEVERE, "Allgemeinlist.equals(error)");
            return null;
        }
        if (!AllgemeinList.contains(splitchar)) {
            Logger.getLogger(GenerallyFileManager.class.getName()).log(Level.SEVERE, "!AllgemeinList.contains(splitchar)");
            return null;
        }

        split = AllgemeinList.split(splitchar);
        if (split == null) {
            Logger.getLogger(GenerallyFileManager.class.getName()).log(Level.SEVERE, "PlotFileManager: split == null");
            return null;
        } else {
            return split;
        }
    }

    public static Map<String, String> AllgemeinFileReader(File file, String splitchar) {
        try {
            if (file.exists()) {
                List<String> AllgemeinFileReader = GenerallyFileManager.AllgemeinFileReader(file);
                if (AllgemeinFileReader.get(0).equals("error")) {
                    return null;
                }
                Map<String, String> input = new HashMap<String, String>();
                for (String value : AllgemeinFileReader) {

                    if (!value.contains(splitchar)) {
                        continue;
                    }
                    String[] split = value.split(splitchar);
                    input.put(split[0], split[1]);
                }
                return input;
            }
        } catch (IOException ex) {
            return null;
        }
        return null;
    }

    public static String FileReadLine(String Inhalt, File file, String splitstring) {
        ArrayList AllgemeinOutput = null;
        try {
            AllgemeinOutput = AllgemeinFileReader(file);
        } catch (IOException ex) {
            Logger.getLogger(GenerallyFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (AllgemeinOutput == null) {
            return "error";
        }
        if (((String) AllgemeinOutput.get(0)).equals("error")) {
            return "error";
        } else {
            String[] split = null;
            for (int i = 0; i < AllgemeinOutput.size(); i++) {
                //Logger.getLogger(PlotFileManager.class.getName()).log(Level.INFO, "Inhalt:"+Inhalt);
                split = ((String) AllgemeinOutput.get(i)).split(splitstring);
                //Logger.getLogger(PlotFileManager.class.getName()).log(Level.INFO, "split[0]:"+split[0]);
                //Logger.getLogger(PlotFileManager.class.getName()).log(Level.INFO, "split[1]:"+split[1]);
                if (split[0].equals(Inhalt)) {
                    return split[1];
                }
            }
            return "error";
        }
    }

    public static Exception StreamCopy(File source, File destination) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(buffer, 0, 1024)) > 0) {
                fileOutputStream.write(buffer, 0, len);
                fileOutputStream.flush();
            }
            return null;
        } catch (Exception ex) {
            return ex;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ex) {
                    return ex;
                }
            }

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception ex) {
                    return ex;
                }
            }
        }
    }

    public static Exception StreamCopy(String source, String destination) {
        return StreamCopy(new File(source), new File(destination));
    }
}