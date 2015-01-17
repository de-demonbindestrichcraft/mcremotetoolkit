// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RTKInterface.java
package com.drdanick.McRKit.api;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Referenced classes of package com.drdanick.McRKit.api:
//            RTKInterfaceException
public class RTKInterface {

    public enum CommandType {
        RESTART,
        FORCE_RESTART,
        FORCE_STOP,
        VERSION,
        DISABLE_RESTARTS,
        ENABLE_RESTARTS,
        HOLD_SERVER,
        UNHOLD_SERVER,
        RESCHEDULE_RESTART;
    }

    public RTKInterface(int i, String s, String s1, String s2, String s3) {
        port = i;
        host = s;
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("SHA1");
            user = convertToHexString(messagedigest.digest((new StringBuilder()).append(s1).append(s3).toString().getBytes()), "UTF-8");
            messagedigest.reset();
            password = convertToHexString(messagedigest.digest((new StringBuilder()).append(s2).append(s3).toString().getBytes()), "UTF-8");
        } catch (UnsupportedEncodingException unsupportedencodingexception) {
            unsupportedencodingexception.printStackTrace();
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
        }
    }

    public synchronized String getLastResponse()
            throws InterruptedException {
        if (lastResponse == null) {
            wait();
        }
        return lastResponse;
    }

    private void setResponse(String s) {
        lastResponse = s;
    }

    private static String convertToHexString(byte abyte0[], String s)
            throws UnsupportedEncodingException {
        byte abyte1[] = new byte[abyte0.length * 2];
        int i = 0;
        byte abyte2[] = abyte0;
        int j = abyte2.length;
        for (int k = 0; k < j; k++) {
            byte byte0 = abyte2[k];
            abyte1[i++] = HEX[(byte0 & 0xf0) >>> 4];
            abyte1[i++] = HEX[byte0 & 0xf];
        }

        return new String(abyte1, s);
    }

    public void executeCommand(CommandType commandtype, String s)
            throws IOException, RTKInterfaceException {
        String s1 = null;
        String s2 = (new StringBuilder()).append(":2:").append(user).append(":").append(password).toString();
        if (commandtype == CommandType.RESTART) {
            s1 = (new StringBuilder()).append("restart").append(s2).toString();
        } else if (commandtype == CommandType.FORCE_RESTART) {
            s1 = (new StringBuilder()).append("forcerestart").append(s2).toString();
        } else if (commandtype == CommandType.FORCE_STOP) {
            s1 = (new StringBuilder()).append("forcestop").append(s2).toString();
        } else if (commandtype == CommandType.VERSION) {
            s1 = (new StringBuilder()).append("version").append(s2).toString();
        } else if (commandtype == CommandType.DISABLE_RESTARTS) {
            s1 = (new StringBuilder()).append("disable").append(s2).toString();
        } else if (commandtype == CommandType.ENABLE_RESTARTS) {
            s1 = (new StringBuilder()).append("enable").append(s2).toString();
        } else if (commandtype == CommandType.HOLD_SERVER) {
            s1 = (new StringBuilder()).append("hold").append(s2).toString();
        } else if (commandtype == CommandType.UNHOLD_SERVER) {
            s1 = (new StringBuilder()).append("unhold").append(s2).toString();
        } else if (commandtype == CommandType.RESCHEDULE_RESTART) {
            if (s == null || s.trim().equals("")) {
                throw new RTKInterfaceException("Illegal command parameter specified");
            }
            s1 = (new StringBuilder()).append("reschedule:").append(s).append(s2).toString();
        } else {
            throw new RTKInterfaceException("Illegal command type specified");
        }
        dispatchUDPPacket(s1, host, port);
    }

    private void dispatchUDPPacket(String s, String s1, int i)
            throws IOException {
        final RTKInterface lock = this;
        lastResponse = null;
        final DatagramSocket ds = new DatagramSocket();
        DatagramPacket datagrampacket;
        try {
            datagrampacket = new DatagramPacket(s.getBytes(), s.getBytes("UTF-8").length, InetAddress.getByName(s1), i);
        } catch (UnsupportedEncodingException unsupportedencodingexception) {
            unsupportedencodingexception.printStackTrace();
            return;
        }
        ds.send(datagrampacket);
        new Thread() {

            public void run() {
                byte abyte0[] = new byte[0x10000];
                DatagramPacket datagrampacket1 = new DatagramPacket(abyte0, abyte0.length);
                try {
                    ds.setSoTimeout(5000);
                    ds.receive(datagrampacket1);
                    String s2 = new String(datagrampacket1.getData());
                    setResponse(s2.trim());
                } catch (SocketTimeoutException sockettimeoutexception) {
                    setResponse("timeout");
                } catch (Exception exception) {
                    System.err.println((new StringBuilder()).append("Unexpected Socket error: ").append(exception).toString());
                    exception.printStackTrace();
                }
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        }.start();
    }
    private String host;
    private int port;
    private String user;
    private String password;
    private String lastResponse;
    private static final byte HEX[] = {
        48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
        97, 98, 99, 100, 101, 102
    };
}
