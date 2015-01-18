// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RTKPlugin.java
package com.drdanick.McRKit.plugin;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.TerminalSupport;
import org.bukkit.craftbukkit.libs.jline.UnsupportedTerminal;
import org.bukkit.craftbukkit.libs.jline.console.ConsoleReader;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

// Referenced classes of package com.drdanick.McRKit.plugin:
//            MetricsLite, BukkitMetricsLiteLegacy, RTKEventHandler, LegacySupport
public class RTKPlugin extends JavaPlugin {

    public RTKPlugin() {
    }

    @Override
    public void onEnable() {
        try {
            MetricsLite metricslite = new MetricsLite(this);
            metricslite.start();
        } catch (Throwable throwable) {
            try {
                (new BukkitMetricsLiteLegacy(this)).start();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        try {
            version = getDescription().getVersion();
            properties = loadProperties(new FileInputStream("toolkit/remote.properties"));
            messageMap = loadMap(new FileInputStream("toolkit/messages.txt"));
        } catch (IOException ioexception) {
            log.info((new StringBuilder()).append("Error loading Toolkit plugin properties: ").append(ioexception).toString());
            ioexception.printStackTrace();
        }
        eventHandler = new RTKEventHandler(this, properties);
        long l = Runtime.getRuntime().totalMemory();
        long l1 = Runtime.getRuntime().maxMemory();
        System.out.println((new StringBuilder()).append("Memory max: ").append(l1).append(" bytes").toString());
        System.out.println((new StringBuilder()).append("Memory total: ").append(l).append(" bytes").toString());
        try {
            org.bukkit.Server server = getServer();
            Method method = server.getClass().getMethod("getReader", new Class[0]);
            Object obj = method.invoke(server, new Object[0]);
            if (obj instanceof ConsoleReader) {
                org.bukkit.craftbukkit.libs.jline.Terminal terminal = ((ConsoleReader) obj).getTerminal();
                if (terminal instanceof UnsupportedTerminal) {
                    Method method1 = TerminalSupport.class.getDeclaredMethod("setAnsiSupported", new Class[]{
                                Boolean.TYPE
                            });
                    method1.setAccessible(true);
                    method1.invoke(terminal, new Object[]{
                                Boolean.valueOf(true)
                            });
                }
            }
        } catch (NoClassDefFoundError noclassdeffounderror) {
            try {
                LegacySupport.initMetrics(this);
            } catch (Throwable throwable1) {
                System.err.println((new StringBuilder()).append("ERROR LOADING TOOLKIT PLUGIN: ").append(throwable1).toString());
                throwable1.printStackTrace();
            }
        } catch (Exception exception1) {
            try {
                LegacySupport.initMetrics(this);
            } catch (Throwable throwable2) {
                System.err.println((new StringBuilder()).append("ERROR LOADING TOOLKIT PLUGIN: ").append(throwable2).toString());
                throwable2.printStackTrace();
            }
        }
        this.registerCommands();
        log.info((new StringBuilder()).append("Remote Toolkit Plugin ").append(version).append(" enabled!").toString());
    }

    public void registerCommands() {
        Stack<String> commands = new Stack<String>();
        commands.push("kickall");
        commands.push("rtkunicodetest");
        commands.push("kickallhold");
        commands.push("kickallstop");
        commands.push("holdsrv");
        commands.push("restartsrv");
        commands.push("RTPING");
        commands.push("stopwrapper");
        int length = commands.size();
        for(int i = 0; i < length; i++)
        {
            this.getCommand(commands.pop()).setExecutor(this);
        }
    }

    public void onDisable() {
        log.info((new StringBuilder()).append("Remote Toolkit Plugin ").append(version).append(" disabled!").toString());
    }

    public Properties getProperties() {
        return properties;
    }

    public Map getMessageMap() {
        return messageMap;
    }

    @Override
    public boolean onCommand(CommandSender commandsender, Command command, String s, String as[]) {
        return eventHandler.onCommand(commandsender, command, s, as, this);
    }

    private Properties loadProperties(InputStream inputstream)
            throws IOException {
        Properties properties1 = new Properties();
        properties1.load(inputstream);
        return properties1;
    }

    public static Map loadMap(InputStream inputstream) {
        HashMap hashmap;
        Scanner scanner;
        hashmap = new HashMap();
        scanner = null;
        for (scanner = new Scanner(inputstream); scanner.hasNextLine();) {
            String as[] = scanner.nextLine().trim().split(":");
            String s = as[0];
            String s1 = "";
            for (int i = 1; i < as.length; i++) {
                s1 = (new StringBuilder()).append(s1).append(":").append(as[i]).toString();
            }

            if (s1.length() > 0) {
                hashmap.put(s, s1.substring(1));
            } else {
                hashmap.put(s, "");
            }
        }

        scanner.close();
        return hashmap;
    }
    public String version;
    public static final Logger log = Logger.getLogger("Minecraft");
    private Properties properties;
    private Map messageMap;
    private RTKEventHandler eventHandler;
}
