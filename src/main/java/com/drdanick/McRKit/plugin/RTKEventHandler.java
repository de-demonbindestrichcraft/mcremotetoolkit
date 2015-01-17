// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RTKEventHandler.java

package com.drdanick.McRKit.plugin;

import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.player.WPlayerInterface;
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

// Referenced classes of package com.drdanick.McRKit.plugin:
//            RTKPlugin

public class RTKEventHandler
{

    public RTKEventHandler(RTKPlugin rtkplugin, Properties properties)
    {
        port = 25561;
        par = rtkplugin;
        out = new PrintStream(new FileOutputStream(FileDescriptor.out));
        err = new PrintStream(new FileOutputStream(FileDescriptor.err));
        try
        {
            port = Integer.parseInt(((String)properties.get("remote-control-port")).trim());
        }
        catch(Exception exception)
        {
            RTKPlugin _tmp = rtkplugin;
            RTKPlugin.log.log(Level.INFO, "Malformed port: {0}. Using the default.", properties.get("remote-control-port"));
            port = 25561;
        }
    }

    public boolean onCommand(CommandSender commandsender, Command command, String s, String as[], RTKPlugin rtkplugin)
    {
        if(command.getName().equals("rtkunicodetest"))
        {
            err.println(new String("A\352\361\374C"));
            commandsender.sendMessage(new String((new StringBuilder()).append(ChatColor.YELLOW).append("A").append("\352").append("\361").append("\374").append("C").toString()));
        } else
        if(commandsender instanceof ConsoleCommandSender)
        {
            if(command.getName().equals("kickall"))
            {
                String s1 = ((String)rtkplugin.getMessageMap().get("restart-kick-message")).replaceAll(new String(new byte[] {
                    92, 92, 110
                }), "\n");
                if(s1 == null)
                    s1 = "Everyone is being kicked ;)";
                Player aplayer[] = WPlayerInterface.getOnlinePlayersOld();
                int i = aplayer.length;
                for(int i1 = 0; i1 < i; i1++)
                {
                    Player player4 = aplayer[i1];
                    player4.kickPlayer(s1);
                }

                return true;
            }
            if(command.getName().equals("kickallhold"))
            {
                String s2 = ((String)rtkplugin.getMessageMap().get("hold-kick-message")).replaceAll(new String(new byte[] {
                    92, 92, 110
                }), "\n");
                if(s2 == null)
                    s2 = "Server is shutting down";
                Player aplayer1[] = WPlayerInterface.getOnlinePlayersOld();
                int j = aplayer1.length;
                for(int j1 = 0; j1 < j; j1++)
                {
                    Player player5 = aplayer1[j1];
                    player5.kickPlayer(s2);
                }

                return true;
            }
            if(command.getName().equals("kickallstop"))
            {
                String s3 = ((String)rtkplugin.getMessageMap().get("toolkit-shutdown-kick-message")).replaceAll(new String(new byte[] {
                    92, 92, 110
                }), "\n");
                if(s3 == null)
                    s3 = "Server is shutting down";
                Player aplayer2[] = WPlayerInterface.getOnlinePlayersOld();
                int k = aplayer2.length;
                for(int k1 = 0; k1 < k; k1++)
                {
                    Player player6 = aplayer2[k1];
                    player6.kickPlayer(s3);
                }

                return true;
            }
            if(command.getName().equals("RTPING"))
            {
                out.println("RTPONG++");
                return true;
            }
        } else
        if(commandsender instanceof Player)
        {
            if(s.equalsIgnoreCase("holdsrv"))
            {
                Player player = (Player)commandsender;
                if(as.length != 1)
                {
                    player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Usage: /holdsrv <[username:]password>").toString());
                    return true;
                }
                if(as[0].contains(":"))
                    dispatchLocalUDPPacket((new StringBuilder()).append("hold:").append(as[0].trim()).toString(), player);
                else
                    dispatchLocalUDPPacket((new StringBuilder()).append("hold:").append(player.getName()).append(":").append(as[0].trim()).toString(), player);
                return true;
            }
            if(s.equalsIgnoreCase("restartsrv"))
            {
                Player player1 = (Player)commandsender;
                if(as.length != 1)
                {
                    player1.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Usage: /restartsrv <[username:]password>").toString());
                    return true;
                }
                if(as[0].contains(":"))
                    dispatchLocalUDPPacket((new StringBuilder()).append("restart:").append(as[0].trim()).toString(), player1);
                else
                    dispatchLocalUDPPacket((new StringBuilder()).append("restart:").append(player1.getName()).append(":").append(as[0].trim()).toString(), player1);
                return true;
            }
            if(s.equalsIgnoreCase("reschedulerestart"))
            {
                Player player2 = (Player)commandsender;
                if(as.length < 2)
                {
                    player2.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Usage: /reschedulerestart <[username:]password> <time>").toString());
                    return true;
                }
                String s4 = "";
                for(int l = 1; l < as.length; l++)
                    s4 = (new StringBuilder()).append(s4).append(as[l].trim()).append(" ").toString();

                s4 = s4.replaceAll(":", "-");
                if(as[0].contains(":"))
                    dispatchLocalUDPPacket((new StringBuilder()).append("reschedule:").append(s4.trim()).append(":").append(as[0].trim()).toString(), player2);
                else
                    dispatchLocalUDPPacket((new StringBuilder()).append("reschedule:").append(s4.trim()).append(":").append(player2.getName()).append(":").append(as[0].trim()).toString(), player2);
                return true;
            }
            if(s.equalsIgnoreCase("stopwrapper"))
            {
                Player player3 = (Player)commandsender;
                if(as.length != 1)
                {
                    player3.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Usage: /stopwrapper <[username:]password>").toString());
                    return true;
                }
                if(as[0].contains(":"))
                    dispatchLocalUDPPacket((new StringBuilder()).append("stopwrapper:").append(as[0].trim()).toString(), player3);
                else
                    dispatchLocalUDPPacket((new StringBuilder()).append("stopwrapper:").append(player3.getName()).append(":").append(as[0].trim()).toString(), player3);
                return true;
            }
        }
        return false;
    }

    private void dispatchLocalUDPPacket(String s, final Player player)
    {
        try
        {
            final DatagramSocket ds = new DatagramSocket();
            DatagramPacket datagrampacket = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName("localhost"), port);
            ds.send(datagrampacket);
            new Thread() {

                public void run()
                {
                    byte abyte0[] = new byte[0x10000];
                    DatagramPacket datagrampacket1 = new DatagramPacket(abyte0, abyte0.length);
                    try
                    {
                        ds.setSoTimeout(700);
                        ds.receive(datagrampacket1);
                        String s1 = new String(datagrampacket1.getData());
                        if(s1.trim().equalsIgnoreCase("response:success"))
                            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Done!").toString());
                        else
                        if(s1.trim().equalsIgnoreCase("response:command_error"))
                            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Invalid restart time!").toString());
                        else
                            player.sendMessage((new StringBuilder()).append(ChatColor.RED).append(denyStrings[(int)(Math.random() * (double)denyStrings.length)]).toString());
                    }
                    catch(SocketTimeoutException sockettimeoutexception)
                    {
                        player.sendMessage((new StringBuilder()).append(ChatColor.RED).append(denyStrings[(int)(Math.random() * (double)denyStrings.length)]).toString());
                    }
                    catch(Exception exception1)
                    {
                        RTKPlugin _tmp = par;
                        RTKPlugin.log.log(Level.INFO, "Unexpected Socket error: {0}", exception1);
                        exception1.printStackTrace();
                    }
                }}.start();
        }
        catch(Exception exception)
        {
            RTKPlugin _tmp = par;
            RTKPlugin.log.log(Level.INFO, "Error in Remote Toolkit plugin: {0}", exception);
            exception.printStackTrace();
        }
    }

    private RTKPlugin par;
    private int port;
    private final String denyStrings[] = {
        "No.", "Make me.", "Nice try!", "It works if you know the password.", "You're not being very persuasive.", "You didn't say the magic word!", "No! You're not my mother.", "Once again, with feeling!", "The password gods have frowned upon you.", "My mother told me not to talk to strangers."
    };
    private PrintStream out;
    private PrintStream err;


}
