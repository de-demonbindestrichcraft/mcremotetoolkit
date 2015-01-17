package com.drdanick.McRKit.Telnet;

import com.drdanick.McRKit.*;

import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files.ThreadSafeTelnet;
import java.awt.Font;
import java.awt.Panel;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.wimpi.telnetd.io.BasicTerminalIO;

public class TelnetIOInputStream
        extends InputStream {

    private static final int HISTORY_LIMIT = 100;
    private BasicTerminalIO tio;
    private boolean echoing;
    private boolean pMode;
    private char pMask;
    private CopyOnWriteArrayList<String> history = new CopyOnWriteArrayList<String>();
    private ConcurrentHashMap<String, Integer> historyLookup = new ConcurrentHashMap<String, Integer>();
    private ConcurrentHashMap<Integer, String> historyLookupI = new ConcurrentHashMap<Integer, String>();
    private boolean historyEnabled = false;
    private String s = "";
    private int sWidth = 0;
    private String prompt = "";
    private ListIterator<String> historyIterator = null;
    private boolean promptPrinted = false;
    private boolean historySearchIsForward = true;
    private boolean utf8Enabled;
    private boolean readUtf8Char = false;
    private byte[] utf8Char;
    private MyFontMetrics fontMetrics = new MyFontMetrics(new Panel(), new Font("Monospaced", 0, 5));
    private char upKey = 'A';
    private char downKey = 'B';
    private char lastChar = 0;
    private int index = 0;
    private int arrowUpKey = 1001;
    private int arrowDownKey = 1002;
    private int arrowLeftKey = 1004;
    private int arrowRightKey = 1003;
    private int deleteKey = 1202;
    private int backKey = 1302;
    private int lastArrowKey = 0;

    public TelnetIOInputStream(BasicTerminalIO paramBasicTerminalIO, boolean paramBoolean) {
        this.tio = paramBasicTerminalIO;
        this.utf8Enabled = paramBoolean;

        this.echoing = false;
        this.pMode = false;
        this.pMask = '\000';
    }

    public synchronized int read()
            throws IOException {
        return this.tio.read();
    }

    public String myReadLine(String paramString) throws IOException {
        int i;
        String my = "";
        int sublength = 0;
        char lastChar;

            if (prompt != null && !prompt.isEmpty() && paramString == null || paramString.isEmpty()) {
                paramString = prompt;
            }
            if (paramString != null && !paramString.isEmpty()) {
                tio.write(paramString);
                tio.flush();
                sublength = paramString.length();
                prompt = paramString;
            } else {
                paramString = "";
            }

            do {
                i = this.tio.read();
                //System.out.println(i);
                lastChar = (char) i;
                if (i == arrowLeftKey) {
                    this.tio.moveLeft(1);
                    return "";
                } else if (i == arrowRightKey) {
                    this.tio.moveRight(1);
                    return "";
                } else if (i == deleteKey) {
                    this.tio.eraseScreen();
                    return "";
                } else if (i == backKey) {
                    this.tio.eraseToBeginOfLine();
                    return "";
                }
                if (historyEnabled) {
                    if (i == arrowUpKey || i == arrowDownKey) {
                        if (history.size() <= 0) {
                            s = my;
                            this.clearLine();
                            return "";
                        }
                        final int keyPress = i - arrowUpKey;
                        switch (keyPress) {
                            case 0: {
                                index++;
                            }
                            break;

                            case 1: {
                                index--;
                            }
                            break;
                        }
                        if (index < 0) {
                            index = history.size() - 1;
                        } else if (index >= history.size()) {
                            index = 0;
                        }
                        s = my;
                        this.clearLine();
                        try {
                            this.tio.write(historyLookupI.get(index));
                            this.tio.flush();
                        } catch (Exception ex) {
                            return "";
                        }
                        this.lastArrowKey = i;
                        return "";
                    } else if (this.lastArrowKey > 1000 && this.lastArrowKey <= 1002 && (lastChar == '\n' || lastChar == '\r')) {
                        lastArrowKey = 0;
                        return historyLookupI.get(index);
                    }
                }
                my = new StringBuilder().append(my).append(lastChar).toString();
                if (this.utf8Enabled) {
                    my = new String(my.getBytes("UTF-8"), "UTF-8");
                }
                lastChar = my.charAt(my.length() - 1);
                this.lastChar = lastChar;
                if (echoing) {
                    if (!pMode) {
                        tio.write(this.lastChar);
                        tio.flush();
                    } else {
                        if (pMask != '\000') {
                            tio.write(pMask);
                            tio.flush();
                        }
                    }
                }
            } while (i != 10 && i != 13);
            if (!my.startsWith(paramString) || paramString.isEmpty()) {
                history.add(my);
                historyLookup.put(my, my.length());
                historyLookupI.put(historyLookup.size() - 1, my);
                s = my;
                return my;
            } else {
                my = my.substring(sublength);
                history.add(my);
                historyLookup.put(my, my.length());
                historyLookupI.put(historyLookup.size() - 1, my);
                s = my;
                return my;
            }
    }

    public synchronized String readLine(String paramString)
            throws IOException {
        synchronized (this) {
            this.prompt = paramString;
            this.s = "";
            this.sWidth = 0;
            this.tio.write(paramString);
            this.promptPrinted = true;
            this.historySearchIsForward = true;
        }
        int i = 0;
        while ((i = this.tio.read()) != 10) {
            int k;
            if ((i != 1303) && (i != 1302) && (i != 1001) && (i != 1002) && (i != 1004) && (i != 1003)) {
                if (!this.utf8Enabled) {
                    this.s += (char) i;
                    this.sWidth += 1;
                } else {
                    this.readUtf8Char = false;
                    i &= 0xFF;
                    if ((i & 0x80) != 0) {
                        this.readUtf8Char = true;
                        int j;
                        for (j = 0; j < 5; j++) {
                            if ((i >> 5 - j & 0x1) == 0) {
                                break;
                            }
                        }
                        this.utf8Char = new byte[j + 2];
                        this.utf8Char[0] = ((byte) i);
                        for (k = 1; k < j + 2; k++) {
                            this.utf8Char[k] = ((byte) (this.tio.read() & 0xFF));
                        }
                        this.s = this.s.concat(new String(this.utf8Char, "UTF-8"));
                        this.sWidth += this.fontMetrics.wcwidth(this.s.charAt(this.s.length() - 1));
                    } else {
                        this.s += (char) i;
                        this.sWidth += 1;
                    }
                }
            }
            if (((i == 1004) || (i != 1003))
                    || (i == 1001)) {
                if (this.historyEnabled) {
                    if (this.historyIterator == null) {
                        this.historyIterator = this.history.listIterator(0);
                    }
                    if (!this.historySearchIsForward) {
                        this.historySearchIsForward = true;
                        this.historyIterator.next();
                    }
                    if (this.historyIterator.hasNext()) {
                        synchronized (this) {
                            this.tio.eraseLine();
                            this.tio.moveLeft(this.sWidth + paramString.length());

                            this.tio.write(paramString);
                            this.tio.flush();
                            this.s = ((String) this.historyIterator.next());
                            this.sWidth = ((Integer) this.historyLookup.get(this.s)).intValue();
                            this.tio.write(this.s);

                            this.tio.flush();
                        }
                    }
                }
            } else if (i == 1002) {
                if ((this.historyEnabled) && (this.historyIterator != null)) {
                    synchronized (this) {
                        if ((this.historySearchIsForward) && (this.historyIterator.hasPrevious())) {
                            this.historySearchIsForward = false;
                            this.historyIterator.previous();
                        }
                        if (this.historyIterator.hasPrevious()) {
                            this.tio.eraseLine();
                            this.tio.moveLeft(this.sWidth + paramString.length());

                            this.tio.write(paramString);
                            this.s = ((String) this.historyIterator.previous());
                            this.sWidth = ((Integer) this.historyLookup.get(this.s)).intValue();
                            this.tio.write(this.s);
                            this.tio.flush();
                        } else {
                            this.historyIterator = null;
                            this.historySearchIsForward = true;
                            this.tio.eraseLine();
                            this.tio.moveLeft(this.sWidth + paramString.length());

                            this.tio.write(paramString);
                            this.s = "";
                            this.sWidth = 0;
                            this.tio.flush();
                        }
                    }
                }
            } else if ((i == 1303) || (i == 1302)) {
                if (this.s.length() > 0) {
                    if (this.echoing) {
                        synchronized (this) {
                            k = this.fontMetrics.wcwidth(this.s.charAt(this.s.length() - 1));
                            this.sWidth -= k;
                            if (this.pMode) {
                                if (this.pMask != 0) {
                                    this.tio.moveLeft(k);
                                    this.tio.eraseToEndOfLine();
                                }
                            } else {
                                this.tio.moveLeft(k);
                                this.tio.eraseToEndOfLine();
                            }
                        }
                    }
                    synchronized (this) {
                        this.s = this.s.substring(0, this.s.length() - 1);
                    }
                }
            }

            if (echoing) {
                synchronized (this) {
                    tio.write("\n");
                    tio.flush();
                }
            }
            if (historyEnabled && !s.trim().isEmpty()) {
                if (historyLookup.remove(s) != null) {
                    history.remove(s);
                }
                history.add(0, s);
                historyLookup.put(s, Integer.valueOf(sWidth));
                if (history.size() > 100) {
                    historyLookup.remove(history.remove(history.size() - 1));
                }
            }
        }
        String s2 = "";
        synchronized (this) {
            historyIterator = null;
            s2 = s;
            s = "";
            sWidth = 0;
            promptPrinted = false;
        }
        return s2;
    }

    public void setPasswordMask(String s1) {
        if (s1.length() > 0) {
            pMask = s1.charAt(0);
        }
    }

    public void setPasswordMode(boolean flag) {
        pMode = flag;
    }

    public void setEchoing(boolean flag) {
        echoing = flag;
    }

    public void setHistoryEnabled(boolean flag) {
        historyEnabled = flag;
    }

    public void printCurrentBuffer()
            throws IOException {
        if (promptPrinted) {
            tio.write(prompt);
        }
        tio.write(s);
        tio.flush();
    }
    
    public void clearLine()
            throws IOException {
        tio.eraseLine();
        tio.moveLeft(s.length() + prompt.length() + 1);
        tio.flush();
    }
    
    public void clearCurrentBuffer()
    {
        s="";
    }
}
/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.drdanick.McRKit.Telnet...TelnetIOInputStream
 * JD-Core Version:    0.7.0.1
 */
