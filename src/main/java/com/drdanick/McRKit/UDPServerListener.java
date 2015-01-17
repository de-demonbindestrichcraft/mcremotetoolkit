// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UDPServerListener.java

package com.drdanick.McRKit;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public interface UDPServerListener
{

    public abstract void handleUDPEvent(DatagramSocket datagramsocket, DatagramPacket datagrampacket, String s);
}
