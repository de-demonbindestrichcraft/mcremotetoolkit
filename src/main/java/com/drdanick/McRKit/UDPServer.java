// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UDPServer.java
package com.drdanick.McRKit;

import java.io.PrintStream;
import java.net.*;
import java.util.Iterator;
import java.util.LinkedList;

// Referenced classes of package com.drdanick.McRKit:
//            UDPServerListener
public class UDPServer {

    public UDPServer(int i, String s) {
        registeredListeners = new LinkedList();
        isRefreshing = true;
        running = false;
        toStop = false;
        dgSocket = null;
        refreshThreadRunning = false;
        refreshRunning = false;
        port = i;
        host = s.trim();
    }

    public void start() {
        toStop = false;
        if (!running) {
            running = true;
            isRefreshing = true;
            runUDPServer();
        }
    }

    public void requestStop() {
        toStop = true;
        isRefreshing = false;
    }

    public void stop() {
        dgSocket.close();
    }

    private void runUDPServer() {
        (new Thread(new Runnable() {

            public void run() {
                long l = 0L;
                int i = 0;
                refreshRunning = false;
                do {
                    try {
                        if (!refreshRunning) {
                            System.out.println((new StringBuilder()).append("Starting UDP listen server on port ").append(port).toString());
                        }
                        refreshRunning = false;
                        byte abyte0[] = new byte[0x10000];
                        final DatagramSocket ds = host.length() <= 0 ? new DatagramSocket(port) : new DatagramSocket(port, InetAddress.getByName(host));
                        dgSocket = ds;
                        if (!refreshThreadRunning) {
                            refreshThreadRunning = true;
                            new Thread(new Runnable() {

                                public void run() {
                                    try {
                                        Thread.sleep(0x36ee80L);
                                    } catch (Exception exception1) {
                                        System.err.println((new StringBuilder()).append("WARNING: UDP server sleep thread exception: ").append(exception1).toString());
                                    }
                                    refreshThreadRunning = false;
                                    refreshRunning = true;
                                    ds.close();
                                }
                            }).start();
                        }
                        do {
                            byte abyte1[] = new byte[0x10000];
                            DatagramPacket datagrampacket = new DatagramPacket(abyte1, abyte1.length);
                            ds.receive(datagrampacket);
                            byte abyte2[] = datagrampacket.getData();
                            String s = (new String(abyte2)).trim();
                            fireUDPServerEvent(ds, datagrampacket, s);
                        } while (!toStop);
                    } catch (BindException bindexception) {
                        try {
                            System.err.println((new StringBuilder()).append("ERROR: There was an issue binding the UDP server to port ").append(port).append(": ").append(bindexception).toString());
                            if (System.currentTimeMillis() - l < 8000L) {
                                l = System.currentTimeMillis();
                                if (i++ < 6) {
                                    System.out.println("Attempting to refresh UDP server in 5 seconds...");
                                    Thread.sleep(5000L);
                                } else {
                                    System.out.println("Attempting to refresh UDP server in 1 minute...");
                                    Thread.sleep(60000L);
                                }
                            } else {
                                l = System.currentTimeMillis();
                                i = 0;
                                System.out.println("Attempting to refresh UDP server in 5 seconds...");
                                Thread.sleep(5000L);
                            }
                        } catch (InterruptedException interruptedexception) {
                            System.err.println((new StringBuilder()).append("WARNING: UDP server sleep thread exception: ").append(bindexception).toString());
                        }
                    } catch (Exception exception) {
                        if (!refreshRunning) {
                            System.out.println((new StringBuilder()).append("Generic UDP Socket exception: ").append(exception).toString());
                        }
                    }
                } while (isRefreshing);
                isRefreshing = true;
                running = false;
            }})).start();
    }

    private void fireUDPServerEvent(DatagramSocket datagramsocket, DatagramPacket datagrampacket, String s) {
        UDPServerListener udpserverlistener;
        for (Iterator iterator = registeredListeners.iterator(); iterator.hasNext(); udpserverlistener.handleUDPEvent(datagramsocket, datagrampacket, s)) {
            udpserverlistener = (UDPServerListener) iterator.next();
        }

    }

    public void addUDPServerListener(UDPServerListener udpserverlistener) {
        if (!registeredListeners.contains(udpserverlistener)) {
            registeredListeners.add(udpserverlistener);
        }
    }

    public void removeUDPServerListener(UDPServerListener udpserverlistener) {
        registeredListeners.remove(udpserverlistener);
    }

    public void setRefreshing(boolean flag) {
        isRefreshing = flag;
    }
    private LinkedList registeredListeners;
    private int port;
    private boolean isRefreshing;
    private boolean running;
    private boolean toStop;
    private DatagramSocket dgSocket;
    private String host;
    private boolean refreshThreadRunning;
    private boolean refreshRunning;
}
