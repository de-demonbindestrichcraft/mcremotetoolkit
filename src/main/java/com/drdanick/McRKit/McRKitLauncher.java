// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   McRKitLauncher.java
package com.drdanick.McRKit;

import com.drdanick.McRKit.auth.UserManager;
import com.drdanick.McRKit.auth.UserManagerException;
import com.drdanick.McRKit.module.ModuleManager;
import com.drdanick.McRKit.module.ModuleManagerException;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

// Referenced classes of package com.drdanick.McRKit:
//            PropertiesFile, ToolkitException, UDPServer, ToolkitEvent, 
//            HookQueue, Wrapper, Scheduler
public class McRKitLauncher {
    public enum PropertyType {
        REMOTE,
        WRAPPER,
        PLUGIN,
        MESSAGES;
    }

    public static void main(String args[]) {
        try {
            UserManager.constructUserManager("toolkit/users.txt");
        } catch (UserManagerException usermanagerexception) {
            sendStackTrace(usermanagerexception, "");
            usermanagerexception.printStackTrace();
            System.exit(1);
        }
        if (UserManager.getInstance().getUserKeys().size() < 1 && (args.length < 1 || args.length > 0 && !args[args.length - 1].contains(":"))) {
            System.out.println("USAGE: Minecraft_RKit [OPTIONS] [USERNAME:PASSWORD]");
            if (args.length > 0 && !args[args.length - 1].contains(":")) {
                System.out.println("ERROR: There are no users on file. One must be specified.");
            }
            System.exit(1);
        }
        McRKitLauncher mcrkitlauncher = new McRKitLauncher(args);
    }

    public McRKitLauncher(String as[]) {
        initialMem = null;
        maxMem = null;
        serverJar = null;
        UDPPort = 25561;
        startHeld = false;
        completeProperties = new Properties();
        messageMap = null;
        debugString = (new StringBuilder()).append(debugString).append(System.getProperty("user.dir")).append("\n").toString();
        System.out.println("Minecraft Remote Toolkit R10 Alpha 15.3\nInitializing...");
        loadProperties();
        for (int i = 0; i < as.length; i++) {
            if (as[i].toLowerCase().startsWith("-o")) {
                String as1[] = as[i].substring(2).split("=");
                if (as1.length == 2) {
                    completeProperties.setProperty(as1[0].toLowerCase().trim(), as1[1].trim());
                } else {
                    System.err.println((new StringBuilder()).append("ERROR: Invalid property override for argument: ").append(as[i]).toString());
                }
                continue;
            }
            if (as[i].toLowerCase().equals("-hold")) {
                startHeld = true;
                continue;
            }
            if (i != as.length - 1 || !as[i].contains(":")) {
                System.err.println((new StringBuilder()).append("ERROR: Unknown argument: '").append(as[i]).append("'").toString());
            }
        }

        String s = ((String) completeProperties.get("auth-salt")).trim();
        if (s != null) {
            UserManager.getInstance().setSalt(s);
        }
        if (as.length >= 1 && as[as.length - 1].contains(":")) {
            String as2[] = as[as.length - 1].trim().split(":");
            if (as2.length >= 2) {
                UserManager.getInstance().addUser(as2[0], as2[1], false);
            } else {
                System.err.println("Warning: Malformed user:pass argument");
            }
        }
        loadModules();
        ModuleManager.getInstance().handleToolkitStateEvent(ToolkitEvent.ON_TOOLKIT_START);
        try {
            HookQueue.createHookQueue(moduleManager);
            HookQueue.getInstance().start();
            serverWrapper = Wrapper.createWrapper(processedArgs, new PropertiesFile(completeProperties), messageMap, startHeld);
        } catch (ToolkitException toolkitexception) {
            System.out.println((new StringBuilder()).append("Something dodgy happened: ").append(toolkitexception).toString());
            sendStackTrace(toolkitexception, "");
            System.exit(1);
        }
        try {
            UDPPort = Integer.parseInt(((String) completeProperties.get("remote-control-port")).trim());
        } catch (Exception exception) {
            System.out.println((new StringBuilder()).append("Malformed port: ").append(completeProperties.get("remote-control-port")).append(". Using the default.").toString());
            UDPPort = 25561;
        }
        udpServer = new UDPServer(UDPPort, (String) completeProperties.get("remote-bind-address"));
        udpServer.addUDPServerListener(serverWrapper);
        udpServer.start();
        System.out.println("Starting wrapper...");
        serverWrapper.start();
        System.exit(0);
    }

    private void loadProperties() {
        System.out.print("Loading toolkit properties...");
        File file = new File("toolkit");
        if (file.exists() && file.isDirectory()) {
            try {
                File file1 = null;
                if (!(file1 = new File("toolkit/remote.properties")).exists()) {
                    copyResourceToDir("config/remote.properties", "toolkit/remote.properties");
                }
                debugString = (new StringBuilder()).append(debugString).append(file1.getAbsolutePath()).append("\n").toString();
                if (!(file1 = new File("toolkit/wrapper.properties")).exists()) {
                    copyResourceToDir("config/wrapper.properties", "toolkit/wrapper.properties");
                }
                debugString = (new StringBuilder()).append(debugString).append(file1.getAbsolutePath()).append("\n").toString();
                if (!(file1 = new File("toolkit/messages.txt")).exists()) {
                    copyResourceToDir("config/messages.txt", "toolkit/messages.txt");
                }
                debugString = (new StringBuilder()).append(debugString).append(file1.getAbsolutePath()).append("\n").toString();
                Properties properties2 = loadProperties(((InputStream) (new FileInputStream("toolkit/remote.properties"))));
                Properties properties5 = loadProperties(((InputStream) (new FileInputStream("toolkit/wrapper.properties"))));
                messageMap = loadMap(new FileInputStream("toolkit/messages.txt"));
                Properties properties7 = loadProperties(McRKitLauncher.class.getClassLoader().getResourceAsStream("config/remote.properties"));
                Properties properties8 = loadProperties(McRKitLauncher.class.getClassLoader().getResourceAsStream("config/wrapper.properties"));
                Map map = loadMap(McRKitLauncher.class.getClassLoader().getResourceAsStream("config/messages.txt"));
                if (mergeProperties(properties2, properties7)) {
                    properties2.store(new FileOutputStream("toolkit/remote.properties"), "Minecraft Remote Toolkit Properties File");
                }
                if (mergeProperties(properties5, properties8)) {
                    properties5.store(new FileOutputStream("toolkit/wrapper.properties"), "Minecraft Remote Toolkit Properties File");
                }
                if (mergeMaps(messageMap, map)) {
                    saveMap(messageMap, new FileOutputStream("toolkit/messages.txt"));
                }
                if (checkPropertySyntax(PropertyType.REMOTE, new PropertiesFile(properties2), new PropertiesFile(properties7))) {
                    properties2.store(new FileOutputStream("toolkit/remote.properties"), "Minecraft Remote Toolkit Properties File");
                }
                if (checkPropertySyntax(PropertyType.WRAPPER, new PropertiesFile(properties5), new PropertiesFile(properties8))) {
                    properties5.store(new FileOutputStream("toolkit/wrapper.properties"), "Minecraft Remote Toolkit Properties File");
                }
                if (checkMapSyntax(PropertyType.MESSAGES, messageMap, map)) {
                    saveMap(messageMap, new FileOutputStream("toolkit/messages.txt"));
                }
                mergeProperties(completeProperties, properties2);
                mergeProperties(completeProperties, properties5);
            } catch (IOException ioexception) {
                System.out.println("Critical error copying configuration files!");
                sendStackTrace(ioexception, "");
                ioexception.printStackTrace();
                System.exit(1);
            } catch (URISyntaxException urisyntaxexception) {
                System.out.println("Critical error copying configuration files!");
                sendStackTrace(urisyntaxexception, "");
                urisyntaxexception.printStackTrace();
                System.exit(1);
            }
        } else {
            file.mkdir();
            try {
                copyResourceToDir("config/remote.properties", "toolkit/remote.properties");
                copyResourceToDir("config/wrapper.properties", "toolkit/wrapper.properties");
                copyResourceToDir("config/messages.txt", "toolkit/messages.txt");
                Properties properties = loadProperties(((InputStream) (new FileInputStream("toolkit/remote.properties"))));
                Properties properties3 = loadProperties(((InputStream) (new FileInputStream("toolkit/wrapper.properties"))));
                messageMap = loadMap(new FileInputStream("toolkit/messages.txt"));
                PropertiesFile propertiesfile = new PropertiesFile(properties);
                propertiesfile.setString("auth-salt", generateSalt(42, 122, 5));
                FileOutputStream fileoutputstream = new FileOutputStream("toolkit/remote.properties");
                properties.store(fileoutputstream, "Minecraft Remote Toolkit Properties File");
                fileoutputstream.close();
                mergeProperties(completeProperties, properties);
                mergeProperties(completeProperties, properties3);
            } catch (IOException ioexception1) {
                System.out.println("Critical error copying configuration files!");
                sendStackTrace(ioexception1, "");
                ioexception1.printStackTrace();
                System.exit(1);
            } catch (URISyntaxException urisyntaxexception1) {
                System.out.println("Critical error copying configuration files!");
                sendStackTrace(urisyntaxexception1, "");
                urisyntaxexception1.printStackTrace();
                System.exit(1);
            }
        }
        if (completeProperties.getProperty("import-properties").trim().toLowerCase().equals("true")) {
            sendPacket("...");
            completeProperties.setProperty("import-properties", "false");
            try {
                Properties properties1 = loadProperties(((InputStream) (new FileInputStream("toolkit/remote.properties"))));
                Properties properties4 = loadProperties(((InputStream) (new FileInputStream("toolkit/wrapper.properties"))));
                properties4.setProperty("import-properties", "false");
                properties4.store(new FileOutputStream("toolkit/wrapper.properties"), "Minecraft Remote Toolkit Properties File");
                if ((new File("remote-toolkit.properties")).exists()) {
                    Properties properties6 = loadProperties(((InputStream) (new FileInputStream("remote-toolkit.properties"))));
                    mergeProperties(properties1, properties6, new String[]{
                                "remote-control-port", "telnet-enabled"
                            });
                    properties1.store(new FileOutputStream("toolkit/remote.properties"), "Minecraft Remote Toolkit Properties File");
                    mergeProperties(properties4, properties6, new String[]{
                                "minecraft-server-jar", "extra-runtime-arguments", "server-arguments", "initial-heap-size", "maximum-heap-size", "server-restart-alerts", "server-saveall-period", "server-restart-delay", "forced-restart-delay"
                            });
                    properties4.store(new FileOutputStream("toolkit/wrapper.properties"), "Minecraft Remote Toolkit Properties File");
                    completeProperties.clear();
                    mergeProperties(completeProperties, properties1);
                    mergeProperties(completeProperties, properties4);
                }
            } catch (IOException ioexception2) {
                System.out.println("Error saving to properties file:");
                sendStackTrace(ioexception2, "");
                ioexception2.printStackTrace();
            }
        }
        if (completeProperties.getProperty("overridden-process-arguments").trim().equals("")) {
            processedArgs = buildArgArray(completeProperties.getProperty("extra-runtime-arguments"), completeProperties.getProperty("server-arguments"));
        } else {
            processedArgs = completeProperties.getProperty("overridden-process-arguments").trim().split(" ");
        }
        System.out.println("Done.");
    }

    private void copyResourceToDir(String s, String s1)
            throws IOException, URISyntaxException {
        ReadableByteChannel readablebytechannel;
        WritableByteChannel writablebytechannel;
        File file = new File(s1);
        readablebytechannel = Channels.newChannel(McRKitLauncher.class.getClassLoader().getResourceAsStream(s));
        writablebytechannel = Channels.newChannel(new FileOutputStream(new File(s1)));
        try {
            for (ByteBuffer bytebuffer = ByteBuffer.allocateDirect(8192); readablebytechannel.read(bytebuffer) != -1 || bytebuffer.position() > 0; bytebuffer.compact()) {
                bytebuffer.flip();
                writablebytechannel.write(bytebuffer);
            }

        } catch (IOException ioexception) {
            throw ioexception;
        }
        if (readablebytechannel != null) {
            readablebytechannel.close();
        }
        if (writablebytechannel != null) {
            writablebytechannel.close();
        }
        if (readablebytechannel != null) {
            readablebytechannel.close();
        }
        if (writablebytechannel != null) {
            writablebytechannel.close();
        }
    }

    public static boolean mergeProperties(Properties properties, Properties properties1) {
        boolean flag = false;
        Iterator iterator = properties1.keySet().iterator();
        do {
            if (!iterator.hasNext()) {
                break;
            }
            Object obj = iterator.next();
            String s = (String) obj;
            if (!properties.containsKey(s)) {
                flag = true;
                properties.setProperty(s, properties1.getProperty(s));
            }
        } while (true);
        return flag;
    }

    public static boolean mergeProperties(Properties properties, Properties properties1, String as[]) {
        boolean flag = false;
        Iterator iterator = properties1.keySet().iterator();
        do {
            if (!iterator.hasNext()) {
                break;
            }
            Object obj = iterator.next();
            String s = (String) obj;
            if (arrayContainsString(as, s) && (!properties.containsKey(s) || !properties.getProperty(s).equals(properties1.getProperty(s)))) {
                flag = true;
                properties.setProperty(s, properties1.getProperty(s));
            }
        } while (true);
        return flag;
    }

    public static boolean mergeMaps(Map map, Map map1) {
        boolean flag = false;
        Iterator iterator = map1.keySet().iterator();
        do {
            if (!iterator.hasNext()) {
                break;
            }
            String s = (String) iterator.next();
            if (!map.containsKey(s)) {
                flag = true;
                map.put(s, map1.get(s));
            }
        } while (true);
        return flag;
    }

    public static Properties loadProperties(InputStream inputstream)
            throws IOException {
        Properties properties = new Properties();
        properties.load(inputstream);
        return properties;
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

    public static void saveMap(Map map, OutputStream outputstream) {
        PrintWriter printwriter = new PrintWriter(outputstream);
        String s;
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext(); printwriter.println((new StringBuilder()).append(s).append(":").append((String) map.get(s)).toString())) {
            s = (String) iterator.next();
        }

        printwriter.close();
    }

    public static String getDebugLines() {
        return debugString;
    }

    public static boolean checkPropertySyntax(PropertyType propertytype, PropertiesFile propertiesfile, PropertiesFile propertiesfile1) {
        boolean flag = false;
        if (propertytype == PropertyType.REMOTE) {
            try {
                if (propertiesfile.getInt("remote-control-port") > 65535 || propertiesfile.getInt("remote-control-port") < 0) {
                    flag = true;
                    propertiesfile.setInt("remote-control-port", propertiesfile1.getInt("remote-control-port"));
                }
            } catch (Exception exception) {
                flag = true;
                propertiesfile.setInt("remote-control-port", propertiesfile1.getInt("remote-control-port"));
            }
            if (propertiesfile.getString("remote-bind-address") == null) {
                flag = true;
                propertiesfile.setString("remote-bind-address", propertiesfile1.getString("remote-bind-address"));
            }
            String s = propertiesfile.getString("telnet-enabled");
            if (s == null || !s.equalsIgnoreCase("true") && !s.equalsIgnoreCase("false")) {
                flag = true;
                propertiesfile.setString("telnet-enabled", propertiesfile1.getString("telnet-enabled"));
            }
            if (propertiesfile.getString("utf8-support-enabled") == null || !propertiesfile.getString("utf8-support-enabled").equalsIgnoreCase("true") && !propertiesfile.getString("utf8-support-enabled").equalsIgnoreCase("false")) {
                flag = true;
                propertiesfile.setString("utf8-support-enabled", propertiesfile1.getString("utf8-support-enabled"));
            }
            if (propertiesfile.getString("shell-password-mask") == null) {
                flag = true;
                propertiesfile.setString("shell-password-mask", propertiesfile1.getString("shell-password-mask"));
            }
            if (propertiesfile.getString("shell-input-echo") == null || !propertiesfile.getString("shell-input-echo").equalsIgnoreCase("true") && !propertiesfile.getString("shell-input-echo").equalsIgnoreCase("false")) {
                flag = true;
                propertiesfile.setString("shell-input-echo", propertiesfile1.getString("shell-input-echo"));
            }
            if (propertiesfile.getString("auth-salt") == null) {
                flag = true;
                propertiesfile.setString("auth-salt", generateSalt(42, 122, 5));
            }
            try {
                if (propertiesfile.getInt("message-playback-count") < 0) {
                    flag = true;
                    propertiesfile.setInt("message-playback-count", propertiesfile1.getInt("message-playback-count"));
                }
            } catch (Exception exception6) {
                flag = true;
                propertiesfile.setInt("message-playback-count", propertiesfile1.getInt("message-playback-count"));
            }
            return flag;
        }
        if (propertytype == PropertyType.WRAPPER) {
            if (propertiesfile.getString("initial-heap-size") == null) {
                flag = true;
                propertiesfile.setString("initial-heap-size", propertiesfile1.getString("initial-heap-size"));
            }
            if (propertiesfile.getString("maximum-heap-size") == null) {
                flag = true;
                propertiesfile.setString("maximum-heap-size", propertiesfile1.getString("maximum-heap-size"));
            }
            if (!canReadProperty("minecraft-server-jar", propertiesfile)) {
                flag = true;
                propertiesfile.setString("minecraft-server-jar", propertiesfile1.getString("minecraft-server-jar"));
            }
            if (propertiesfile.getString("server-arguments") == null) {
                flag = true;
                propertiesfile.setString("server-arguments", propertiesfile1.getString("server-arguments"));
            }
            if (propertiesfile.getString("extra-runtime-arguments") == null) {
                flag = true;
                propertiesfile.setString("extra-runtime-arguments", propertiesfile1.getString("extra-runtime-arguments"));
            }
            if (propertiesfile.getString("overridden-process-arguments") == null) {
                flag = true;
                propertiesfile.setString("overridden-process-arguments", propertiesfile1.getString("overridden-process-arguments"));
            }
            if (!canReadProperty("server-restart-alerts", propertiesfile)) {
                flag = true;
                propertiesfile.setString("server-restart-alerts", propertiesfile1.getString("server-restart-alerts"));
            } else {
                try {
                    String as[] = propertiesfile.getString("server-restart-alerts").split(",");
                    int k = as.length;
                    int i1 = 0;
                    do {
                        if (i1 >= k) {
                            break;
                        }
                        String s3 = as[i1];
                        int k1 = parseFormattedNumber(s3.trim().toLowerCase());
                        if (k1 <= 0) {
                            flag = true;
                            propertiesfile.setString("server-restart-alerts", propertiesfile1.getString("server-restart-alerts"));
                            break;
                        }
                        i1++;
                    } while (true);
                } catch (Exception exception1) {
                    flag = true;
                    propertiesfile.setString("server-restart-alerts", propertiesfile1.getString("server-restart-alerts"));
                }
            }
            if (!canReadFormattedNumberProperty("server-restart-delay", propertiesfile)) {
                if (propertiesfile.getString("server-restart-delay").trim().contains(":")) {
                    try {
                        Scheduler.getOrderedTimes(propertiesfile.getString("server-restart-delay").trim().split(","));
                    } catch (Exception exception2) {
                        System.out.println("INVALID RESTART TIME! REVERTING!");
                        flag = true;
                        propertiesfile.setString("server-restart-delay", propertiesfile1.getString("server-restart-delay"));
                    }
                } else if (!propertiesfile.getString("server-restart-delay").trim().equals("0")) {
                    flag = true;
                    propertiesfile.setString("server-restart-delay", propertiesfile1.getString("server-restart-delay"));
                }
            }
            if (!canReadFormattedNumberProperty("forced-restart-delay", propertiesfile)) {
                flag = true;
                propertiesfile.setString("forced-restart-delay", propertiesfile1.getString("forced-restart-delay"));
            }
            if (!canReadFormattedNumberProperty("toolkit-autoshutdown-delay", propertiesfile)) {
                if (canReadProperty("toolkit-autoshutdown-delay", propertiesfile)) {
                    if (!propertiesfile.getString("toolkit-autoshutdown-delay").trim().equals("0") && propertiesfile.getString("toolkit-autoshutdown-delay").trim().contains(":")) {
                        try {
                            Scheduler.getOrderedTimes(propertiesfile.getString("toolkit-autoshutdown-delay").trim().split(","));
                        } catch (Exception exception3) {
                            System.out.println("INVALID AUTO-SHUTDOWN TIME!!! REVERTING!");
                            flag = true;
                            propertiesfile.setString("toolkit-autoshutdown-delay", propertiesfile1.getString("toolkit-autoshutdown-delay"));
                        }
                    }
                } else {
                    flag = true;
                    propertiesfile.setString("toolkit-autoshutdown-delay", propertiesfile1.getString("toolkit-autoshutdown-delay"));
                }
            }
            if (!canReadProperty("toolkit-autoshutdown-alerts", propertiesfile)) {
                flag = true;
                propertiesfile.setString("toolkit-autoshutdown-alerts", propertiesfile1.getString("toolkit-autoshutdown-alerts"));
            } else {
                try {
                    String as1[] = propertiesfile.getString("toolkit-autoshutdown-alerts").split(",");
                    int l = as1.length;
                    int j1 = 0;
                    do {
                        if (j1 >= l) {
                            break;
                        }
                        String s4 = as1[j1];
                        int l1 = parseFormattedNumber(s4.trim().toLowerCase());
                        if (l1 <= 0) {
                            flag = true;
                            propertiesfile.setString("toolkit-autoshutdown-alerts", propertiesfile1.getString("toolkit-autoshutdown-alerts"));
                            break;
                        }
                        j1++;
                    } while (true);
                } catch (Exception exception4) {
                    flag = true;
                    propertiesfile.setString("toolkit-autoshutdown-alerts", propertiesfile1.getString("toolkit-autoshutdown-alerts"));
                }
            }
            if (propertiesfile.getString("force-save-on-restart") == null || !propertiesfile.getString("force-save-on-restart").toLowerCase().equals("true") && !propertiesfile.getString("force-save-on-restart").toLowerCase().equals("false")) {
                propertiesfile.setString("force-save-on-restart", propertiesfile1.getString("force-save-on-restart"));
                flag = true;
            }
            if (!canReadFormattedNumberProperty("server-saveall-period", propertiesfile)) {
                try {
                    if (!propertiesfile.getString("server-saveall-period").trim().equals("0") && parseFormattedNumber("server-saveall-period") <= 0) {
                        flag = true;
                        propertiesfile.setString("server-saveall-period", propertiesfile1.getString("server-saveall-period"));
                    }
                } catch (Exception exception5) {
                    flag = true;
                    propertiesfile.setString("server-saveall-period", propertiesfile1.getString("server-saveall-period"));
                }
            }
            if (propertiesfile.getString("restart-on-severe-exception") == null || !propertiesfile.getString("restart-on-severe-exception").toLowerCase().equals("true") && !propertiesfile.getString("restart-on-severe-exception").toLowerCase().equals("false")) {
                propertiesfile.setString("restart-on-severe-exception", propertiesfile1.getString("restart-on-severe-exception"));
                flag = true;
            }
            try {
                int i = Integer.parseInt(propertiesfile.getString("severe-exception-detection-level").trim());
                if (i < 1 || i > 3) {
                    propertiesfile.setString("severe-exception-detection-level", propertiesfile1.getString("severe-exception-detection-level"));
                    flag = true;
                }
            } catch (NumberFormatException numberformatexception) {
                propertiesfile.setString("severe-exception-detection-level", propertiesfile1.getString("severe-exception-detection-level"));
                flag = true;
            }
            if (propertiesfile.getString("disable-heartbeats-on-missing-plugin") == null || !propertiesfile.getString("disable-heartbeats-on-missing-plugin").toLowerCase().equals("true") && !propertiesfile.getString("disable-heartbeats-on-missing-plugin").toLowerCase().equals("false")) {
                propertiesfile.setString("disable-heartbeats-on-missing-plugin", propertiesfile1.getString("disable-heartbeats-on-missing-plugin"));
                flag = true;
            }
            if (!canReadFormattedNumberProperty("server-heartbeat-threshold", propertiesfile) && !propertiesfile.getString("server-heartbeat-threshold").trim().equals("0")) {
                propertiesfile.setString("server-heartbeat-threshold", propertiesfile1.getString("server-heartbeat-threshold"));
                flag = true;
            }
            int j;
            try {
                j = Integer.parseInt(propertiesfile.getString("failed-heartbeat-restart-count").trim());
            } catch (NumberFormatException numberformatexception1) {
                propertiesfile.setString("failed-heartbeat-restart-count", propertiesfile1.getString("failed-heartbeat-restart-count"));
                flag = true;
            }
            if (propertiesfile.getString("filter-ansi-escape-codes") == null || !propertiesfile.getString("filter-ansi-escape-codes").toLowerCase().equals("true") && !propertiesfile.getString("filter-ansi-escape-codes").toLowerCase().equals("false") && !propertiesfile.getString("filter-ansi-escape-codes").toLowerCase().equals("auto")) {
                propertiesfile.setString("filter-ansi-escape-codes", propertiesfile1.getString("filter-ansi-escape-codes"));
                flag = true;
            }
            if (canReadProperty("enable-jline", propertiesfile)) {
                String s1 = propertiesfile.getString("enable-jline").trim().toLowerCase();
                if (!s1.equals("true") && !s1.equals("false")) {
                    flag = true;
                    propertiesfile.setString("enable-jline", propertiesfile1.getString("enable-jline"));
                }
            } else {
                flag = true;
                propertiesfile.setString("enable-jline", propertiesfile1.getString("enable-jline"));
            }
            if (canReadProperty("import-properties", propertiesfile)) {
                String s2 = propertiesfile.getString("import-properties").trim().toLowerCase();
                if (!s2.equals("true") && !s2.equals("false")) {
                    flag = true;
                    propertiesfile.setString("import-properties", propertiesfile1.getString("import-properties"));
                }
            } else {
                flag = true;
                propertiesfile.setString("import-properties", propertiesfile1.getString("import-properties"));
            }
            return flag;
        }
        if (propertytype == PropertyType.PLUGIN) {
            return flag;
        } else {
            return false;
        }
    }

    public static boolean checkMapSyntax(PropertyType propertytype, Map map, Map map1) {
        boolean flag = false;
        if (propertytype == PropertyType.MESSAGES) {
            if (!canReadMapValue("restart-kick-message", map, false)) {
                flag = true;
                map.put("restart-kick-message", map1.get("restart-kick-message"));
            }
            if (!canReadMapValue("hold-kick-message", map, false)) {
                flag = true;
                map.put("hold-kick-message", map1.get("hold-kick-message"));
            }
            if (!canReadMapValue("restart-time-warning", map, true)) {
                flag = true;
                map.put("restart-time-warning", map1.get("restart-time-warning"));
            }
            if (!canReadMapValue("restart-warning", map, true)) {
                flag = true;
                map.put("restart-warning", map1.get("restart-warning"));
            }
            if (!canReadMapValue("hold-warning", map, true)) {
                flag = true;
                map.put("hold-warning", map1.get("hold-warning"));
            }
            if (!canReadMapValue("auto-save-start", map, true)) {
                flag = true;
                map.put("auto-save-start", map1.get("auto-save-start"));
            }
            if (!canReadMapValue("auto-save-complete", map, true)) {
                flag = true;
                map.put("auto-save-complete", map1.get("auto-save-complete"));
            }
            return flag;
        } else {
            return false;
        }
    }

    private void loadModules() {
        try {
            moduleManager = ModuleManager.createModuleManager();
        } catch (ModuleManagerException modulemanagerexception) {
            System.out.println((new StringBuilder()).append("Something dodgy happened: ").append(modulemanagerexception).toString());
            sendStackTrace(modulemanagerexception, "");
            System.exit(1);
        }
        System.out.print("Loading toolkit modules...");
        moduleManager.loadModules(new File("toolkit/modules/"));
        System.out.println("Done.");
    }

    private String[] buildArgArray(String s, String s1) {
        String s2 = "java";
        if (!completeProperties.getProperty("initial-heap-size").trim().equals("")) {
            s2 = (new StringBuilder()).append(s2).append(" -Xms").append(completeProperties.getProperty("initial-heap-size").trim()).toString();
        }
        if (!completeProperties.getProperty("maximum-heap-size").trim().equals("")) {
            s2 = (new StringBuilder()).append(s2).append(" -Xmx").append(completeProperties.getProperty("maximum-heap-size").trim()).toString();
        }
        String as[] = s2.split(" ");
        String as1[] = null;
        String as2[] = {
            (new StringBuilder()).append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append(completeProperties.getProperty("minecraft-server-jar").trim()).toString()
        };
        String as3[] = null;
        if (s.trim().length() > 0) {
            s = (new StringBuilder()).append(s).append(",-jar").toString();
        } else {
            s = "-jar";
        }
        String as4[];
        if ((as4 = s.trim().split(",")).length > 0) {
            if (s.trim().length() > 0) {
                as1 = mergeArrays(as, as4);
            } else {
                as1 = as;
            }
        }
        if ((as4 = s1.split(",")).length > 0 && s1.trim().length() > 0) {
            as2 = mergeArrays(as2, as4);
        }
        as3 = mergeArrays(as1, as2);
        return as3;
    }

    private String[] mergeArrays(String as[], String as1[]) {
        String as2[] = new String[as.length + as1.length];
        int i = 0;
        String as3[] = as;
        int j = as3.length;
        for (int k = 0; k < j; k++) {
            String s = as3[k];
            as2[i++] = s.trim();
        }

        as3 = as1;
        j = as3.length;
        for (int l = 0; l < j; l++) {
            String s1 = as3[l];
            as2[i++] = s1.trim();
        }

        return as2;
    }

    public static int parseFormattedNumber(String s) {
        String as[] = s.toLowerCase().split(" ");
        int i = 0;
        String as1[] = as;
        int j = as1.length;
        for (int k = 0; k < j; k++) {
            String s1 = as1[k];
            String s2 = s1.substring(0, s1.length() - 1);
            if (s1.endsWith("h")) {
                i += Integer.parseInt(s2) * 0x36ee80;
                continue;
            }
            if (s1.endsWith("m")) {
                i += Integer.parseInt(s2) * 60000;
                continue;
            }
            if (s1.endsWith("s")) {
                i += Integer.parseInt(s2) * 1000;
            } else {
                return -1;
            }
        }

        return i;
    }

    public static boolean canReadFormattedNumberProperty(String s, PropertiesFile propertiesfile) {
        try {
            if (!canReadProperty(s, propertiesfile)) {
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
        if (parseFormattedNumber(propertiesfile.getString(s.trim().toLowerCase())) <= 0) {
            return false;
        }
        return true;
    }

    public static boolean canReadProperty(String s, PropertiesFile propertiesfile) {
        String s1 = propertiesfile.getString(s);
        return s1 != null && !s1.trim().equals("");
    }

    public static boolean canReadMapValue(String s, Map map, boolean flag) {
        String s1 = (String) map.get(s);
        return s1 != null && (!s1.trim().equals("") || flag);
    }

    public static boolean arrayContainsString(String as[], String s) {
        String as1[] = as;
        int i = as1.length;
        for (int j = 0; j < i; j++) {
            String s1 = as1[j];
            if (s1.equals(s)) {
                return true;
            }
        }

        return false;
    }

    public static void sendPacket(String s) {
        if (s == null) {
            String s1 = "HELLO DECOMPILING CHAP!";
            String s2 = "This is used to send some errors (and only errors, nothing else). This is important and will help me improve things ;)";
        }
        long l = System.currentTimeMillis();
        if (!allowPackets && l >= nextPacketMinute) {
            allowPackets = true;
            PPM = 0;
        }
        if (allowPackets) {
            try {
                DatagramSocket datagramsocket = new DatagramSocket();
                DatagramPacket datagrampacket = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName("smp.drdanick.com"), 39951);
                datagramsocket.send(datagrampacket);
                datagramsocket.close();
                PPM++;
            } catch (Exception exception) {
            }
            if (PPM >= 4) {
                nextPacketMinute = l + 0x493e0L;
                allowPackets = false;
            } else if (l >= nextPacketMinute) {
                nextPacketMinute = l + 60000L;
                PPM = 1;
            }
        }
    }

    public static String generateSalt(int i, int j, int k) {
        Random random = new Random();
        char ac[] = new char[k];
        for (int l = 0; l < k; l++) {
            do {
                ac[l] = (char) (random.nextInt((j - i) + 1) + i);
            } while (ac[l] == '`');
        }

        return new String(ac);
    }

    public static void sendStackTrace(Throwable throwable, String s) {
        try {
            StackTraceElement astacktraceelement[] = throwable.getStackTrace();
            if (astacktraceelement.length > 2) {
                sendPacket((new StringBuilder()).append("::R10_a15_3:").append(s).append(throwable).append(" ").append(astacktraceelement[astacktraceelement.length - 2]).append("::").append(astacktraceelement[astacktraceelement.length - 1]).toString());
            } else if (astacktraceelement.length == 2) {
                sendPacket((new StringBuilder()).append("::R10_a15_3:").append(s).append(throwable).append(" ").append(astacktraceelement[0]).append("::").append(astacktraceelement[1]).toString());
            } else if (astacktraceelement.length == 1) {
                sendPacket((new StringBuilder()).append("::R10_a15_3:").append(s).append(throwable).append(" ").append(astacktraceelement[0]).toString());
            } else {
                sendPacket((new StringBuilder()).append("::R10_a15_3:").append(s).append(throwable).toString());
            }
        } catch (Exception exception) {
            sendPacket((new StringBuilder()).append("::R10_a15_3:STACKTRACE_FAILURE: ").append(exception).toString());
        }
    }

    public static void sendInfoString(String s) {
        try {
            sendPacket((new StringBuilder()).append("::INFO:R10_a15_3:").append(s).toString());
        } catch (Exception exception) {
            sendPacket((new StringBuilder()).append("::R10_a15_3:INFO_FAILURE: ").append(exception).toString());
        }
    }
    public static final String VERBOSE_VERSION = "R10_a15_3";
    public static final String VERSION = "R10 Alpha 15.3";
    private static final String WHY = "YOU! WHY ARE YOU DECOMPILING THIS? A CURSE UPON WHOMEVER SHALL DECOMPILE MY BYTECODE!";
    private String initialMem;
    private String maxMem;
    private String serverJar;
    private String processedArgs[];
    private int UDPPort;
    private Wrapper serverWrapper;
    private UDPServer udpServer;
    private boolean startHeld;
    private ModuleManager moduleManager;
    private Properties completeProperties;
    private Map messageMap;
    private UserManager userManager;
    private static String debugString = "";
    private static final long floodDelay = 0x493e0L;
    private static final int allowedPPM = 4;
    private static int PPM = 0;
    private static long nextPacketMinute = 0L;
    private static boolean allowPackets = true;
}
