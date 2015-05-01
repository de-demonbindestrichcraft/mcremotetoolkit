// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ConsoleShell.java
package com.drdanick.McRKit.Telnet;

import com.drdanick.McRKit.*;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.wimpi.telnetd.io.BasicTerminalIO;
import net.wimpi.telnetd.net.Connection;
import net.wimpi.telnetd.net.ConnectionEvent;
import net.wimpi.telnetd.shell.Shell;

// Referenced classes of package com.drdanick.McRKit.Telnet:
//            TelnetIOInputStream
public class ConsoleShell
        implements Shell, ConsoleListener {

    public ConsoleShell() {
        enableEcho = true;
        utf8Enabled = false;
    }

    public static Shell createShell() {
        return new ConsoleShell();
    }

    public void run(Connection connection) {
        try {
            connectionRunning = true;
            outputQueue = new LinkedBlockingQueue();
            m_Connection = connection;
            m_IO = m_Connection.getTerminalIO();
            m_Connection.addConnectionListener(this);
            TelnetIOInputStream telnetioinputstream;
            List list;
            Iterator iterator;
            String s4;
            try {
                wrapperInstance = Wrapper.getInstance();
                String s = wrapperInstance.getToolkitProperties().getString("shell-password-mask").trim();
                enableEcho = wrapperInstance.getToolkitProperties().getString("shell-input-echo").trim().equalsIgnoreCase("true");
                utf8Enabled = wrapperInstance.getToolkitProperties().getString("utf8-support-enabled").trim().equalsIgnoreCase("true");
                m_IO.eraseScreen();
                m_IO.homeCursor();
                m_IO.write("RemoteToolkit RemoteShell V0.60\r\n");
                m_IO.flush();
                telnetioinputstream = new TelnetIOInputStream(m_IO, utf8Enabled);
                inputStream = telnetioinputstream;
                telnetioinputstream.setPasswordMask(s);
                if (enableEcho) {
                    telnetioinputstream.setEchoing(true);
                }
                telnetioinputstream.setHistoryEnabled(false);
                m_IO.write("Enter username: ");
                m_IO.flush();
                String s2 = telnetioinputstream.myReadLine("");
                telnetioinputstream.setPasswordMode(true);
                m_IO.write("Enter password: ");
                m_IO.flush();
                String s3 = telnetioinputstream.myReadLine("");
                telnetioinputstream.setPasswordMode(false);
                if (!wrapperInstance.login(s2, s3, false, false)) {
                    m_IO.eraseScreen();
                    m_IO.homeCursor();
                    m_IO.write("Nice try!\r\n");
                    m_IO.flush();
                    connectionRunning = false;
                    m_Connection.close();
                    return;
                }
            } catch (Exception exception) {
                if (exception instanceof EOFException) {
                    System.err.println("Telnet connection terminated remotely, removing connection!");
                } else {
                    System.err.println("Unexpected error in shell!");
                    exception.printStackTrace();
                }
                connectionRunning = false;
                wrapperInstance.deregisterTelnetShell(this);
                m_Connection.close();
                return;
            }
            m_IO.eraseScreen();
            m_IO.homeCursor();
            m_IO.write("Connected to console!\r\n");
            m_IO.flush();
            telnetioinputstream.setHistoryEnabled(true);
            list = wrapperInstance.getPlaybackBuffer();
            for (iterator = list.iterator(); iterator.hasNext(); m_IO.write((new StringBuilder()).append(s4).append("\n").toString())) {
                s4 = (String) iterator.next();
            }
            m_IO.flush();
            wrapperInstance.registerTelnetShell(this);
            Thread createInputThread = createInputThread(telnetioinputstream, wrapperInstance);
            createInputThread.start();
            do {
                try {
                    String s1 = (new StringBuilder()).append((String) outputQueue.take()).append("\r\n").toString();
                    if (!s1.startsWith(">")) {
                        synchronized (inputStream) {
                            inputStream.setEchoing(false);
                            inputStream.clearLine();
                            m_IO.write(s1);
                            System.out.println(s1);
                            m_IO.flush();
                            inputStream.setEchoing(true);
                            inputStream.printCurrentBuffer();
                        }
                    }
                } catch (IOException ioexception) {
                    if (ioexception.toString().equals("java.net.SocketException: Broken pipe")) {
                        connectionRunning = false;
                        wrapperInstance.deregisterTelnetShell(this);
                    } else if (ioexception.toString().trim().startsWith("java.net.SocketException: Connection reset by peer") || ioexception.toString().trim().startsWith("java.net.SocketException: Socket closed")) {
                        System.err.println("Detected a registered Telnet connection that appears to be dead!");
                        connectionRunning = false;
                        wrapperInstance.deregisterTelnetShell(this);
                        try {
                            m_Connection.close();
                        } catch (Exception exception2) {
                        }
                    } else {
                        System.out.println((new StringBuilder()).append("Telnet shell IO exception: ").append(ioexception).toString());
                    }
                } catch (Exception exception1) {
                    if (exception1 instanceof InterruptedException) {
                        System.err.println("Telnet shell output thread interrupted, closing connection!");
                        connectionRunning = false;
                    } else {
                        System.err.println("Unexpected error writing to shell!");
                        exception1.printStackTrace();
                    }
                }
            } while (connectionRunning);
            wrapperInstance.deregisterTelnetShell(this);
            try {
                synchronized (inputStream) {
                    inputStream.clearLine();
                }
            } catch (IOException ioexception1) {
            }
            m_Connection.close();
            return;
        } catch (IOException ex) {
            Logger.getLogger(ConsoleShell.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Thread createInputThread(final TelnetIOInputStream telnetioinputstream, final Wrapper wrapper) {
        if (enableEcho) {
            telnetioinputstream.setEchoing(true);
        }
        final ConsoleShell consoleshell = this;
        Thread t = new Thread() {

            public void run() {
                try {
                    while (connectionRunning) {
                        String s = consoleshell.inputStream.myReadLine(">");
                        if (s.isEmpty()) {
                            continue;
                        }
                        try {
                            if (!s.startsWith(".") || !wrapper.parseConsoleInput(s.substring(1))) {
                                wrapper.writeStringToConsole((new StringBuilder()).append(s).append("\n").toString());
                            }
                        } catch (IOException ioexception) {
                            if (!ioexception.toString().equals("java.io.IOException: Broken pipe")) {
                                connectionRunning = false;
                            }
                        }
                        consoleshell.inputStream.clearCurrentBuffer();
                        s = "";
                    }
                } catch (Exception exception) {
                    System.out.println((new StringBuilder()).append("Telnet input stream error: ").append(exception).toString());
                    exception.printStackTrace(System.out);
                    System.out.println("Telnet input stream closing...");
                }
                connectionRunning = false;

                wrapperInstance.deregisterTelnetShell(consoleshell);

                m_Connection.close();
            }
        };
        t.setDaemon(
                true);

        return t;
    }

    public boolean getConnectionRunning() {
        return connectionRunning;
    }

    public void queueOutputString(String s)
            throws InterruptedException {
        outputQueue.put(s);
    }

    public synchronized void updateConsoleShell(BasicTerminalIO io, Wrapper wrapperInstance, int lastsize) {
    }

    public void connectionTimedOut(ConnectionEvent connectionevent) {
        try {
            wrapperInstance.deregisterTelnetShell(this);
            connectionRunning = false;
            queueOutputString("CONNECTION_TIMEDOUT");
            inputStream.clearLine();
            m_Connection.close();
        } catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
            m_Connection.close();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
            m_Connection.close();
        }
    }

    public void connectionIdle(ConnectionEvent connectionevent) {
        try {
            m_IO.write("CONNECTION_IDLE");
            m_IO.flush();
        } catch (IOException ioexception) {
        }
    }

    public void connectionLogoutRequest(ConnectionEvent connectionevent) {
        try {
            wrapperInstance.deregisterTelnetShell(this);
            connectionRunning = false;
            queueOutputString("CONNECTION_LOGOUTREQUEST");
            inputStream.clearLine();
            m_Connection.close();
        } catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
            m_Connection.close();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
            m_Connection.close();
        }
    }

    public void connectionSentBreak(ConnectionEvent connectionevent) {
        try {
            wrapperInstance.deregisterTelnetShell(this);
            connectionRunning = false;
            queueOutputString("CONNECTION_BREAK");
            inputStream.clearLine();
            m_Connection.close();
        } catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
            m_Connection.close();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
            m_Connection.close();
        }
    }
    
    public static final String VERSION = "0.60";
    private Connection m_Connection;
    private BasicTerminalIO m_IO;
    private Wrapper wrapperInstance;
    private boolean connectionRunning;
    private LinkedBlockingQueue outputQueue;
    private TelnetIOInputStream inputStream;
    private boolean enableEcho;
    private boolean utf8Enabled;
}
