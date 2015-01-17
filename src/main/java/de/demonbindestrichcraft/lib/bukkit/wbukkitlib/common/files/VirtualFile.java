/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ABC
 */
public final class VirtualFile {

    private File file;
    private List<String> fileList;

    public VirtualFile() throws FileNotFoundException, IOException {
        init();
    }

    public VirtualFile(File file, boolean createNewFile) throws FileNotFoundException, IOException {
        init(file, createNewFile);
    }

    public void init() throws FileNotFoundException, IOException {
        this.file = null;
        this.fileList = null;
        if (this.file != null) {
            throw new FileNotFoundException();
        }
        if (this.fileList != null) {
            throw new IOException();
        }
    }

    public void init(File file, boolean createNewFile) throws FileNotFoundException, IOException {
        this.file = file;
        this.fileList = new LinkedList<String>();
        if (!this.file.exists() && createNewFile) {
            this.file.createNewFile();
        } else {
            boolean readFile = readFile();
            if (!readFile) {
                throw new IOException(file.getName() + " can not read!");
            }
        }
    }

    private boolean readFile() throws FileNotFoundException, IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        if (file.canRead()) {
            String line = null;
            line = bufferedReader.readLine();
            int i = 0;
            for (i = 0; line != null; i++) {
                fileList.add(line);
                line = bufferedReader.readLine();
            }
            return i == 0;
        } else {
            return false;
        }

    }

    public boolean writeFile() throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        PrintWriter printWriter = new PrintWriter(bufferedWriter);
        if (!(fileList instanceof List) || !file.canWrite()) {
            return false;
        }

        for (String line : fileList) {
            printWriter.println(line);
        }
        return true;
    }

    public File getFile() {
        return file;
    }

    public List<String> getFileContent() {
        List<String> temp = new LinkedList<String>(fileList);
        return temp;
    }

    public void add(String line) {
        fileList.add(line);
    }

    public boolean remove(String line) {
        return fileList.remove(line);
    }

    public String remove(int index) {
        return fileList.remove(index);
    }

    public boolean contains(String line) {
        List<String> tempList = getFileContent();
        for (String temp : tempList) {
            if (line.equals(temp)) {
                return true;
            }
        }
        return false;
    }
}
