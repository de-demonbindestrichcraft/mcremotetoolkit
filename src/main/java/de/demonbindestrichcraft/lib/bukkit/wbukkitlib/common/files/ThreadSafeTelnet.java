/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files;

import java.io.IOException;
import net.wimpi.telnetd.io.BasicTerminalIO;

/**
 *
 * @author ABC
 */
public class ThreadSafeTelnet implements BasicTerminalIO {
    
    private BasicTerminalIO io;
    public ThreadSafeTelnet(BasicTerminalIO basicTerminalIO)
    {
        this.io = basicTerminalIO;
    }

    public synchronized int read() throws IOException {
        return this.io.read();
    }

    public synchronized void write(byte b) throws IOException {
        this.io.write(b);
    }

    public synchronized void write(char ch) throws IOException {
        this.io.write(ch);
    }

    public synchronized void write(String str) throws IOException {
        this.io.write(str);
    }

    public synchronized void setCursor(int row, int col) throws IOException {
        this.io.setCursor(row, col);
    }

    public synchronized void moveCursor(int direction, int times) throws IOException {
        this.io.moveCursor(direction, times);
    }

    public synchronized void moveRight(int times) throws IOException {
        this.io.moveRight(times);
    }

    public synchronized void moveLeft(int times) throws IOException {
        this.io.moveLeft(times);
    }

    public synchronized void moveUp(int times) throws IOException {
        this.io.moveUp(times);
    }

    public synchronized void moveDown(int times) throws IOException {
        this.io.moveDown(times);
    }

    public synchronized void homeCursor() throws IOException {
        this.io.homeCursor();
    }

    public synchronized void storeCursor() throws IOException {
        this.io.storeCursor();
    }

    public synchronized void restoreCursor() throws IOException {
        this.io.restoreCursor();
    }

    public synchronized void eraseToEndOfLine() throws IOException {
        this.io.eraseToEndOfLine();
    }

    public synchronized void eraseToBeginOfLine() throws IOException {
        this.io.eraseToBeginOfLine();
    }

    public synchronized void eraseLine() throws IOException {
        this.eraseLine();
    }

    public synchronized void eraseToEndOfScreen() throws IOException {
        this.eraseToEndOfScreen();
    }

    public synchronized void eraseToBeginOfScreen() throws IOException {
        this.eraseToBeginOfScreen();
    }

    public synchronized void eraseScreen() throws IOException {
        this.eraseScreen();
    }

    public synchronized void setForegroundColor(int color) throws IOException {
        this.setForegroundColor(color);
    }

    public synchronized void setBackgroundColor(int color) throws IOException {
        this.setBackgroundColor(color);
    }

    public synchronized void setBold(boolean b) throws IOException {
        this.io.setBold(b);
    }

    public synchronized void forceBold(boolean b) {
        this.io.forceBold(b);
    }

    public synchronized void setItalic(boolean b) throws IOException {
        this.io.setItalic(b);
    }

    public synchronized void setUnderlined(boolean b) throws IOException {
        this.io.setUnderlined(b);
    }

    public synchronized void setBlink(boolean b) throws IOException {
        this.io.setBlink(b);
    }

    public synchronized void resetAttributes() throws IOException {
        this.io.resetAttributes();
    }

    public synchronized void bell() throws IOException {
        this.io.bell();
    }

    public synchronized void flush() throws IOException {
        this.io.flush();
    }

    public synchronized void close() throws IOException {
        this.io.close();
    }

    public synchronized void setTerminal(String terminalname) throws IOException {
        this.io.setTerminal(terminalname);
    }

    public synchronized void setDefaultTerminal() throws IOException {
        this.io.setDefaultTerminal();
    }

    public int getRows() {
        return this.io.getRows();
    }

    public int getColumns() {
        return this.io.getColumns();
    }

    public synchronized void setSignalling(boolean b) {
        this.io.setSignalling(b);
    }

    public boolean isSignalling() {
        return this.io.isSignalling();
    }

    public synchronized void setAutoflushing(boolean b) {
        this.io.setAutoflushing(b);
    }

    public boolean isAutoflushing() {
        return this.io.isAutoflushing();
    }

    public synchronized void resetTerminal() throws IOException {
        this.io.resetTerminal();
    }

    public synchronized void setLinewrapping(boolean b) throws IOException {
        this.io.setLinewrapping(b);
    }

    public boolean isLineWrapping() throws IOException {
        return this.io.isLineWrapping();
    }

    public boolean defineScrollRegion(int topmargin, int bottommargin) throws IOException {
        return this.io.defineScrollRegion(topmargin, bottommargin);
    }
    
}
