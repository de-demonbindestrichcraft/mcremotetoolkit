// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Wrapper.java
package com.drdanick.McRKit;

import com.drdanick.McRKit.ToolkitAction;
import com.drdanick.McRKit.auth.UserManager;
import com.drdanick.McRKit.module.Module;
import com.drdanick.McRKit.module.ModuleManager;
import com.drdanick.McRKit.module.ModuleMetadata;
import java.io.*;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jline.Terminal;
import jline.console.ConsoleReader;
import jline.internal.NonBlockingInputStream;
import net.wimpi.telnetd.BootException;
import net.wimpi.telnetd.TelnetD;
import net.wimpi.telnetd.net.PortListener;

// Referenced classes of package com.drdanick.McRKit:
//            Scheduler, WrapperException, ScheduledTask, LoggerOutputStream, 
//            ScheduledEvent, ConsoleListener, HookEvent, UDPServerListener, 
//            ScheduledEventListener, ToolkitInterface, PropertiesFile, McRKitLauncher, 
//            ToolkitEvent, ToolkitAction, HookQueue
public class Wrapper
        implements UDPServerListener, ScheduledEventListener, ToolkitInterface {

    private String version;
    private String[] processArgs;
    private boolean restarting = true;
    private Thread shutdownHook = null;
    private Process mcProcess = null;
    private Thread stdInThread = null;
    private OutputStream console = null;
    private boolean serverRunning = false;
    private ProcessBuilder builder = null;
    private Scheduler scheduler;
    private PropertiesFile toolkitProperties;
    private final int BUFFER_SIZE = 1024;
    private final int STRING_BUFFER_SIZE = 100;
    private static Wrapper wrapperInstance = null;
    private LinkedList<ConsoleListener> registeredShells = null;
    private boolean useTelnet;
    private int telnetPort;
    private Map<String, String> messageMap;
    private boolean severeRestart;
    private int severeLevel;
    private boolean severeRestartInProgress = false;
    private boolean xpFix = false;
    private boolean pauseMode = false;
    private long heartbeatDelay = 60000L;
    private long heartbeatRestartDelay = 305000L;
    private boolean heartbeatEnabled = true;
    private ScheduledTask heartbeatRestartTask = null;
    private UserManager users = UserManager.getInstance();
    private LinkedList<ScheduledTask> scheduledTasks = new LinkedList();
    private boolean debugMode = false;
    private long restartTime;
    private long shutdownTime = -1L;
    private boolean saveOnServerStop;
    private boolean saveInProgress = false;
    private boolean shutdownSave = false;
    private boolean willFilterAnsi = false;
    private boolean ansiMode = true;
    private int serverID = 0;
    private int heartbeatCount;
    private boolean gotHeartbeatResponse;
    private Pattern ansiPattern = Pattern.compile("\\x1B\\[([0-9]{1,2}(;[0-9]{1,2})*)?[m|K]");
    private String[] numberWords = {"First", "Second", "Third"};
    private ConsoleReader reader = null;
    private ConsoleReader myReader = null;
    private int playbackCount = 0;
    private List<String> playbackBuffer = new LinkedList();
    private Logger logger = Logger.getLogger("com.drdanick.McRkit");
    private boolean startHeld = false;
    private String stopCommand = "stop";
    private boolean vanillaServer = false;
    private boolean serverTypeKnown = false;

    private Wrapper() {
        restarting = true;
        shutdownHook = null;
        mcProcess = null;
        stdInThread = null;
        console = null;
        serverRunning = false;
        builder = null;
        registeredShells = null;
        severeRestartInProgress = false;
        xpFix = false;
        pauseMode = false;
        heartbeatDelay = 60000L;
        heartbeatRestartDelay = 0x4a768L;
        heartbeatEnabled = true;
        heartbeatRestartTask = null;
        users = UserManager.getInstance();
        scheduledTasks = new LinkedList();
        debugMode = false;
        shutdownTime = -1L;
        saveInProgress = false;
        shutdownSave = false;
        willFilterAnsi = false;
        ansiMode = true;
        serverID = 0;
        ansiPattern = Pattern.compile("\\x1B\\[([0-9]{1,2}(;[0-9]{1,2})*)?[m|K]");
        reader = null;
        playbackCount = 0;
        playbackBuffer = new LinkedList();
        logger = Logger.getLogger("com.drdanick.McRkit");
        startHeld = false;
        stopCommand = "stop";
        vanillaServer = false;
        serverTypeKnown = false;
        myReader = null;
    }

    private Wrapper(String as[], PropertiesFile propertiesfile, Map map, boolean flag) {
        restarting = true;
        shutdownHook = null;
        mcProcess = null;
        stdInThread = null;
        console = null;
        serverRunning = false;
        builder = null;
        registeredShells = null;
        severeRestartInProgress = false;
        xpFix = false;
        pauseMode = false;
        heartbeatDelay = 60000L;
        heartbeatRestartDelay = 0x4a768L;
        heartbeatEnabled = true;
        heartbeatRestartTask = null;
        users = UserManager.getInstance();
        scheduledTasks = new LinkedList();
        debugMode = false;
        shutdownTime = -1L;
        saveInProgress = false;
        shutdownSave = false;
        willFilterAnsi = false;
        ansiMode = true;
        serverID = 0;
        ansiPattern = Pattern.compile("\\x1B\\[([0-9]{1,2}(;[0-9]{1,2})*)?[m|K]");
        reader = null;
        myReader = null;
        playbackCount = 0;
        playbackBuffer = new LinkedList();
        logger = Logger.getLogger("com.drdanick.McRkit");
        startHeld = false;
        stopCommand = "stop";
        vanillaServer = false;
        serverTypeKnown = false;
        startHeld = flag;
        System.out.println((new StringBuilder()).append("Wrapper is running on: ").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version")).append(" ").append(System.getProperty("os.arch")).toString());
        if (System.getProperty("os.name").trim().equals("Windows XP")) {
            System.out.println("NOTE: Windows XP is not fully supported.\nTry pressing enter a few times...");
        }
        processArgs = as;
        toolkitProperties = propertiesfile;
        messageMap = map;
        version = "R10_a15_3";
        scheduler = new Scheduler(this, map);
        severeRestart = propertiesfile.getString("restart-on-severe-exception").toLowerCase().equals("true");
        severeLevel = Integer.parseInt(propertiesfile.getString("severe-exception-detection-level").trim());
        if (!propertiesfile.getString("server-heartbeat-threshold").trim().equals("0")) {
            heartbeatDelay = Scheduler.convertFormattedTime(propertiesfile.getString("server-heartbeat-threshold"));
            heartbeatRestartDelay = (long) propertiesfile.getInt("failed-heartbeat-restart-count") * heartbeatDelay + 5000L;
        } else {
            heartbeatEnabled = false;
        }
        try {
            String s = propertiesfile.getString("stop-command");
            if (s != null && !s.trim().isEmpty()) {
                stopCommand = s.trim();
            }
        } catch (Exception exception) {
            stopCommand = "stop";
        }
        saveOnServerStop = propertiesfile.getString("force-save-on-restart").toLowerCase().trim().equals("true");
        useTelnet = propertiesfile.getString("telnet-enabled").toLowerCase().trim().equals("true");
        ansiMode = !System.getProperty("os.name").trim().startsWith("Windows");
        if (propertiesfile.getString("filter-ansi-escape-codes").toLowerCase().trim().equals("auto")) {
            willFilterAnsi = System.getProperty("os.name").trim().startsWith("Windows");
        } else {
            willFilterAnsi = propertiesfile.getString("filter-ansi-escape-codes").toLowerCase().trim().equals("true");
        }
        scheduleShutdown(propertiesfile.getString("toolkit-autoshutdown-delay"));
        init();
        if (useTelnet) {
            registeredShells = new LinkedList();
            try {
                String s1 = propertiesfile.getString("remote-bind-address").trim();
                telnetPort = propertiesfile.getInt("remote-control-port");
                playbackCount = propertiesfile.getInt("message-playback-count");
                InputStream inputstream = Wrapper.class.getClassLoader().getResourceAsStream("config/telnet.properties");
                Properties properties = new Properties();
                properties.load(inputstream);
                inputstream.close();
                properties.setProperty("std.port", (new StringBuilder()).append("").append(telnetPort).toString());
                TelnetD telnetd = null;
                if ((telnetd = TelnetD.getReference()) == null) {
                    telnetd = TelnetD.createTelnetD(properties);
                }
                if (s1.length() > 0) {
                    telnetd.getPortListener("std").setBindAddress(s1);
                }
                telnetd.start();
            } catch (IOException ioexception) {
                System.err.println("TELNET INIT EXCEPTION:");
                ioexception.printStackTrace();
                McRKitLauncher.sendStackTrace(ioexception, "WR:");
            } catch (BootException bootexception) {
                McRKitLauncher.sendStackTrace(bootexception, "WR:");
                System.err.println((new StringBuilder()).append("BOOT EXCEPTION: ").append(bootexception).toString());
                bootexception.printStackTrace();
            }
        }
    }

    public static Wrapper createWrapper(String as[], PropertiesFile propertiesfile, Map map, boolean flag)
            throws WrapperException {
        if (wrapperInstance == null) {
            return wrapperInstance = new Wrapper(as, propertiesfile, map, flag);
        } else {
            throw new WrapperException("Wrapper already instantiated.");
        }
    }

    public static Wrapper getInstance() {
        return wrapperInstance;
    }

    public static ToolkitInterface getInterface() {
        return wrapperInstance;
    }

    private boolean scheduleShutdown(String s) {
        s = s.trim().toLowerCase();
        if (s.equals("0")) {
            return false;
        }
        if (s.contains(":")) {
            String as[] = s.split(",");
            try {
                Scheduler.getOrderedTimes(as).first();
            } catch (ParseException parseexception) {
                System.err.println("Error parsing a shutdown time!\nDisabling shutdown!");
                return false;
            }
        } else if (Scheduler.convertFormattedTime(s) == -1L) {
            System.err.println("ERROR: Invalid shutdown time!");
            return false;
        }
        Iterator iterator = scheduledTasks.iterator();
        do {
            if (!iterator.hasNext()) {
                break;
            }
            ScheduledTask scheduledtask = (ScheduledTask) iterator.next();
            if (scheduledtask != null && (scheduledtask.getEvent().getType() == Scheduler.Type.SHUTDOWN || scheduledtask.getEvent().getType() == Scheduler.Type.SHUTDOWNALERT)) {
                scheduledtask.cancel();
            }
        } while (true);
        scheduler.purge();
        if (!s.equals("0")) {
            String s1 = toolkitProperties.getString("toolkit-autoshutdown-alerts").toLowerCase().trim();
            ScheduledTask scheduledtask1 = scheduler.scheduleShutdownEvent(s, s.contains(":"));
            if (scheduledtask1 == null) {
                return false;
            }
            scheduledTasks.add(scheduledtask1);
            shutdownTime = scheduledtask1.scheduledExecutionTime();
            String as1[] = s1.split(",");
            int i = as1.length;
            for (int j = 0; j < i; j++) {
                String s2 = as1[j];
                scheduledTasks.add(scheduler.scheduleAlertEvent(s2.trim().toLowerCase(), Long.valueOf(shutdownTime), (String) messageMap.get("toolkit-shutdown-time-warning"), Scheduler.Type.SHUTDOWNALERT));
            }

            return true;
        } else {
            return false;
        }
    }

    private void init() {
        if (severeRestartInProgress) {
            severeRestartInProgress = false;
        }
        if (toolkitProperties.getString("enable-jline").toLowerCase().trim().equals("true") && reader == null) {
            try {
                //reader = new ConsoleReader(System.in, new FileOutputStream(FileDescriptor.out));
                reader = new ConsoleReader(System.in, System.out);
                reader.getTerminal().setEchoEnabled(true);
                reader.setEchoCharacter(null);
                myReader = new ConsoleReader(System.in, System.out);
                myReader.getTerminal().setEchoEnabled(true);
                myReader.setEchoCharacter(null);
                if (toolkitProperties.getString("filter-ansi-escape-codes").toLowerCase().trim().equals("auto")) {
                    ansiMode = reader.getTerminal().isAnsiSupported();
                } else if (toolkitProperties.getString("filter-ansi-escape-codes").toLowerCase().trim().equals("true")) {
                    ansiMode = false;
                } else {
                    ansiMode = true;
                }
                System.setOut(new PrintStream(new LoggerOutputStream(logger, Level.INFO, System.out, reader), true));
                System.setErr(new PrintStream(new LoggerOutputStream(logger, Level.SEVERE, System.err, reader), true));
            } catch (IOException ioexception) {
                System.err.println((new StringBuilder()).append("Error: Could not open console input stream: ").append(ioexception.getMessage()).toString());
            }
        }
        if (startHeld) {
            return;
        }
        int i = scheduledTasks.size();
        int j = 0;
        for (int k = 0; k < i; k++) {
            ScheduledTask scheduledtask = (ScheduledTask) scheduledTasks.get(j);
            if (scheduledtask != null && scheduledtask.getEvent().getType() != Scheduler.Type.SHUTDOWN && scheduledtask.getEvent().getType() != Scheduler.Type.SHUTDOWNALERT) {
                scheduledtask.cancel();
                scheduledTasks.remove(j);
            } else {
                j++;
            }
        }

        scheduler.purge();
        String s = toolkitProperties.getString("server-restart-delay").toLowerCase().trim();
        String s1 = toolkitProperties.getString("server-saveall-period").toLowerCase().trim();
        String s2 = toolkitProperties.getString("server-restart-alerts").toLowerCase().trim();
        if (!s.equals("0")) {
            ScheduledTask scheduledtask1 = scheduler.scheduleRestartEvent(s, s.contains(":"));
            scheduledTasks.add(scheduledtask1);
            restartTime = scheduledtask1.scheduledExecutionTime();
            scheduledTasks.add(scheduler.scheduleKillEvent(restartTime + Scheduler.convertFormattedTime(toolkitProperties.getString("forced-restart-delay").toLowerCase().trim())));
            String as[] = s2.split(",");
            int l = as.length;
            for (int i1 = 0; i1 < l; i1++) {
                String s3 = as[i1];
                scheduledTasks.add(scheduler.scheduleAlertEvent(s3.trim().toLowerCase(), Long.valueOf(restartTime), (String) messageMap.get("restart-time-warning"), Scheduler.Type.RESTARTALERT));
            }

        }
        scheduledTasks.add(scheduler.scheduleSaveEvent(s1));
        if (heartbeatEnabled) {
            gotHeartbeatResponse = toolkitProperties.getString("disable-heartbeats-on-missing-plugin").trim().toLowerCase().equals("false");
            if (gotHeartbeatResponse) {
                if (heartbeatRestartTask != null) {
                    heartbeatRestartTask.cancel();
                    scheduler.purge();
                }
                heartbeatRestartTask = scheduler.scheduleEvent(new ScheduledEvent(Scheduler.Type.HEARTBEATRESTART, ""), new Date(System.currentTimeMillis() + heartbeatRestartDelay));
            }
            scheduledTasks.add(scheduler.scheduleHeartbeat(heartbeatDelay));
            heartbeatCount = 0;
        }
    }

    public void start() {
        serverRunning = true;
        do {
            serverRunning = true;
            init();
            try {
                if (shutdownHook != null) {
                    Runtime.getRuntime().removeShutdownHook(shutdownHook);
                }
                builder = new ProcessBuilder(processArgs);
                ModuleManager.getInstance().handleToolkitStateEvent(ToolkitEvent.ON_SERVER_START);
                if (startHeld) {
                    serverRunning = false;
                }
                Process process = null;
                if (!startHeld) {
                    process = builder.start();
                    mcProcess = process;
                    console = process.getOutputStream();
                    shutdownHook = new Thread(new Runnable() {

                        public void run() {
                            mcProcess.destroy();
                            if (reader != null) {
                                reader.getTerminal().setEchoEnabled(true);
                            }
                        }
                    });
                    Runtime.getRuntime().addShutdownHook(shutdownHook);
                    Thread thread = new Thread("stdout") {

                        public void run() {
                            BufferedReader bufferedreader = null;
                            try {
                                PrintStream printstream = System.out;
                                bufferedreader = new BufferedReader(new InputStreamReader(mcProcess.getInputStream()), 1024);
                                int i = 0;
                                StringBuilder stringbuilder = new StringBuilder(100);
                                int j = 0;
                                boolean flag = true;
                                String s1 = null;
                                while ((i = bufferedreader.read()) >= 0) {
                                    if (i != System.getProperty("line.separator").charAt(System.getProperty("line.separator").length() - 1)) {
                                        stringbuilder.appendCodePoint(i);
                                        j++;
                                    } else {
                                        String s = stringbuilder.toString();
                                        if (ansiPattern.matcher(s).replaceAll("").trim().endsWith("RTPONG++")) {
                                            if (debugMode) {
                                                System.err.println("PING_ACK");
                                                enqueueStringToShells("PING_ACK");
                                            }
                                            flag = false;
                                            if (heartbeatEnabled) {
                                                if (heartbeatRestartTask != null) {
                                                    heartbeatRestartTask.cancel();
                                                    scheduler.purge();
                                                }
                                                if (!gotHeartbeatResponse) {
                                                    gotHeartbeatResponse = true;
                                                    System.out.println("RemoteToolkit plugin for Bukkit found! Enabling heartbeats...");
                                                }
                                                heartbeatRestartTask = scheduler.scheduleEvent(new ScheduledEvent(Scheduler.Type.HEARTBEATRESTART, ""), new Date(System.currentTimeMillis() + heartbeatRestartDelay));
                                            }
                                        }
                                        if (flag) {
                                            printstream.println(s);
                                            enqueueStringToShells(s);
                                            if (debugMode) {
                                                String s2 = "";
                                                for (int k = 0; k < s.length(); k++) {
                                                    s2 = (new StringBuilder()).append(s2).append(s.charAt(k)).append(",").toString();
                                                }

                                                printLnToAll((new StringBuilder()).append(s2).append("=out=").toString());
                                            }
                                            try {
                                                HookQueue.getInstance().put(new HookEvent(HookEvent.Type.STDOUT, s));
                                            } catch (InterruptedException interruptedexception1) {
                                            }
                                        }
                                        if (saveInProgress || shutdownSave) {
                                            s1 = ansiPattern.matcher(s).replaceAll("").trim();
                                        }
                                        if ((saveInProgress || shutdownSave) && ((!serverTypeKnown || !vanillaServer) && s1.endsWith("Save complete.") || (!serverTypeKnown || vanillaServer) && s1.endsWith("Saved the world"))) {
                                            if (!serverTypeKnown) {
                                                serverTypeKnown = true;
                                                if (s1.endsWith("Save complete.")) {
                                                    vanillaServer = false;
                                                } else {
                                                    vanillaServer = true;
                                                }
                                            }
                                            if (!shutdownSave && ((String) messageMap.get("auto-save-complete")).length() > 0) {
                                                writeStringToConsole((new StringBuilder()).append("say ").append((String) messageMap.get("auto-save-complete")).append("\n").toString());
                                            }
                                            shutdownSave = false;
                                            saveInProgress = false;
                                        }
                                        flag = true;
                                        stringbuilder.delete(0, j + 1);
                                        j = 0;
                                    }
                                }
                            } catch (Exception exception1) {
                                if (!exception1.toString().equals("java.io.IOException: Stream closed")) {
                                    McRKitLauncher.sendStackTrace(exception1, "WR:STO:");
                                }
                                exception1.printStackTrace();
                                try {
                                    bufferedreader.close();
                                } catch (Exception exception2) {
                                }
                            }
                        }
                    };
                    thread.setDaemon(true);
                    thread.start();

                    thread = new Thread("stderr") {

                        public void run() {
                            BufferedReader bufferedreader = null;
                            boolean flag = false;
                            short word0 = 0;
                            int ai[] = new int[5];
                            int ai1[][] = {
                                {
                                    91, 48, 109
                                }
                            };
                            String s = "\033[0m";
                            try {
                                PrintStream printstream = System.err;
                                bufferedreader = new BufferedReader(new InputStreamReader(mcProcess.getErrorStream()), 1024);
                                boolean flag1 = false;
                                StringBuilder stringbuilder = new StringBuilder(100);
                                int j = 0;
                                boolean flag2 = true;
                                do {
                                    int i;
                                    if ((i = bufferedreader.read()) < 0) {
                                        break;
                                    }
                                    if (i != System.getProperty("line.separator").charAt(System.getProperty("line.separator").length() - 1)) {
                                        if (!flag) {
                                            if (i == 27) {
                                                flag = true;
                                                word0 = 0;
                                            } else {
                                                stringbuilder.appendCodePoint(i);
                                            }
                                            j++;
                                        } else {
                                            ai[word0++] = i;
                                            if (word0 >= ai.length || i == 109) {
                                                flag = false;
                                                boolean flag3 = false;
                                                int ai2[][] = ai1;
                                                int l = ai2.length;
                                                for (int j1 = 0; j1 < l; j1++) {
                                                    int ai3[] = ai2[j1];
                                                    if (ai3.length != word0) {
                                                        continue;
                                                    }
                                                    flag3 = true;
                                                    int k1 = 0;
                                                    do {
                                                        if (k1 >= ai3.length) {
                                                            break;
                                                        }
                                                        if (ai3[k1] != ai[k1]) {
                                                            flag3 = false;
                                                            break;
                                                        }
                                                        k1++;
                                                    } while (true);
                                                    if (flag3) {
                                                        break;
                                                    }
                                                }

                                                if (!flag3) {
                                                    stringbuilder.appendCodePoint(27);
                                                    int k = 0;
                                                    while (k < word0) {
                                                        stringbuilder.appendCodePoint(ai[k]);
                                                        j++;
                                                        k++;
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        String s1 = stringbuilder.toString();
                                        String s2 = ansiPattern.matcher(s1).replaceAll("").trim();
                                        if (((!serverTypeKnown || !vanillaServer) && s2.endsWith("Save complete.") || (!serverTypeKnown || vanillaServer) && s2.endsWith("Saved the world")) && (saveInProgress || shutdownSave)) {
                                            if (!serverTypeKnown) {
                                                serverTypeKnown = true;
                                                if (s2.endsWith("Save complete.")) {
                                                    vanillaServer = false;
                                                } else {
                                                    vanillaServer = true;
                                                }
                                            }
                                            if (!shutdownSave && ((String) messageMap.get("auto-save-complete")).length() > 0) {
                                                writeStringToConsole((new StringBuilder()).append("say ").append((String) messageMap.get("auto-save-complete")).append("\n").toString());
                                            }
                                            shutdownSave = false;
                                            saveInProgress = false;
                                        } else if (severeRestart && !severeRestartInProgress && s1.contains("SEVERE") && stringConstitutesSevereRestart(s2.trim())) {
                                            severeRestartInProgress = true;
                                            McRKitLauncher.sendInfoString((new StringBuilder()).append("SEVERE_RESTART:").append(s1).toString());
                                            System.out.println("Severe exception detected! Attempting to restart the server...");
                                            scheduler.refresh();
                                            scheduledTasks.clear();
                                            scheduler.scheduleKillEvent(scheduler.scheduleRestartEvent(System.currentTimeMillis() + 5000L).scheduledExecutionTime() + 60000L);
                                        }
                                        if (flag2) {
                                            if (!ansiMode) {
                                                System.err.println(s2);
                                            } else {
                                                System.err.print(s1);
                                                System.err.println(s);
                                            }
                                            if (useTelnet) {
                                                enqueueStringToShells((new StringBuilder()).append(s1).append(s).toString());
                                            }
                                            try {
                                                HookQueue.getInstance().put(new HookEvent(HookEvent.Type.STDERR, s1));
                                            } catch (InterruptedException interruptedexception1) {
                                            }
                                            if (debugMode) {
                                                String s3 = "";
                                                for (int i1 = 0; i1 < s1.length(); i1++) {
                                                    s3 = (new StringBuilder()).append(s3).append(s1.charAt(i1)).append(",").toString();
                                                }

                                                printLnToAll((new StringBuilder()).append(s3).append("=err=").toString());
                                            }
                                        }
                                        stringbuilder.delete(0, j + 1);
                                        j = 0;
                                        flag2 = true;
                                    }
                                } while (true);
                            } catch (Exception exception1) {
                                if (!exception1.toString().equals("java.io.IOException: Stream closed")) {
                                    McRKitLauncher.sendStackTrace(exception1, "WR:STE:");
                                }
                                exception1.printStackTrace();
                                try {
                                    bufferedreader.close();
                                } catch (Exception exception2) {
                                }
                            }
                        }
                    };
                    thread.setDaemon(true);
                    thread.start();
                }


                if (stdInThread == null) {
                    stdInThread = new Thread("stdin") {

                        public void run() {
                            try {
                                boolean flag;
                                String s;
                                flag = true;
                                s = "";
                                if (reader == null) {
                                    return;
                                }
                                String s1 = reader.readLine(">");
                                if (s1 == null) {
                                    try {
                                        System.err.println("ERROR: Could not read from stdin; waiting 10 seconds before retrying...");
                                        sleep(10000L);
                                    } catch (InterruptedException interruptedexception2) {
                                    }
                                }
                                try {
                                    s1 = s1.trim();
                                    reader.setPrompt("");
                                    if (!s1.startsWith(".") || !parseConsoleInput(s1.substring(1))) {
                                        if (serverRunning) {
                                            console.write((new StringBuilder()).append(s1).append("\n").toString().getBytes());
                                            console.flush();

                                        }
                                        try {
                                            HookQueue.getInstance().put(new HookEvent(HookEvent.Type.STDIN, s1));
                                        } catch (InterruptedException interruptedexception3) {
                                            System.out.println(interruptedexception3);
                                        }
                                    }
                                } catch (Exception exception1) {
                                    if (exception1.toString().contains("Bad file descriptor") || exception1.toString().contains("Broken pipe") || exception1.toString().contains("Gebroken pijp") || exception1.toString().contains("Input/output error") || exception1.toString().contains("Ung\374ltiger Dateideskriptor") || exception1.toString().contains("The handle is invalid") || exception1.toString().contains("Relais bris\351")) {
                                        System.err.println((new StringBuilder()).append("ERROR: Exception caught when reading from stdin: ").append(exception1).toString());
                                        flag = false;
                                    }
                                    McRKitLauncher.sendStackTrace(exception1, "WR:STIN:");
                                    exception1.printStackTrace();
                                }
                                int i = -1;

                                try {
                                    while ((i = System.in.read()) >= 0) {
                                        try {
                                            if (i != 13 && i != 10) {
                                                s = (new StringBuilder()).append(s).append((char) i).toString();
                                                myReader.getOutput().append(s);
                                                myReader.flush();
                                            } else {
                                                s = s.trim();
                                                if (!s.startsWith(".") || !parseConsoleInput(s.substring(1))) {
                                                    if (serverRunning) {
                                                        console.write((new StringBuilder()).append(s).append('\n').toString().getBytes());
                                                        console.flush();
                                                    }
                                                    try {
                                                        HookQueue.getInstance().put(new HookEvent(HookEvent.Type.STDIN, s));
                                                    } catch (InterruptedException interruptedexception4) {
                                                        System.out.println(interruptedexception4);
                                                    }
                                                }
                                                s = "";
                                                /*for (int k = 0; k < 10; k++) {
                                                System.out.println();
                                                }*/
                                            }
                                        } catch (IOException ioexception1) {
                                            s = "";
                                            if (!ioexception1.toString().equals("java.io.IOException: Broken pipe") && !ioexception1.toString().equals("java.io.IOException: The pipe is being closed")) {
                                                throw ioexception1;
                                            }
                                        }
                                    }

                                } catch (Exception exception2) {
                                    if (exception2.toString().contains("Bad file descriptor") || exception2.toString().contains("Broken pipe") || exception2.toString().contains("Gebroken pijp") || exception2.toString().contains("Input/output error") || exception2.toString().contains("Ung\374ltiger Dateideskriptor") || exception2.toString().contains("The handle is invalid") || exception2.toString().contains("Relais bris\351")) {
                                        System.err.println((new StringBuilder()).append("ERROR: Exception caught when reading from stdin: ").append(exception2).toString());
                                        flag = false;
                                    }
                                    McRKitLauncher.sendStackTrace(exception2, "WR:STIN:");
                                    exception2.printStackTrace();
                                }

                                /*Thread e = new Thread(new Runnable() {
                                
                                public void run() {
                                try {
                                while(serverRunning)
                                {
                                String s1 = reader.readLine(">");
                                if (s1 == null) {
                                try {
                                System.err.println("ERROR: Could not read from stdin; waiting 10 seconds before retrying...");
                                sleep(1000L);
                                } catch (InterruptedException interruptedexception2) {
                                }
                                }
                                s1 = s1.trim();
                                reader.setPrompt("");
                                console.write((new StringBuilder()).append(s1).append('\n').toString().getBytes());
                                }
                                } catch (IOException ex) {
                                Logger.getLogger(Wrapper.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                }
                                });
                                e.setDaemon(true);
                                e.start();*/
                                if (flag) {
                                    System.err.println("Error: Console input stream closed unexpectedly. Attempting to re-open stream in 20 seconds...");
                                    try {
                                        sleep(20000L);
                                    } catch (InterruptedException interruptedexception1) {
                                    }
                                    System.err.println("Console input stream re-opening...");
                                } else {
                                    System.err.println("Standard input stream has closed externally and cannot be recovered!");
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(Wrapper.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    ;
                    };
                stdInThread.setDaemon(true);
                    stdInThread.start();
                }
                if (xpFix && !startHeld) {
                    try {
                        console.write("\r\n".getBytes());
                        console.flush();
                        System.out.println("\r\n");
                    } catch (Exception exception) {
                    }
                }

                if (!startHeld) {
                    process.waitFor();
                } else {
                    startHeld = false;
                    pauseMode = true;
                    restarting = true;
                }
                restartTime = 0L;
                serverID++;
                scheduler.refresh();

                scheduledTasks.clear();
                serverRunning = false;
                shutdownSave = false;

                if (restarting) {
                    if (pauseMode) {
                        System.out.println("Server is being held...");
                        ModuleManager.getInstance().handleToolkitStateEvent(ToolkitEvent.ON_SERVER_HOLD);
                        while (pauseMode) {
                            Thread.sleep(1000L);
                        }
                    }
                    ModuleManager.getInstance().handleToolkitStateEvent(ToolkitEvent.ON_SERVER_RESTART);
                    System.out.println("Restarting...");
                    System.gc();
                    scheduler.refresh();
                    Thread.sleep(3000L);
                }
            } catch (IOException ioexception) {
                System.out.println("Severe error in Minecraft Remote Toolkit wrapper!");
                McRKitLauncher.sendStackTrace(ioexception, "WR:");
                ioexception.printStackTrace();
                System.exit(0);
            } catch (InterruptedException interruptedexception) {
                System.out.println("Non-severe Thread error in wrapper, but unexpected behavior may result.");
            }
        } while (restarting);
        serverRunning = false;
        if (TelnetD.getReference() != null) {
            TelnetD.getReference().stop();
        }
        if (reader != null) {
            reader.getTerminal().setEchoEnabled(true);
        }
    }

    public synchronized void cancelTasks(Scheduler.Type type) {
        try {
            Iterator iterator = scheduledTasks.iterator();
            do {
                if (!iterator.hasNext()) {
                    break;
                }
                ScheduledTask scheduledtask = (ScheduledTask) iterator.next();
                if (scheduledtask != null && scheduledtask.getEvent().getType() == type) {
                    scheduledtask.cancel();
                }
            } while (true);
        } catch (Exception exception) {
            exception.printStackTrace();
            McRKitLauncher.sendStackTrace(exception, "WR:");
        }
    }

    public void handleUDPEvent(DatagramSocket datagramsocket, DatagramPacket datagrampacket, String s) {
        String s1;
        String s2;
        String as[];
        boolean flag = false;
        s1 = "response:success";
        s2 = "response:command_error";
        String s3 = "response:authentication_error";
        as = s.split(":");
        if (as.length < 3) {
            return;
        }
        if (as.length > 3 && as[as.length - 3].equals("2")) {
            flag = true;
        }
        if (!users.isValidLogon(as[as.length - 2], as[as.length - 1], flag, flag)) {
            try {
                DatagramPacket datagrampacket1 = new DatagramPacket(s3.getBytes(), s3.getBytes().length, datagrampacket.getAddress(), datagrampacket.getPort());
                datagramsocket.send(datagrampacket1);
            } catch (Exception exception) {
            }
            return;
        }
        as[0] = as[0].trim().toUpperCase();
        if (as[0].equals("ENABLE")) {
            try {
                DatagramPacket datagrampacket2 = new DatagramPacket(s1.getBytes(), s1.getBytes().length, datagrampacket.getAddress(), datagrampacket.getPort());
                datagramsocket.send(datagrampacket2);
                performAction(ToolkitAction.ENABLE, null);
            } catch (Exception exception1) {
            }
        } else if (as[0].equals("DISABLE")) {
            try {
                DatagramPacket datagrampacket3 = new DatagramPacket(s1.getBytes(), s1.getBytes().length, datagrampacket.getAddress(), datagrampacket.getPort());
                datagramsocket.send(datagrampacket3);
                performAction(ToolkitAction.DISABLE, null);
            } catch (Exception exception2) {
            }
        } else if (as[0].equals("RESTART")) {
            try {
                DatagramPacket datagrampacket4 = new DatagramPacket(s1.getBytes(), s1.getBytes().length, datagrampacket.getAddress(), datagrampacket.getPort());
                datagramsocket.send(datagrampacket4);
                performAction(ToolkitAction.RESTART, null);
            } catch (Exception exception3) {
            }
        } else if (as[0].equals("FORCERESTART")) {
            try {
                DatagramPacket datagrampacket5 = new DatagramPacket(s1.getBytes(), s1.getBytes().length, datagrampacket.getAddress(), datagrampacket.getPort());
                datagramsocket.send(datagrampacket5);
                performAction(ToolkitAction.FORCERESTART, null);
            } catch (Exception exception4) {
            }
        } else if (as[0].equals("STOPWRAPPER")) {
            try {
                DatagramPacket datagrampacket6 = new DatagramPacket(s1.getBytes(), s1.getBytes().length, datagrampacket.getAddress(), datagrampacket.getPort());
                datagramsocket.send(datagrampacket6);
                performAction(ToolkitAction.STOPWRAPPER, null);
            } catch (Exception exception5) {
                McRKitLauncher.sendStackTrace(exception5, "WR:");
                exception5.printStackTrace();
            }
        } else if (as[0].equals("FORCESTOP")) {
            try {
                DatagramPacket datagrampacket7 = new DatagramPacket(s1.getBytes(), s1.getBytes().length, datagrampacket.getAddress(), datagrampacket.getPort());
                datagramsocket.send(datagrampacket7);
                performAction(ToolkitAction.FORCESTOP, null);
            } catch (Exception exception6) {
            }
        } else if (as[0].equals("VERSION")) {
            try {
                DatagramPacket datagrampacket8 = new DatagramPacket("R10 Alpha 15.3".getBytes(), "R10 Alpha 15.3".getBytes().length, datagrampacket.getAddress(), datagrampacket.getPort());
                datagramsocket.send(datagrampacket8);
            } catch (Exception exception7) {
            }
        } else if (as[0].equals("HOLD")) {
            try {
                DatagramPacket datagrampacket9 = new DatagramPacket(s1.getBytes(), s1.getBytes().length, datagrampacket.getAddress(), datagrampacket.getPort());
                datagramsocket.send(datagrampacket9);
                performAction(ToolkitAction.HOLD, null);
            } catch (Exception exception8) {
            }
        } else if (as[0].equals("UNHOLD")) {
            try {
                DatagramPacket datagrampacket10 = new DatagramPacket(s1.getBytes(), s1.getBytes().length, datagrampacket.getAddress(), datagrampacket.getPort());
                datagramsocket.send(datagrampacket10);
                performAction(ToolkitAction.UNHOLD, null);
            } catch (Exception exception9) {
            }
        } else if (!as[0].equals("RESCHEDULE")) {
            return;
        }
        if (as.length < 2) {
            return;
        }
        try {
            String s4 = as[1].trim().toLowerCase();
            if (performAction(ToolkitAction.RESCHEDULE, s4)) {
                try {
                    DatagramPacket datagrampacket11 = new DatagramPacket(s1.getBytes(), s1.getBytes().length, datagrampacket.getAddress(), datagrampacket.getPort());
                    datagramsocket.send(datagrampacket11);
                } catch (Exception exception10) {
                }
            } else {
                try {
                    DatagramPacket datagrampacket12 = new DatagramPacket(s2.getBytes(), s2.getBytes().length, datagrampacket.getAddress(), datagrampacket.getPort());
                    datagramsocket.send(datagrampacket12);
                } catch (Exception exception11) {
                }
            }
        } catch (NullPointerException nullpointerexception) {
        }
    }

    public boolean performAction(ToolkitAction toolkitaction, String s) {
        {
            switch (toolkitaction.ordinal()) {
                case 1:
                    this.restarting = true;
                    System.out.println("Enabled restarts...");
                    return true;
                case 2:
                    this.restarting = false;
                    System.out.println("Disabled restarts...");
                    return true;
                case 3:
                    if (this.pauseMode) {
                        this.pauseMode = false;
                        return true;
                    }
                    if (!this.serverRunning) {
                        return true;
                    }
                    this.restarting = true;
                    System.out.println("Restarting server...");
                    cancelTasks(Scheduler.Type.SAVE);
                    try {
                        if (this.saveOnServerStop) {
                            saveAndWait();
                        }
                        this.console.write(("say " + (String) this.messageMap.get("restart-warning") + "\n").getBytes());
                        this.console.flush();
                        Thread.sleep(3000L);
                        this.console.write("kickall\n".getBytes());
                        this.console.flush();
                        Thread.sleep(3000L);
                        this.console.write((this.stopCommand + "\n").getBytes());
                        this.console.flush();
                    } catch (Exception localException1) {
                    }
                    return true;
                case 4:
                    if (!this.serverRunning) {
                        return true;
                    }
                    this.restarting = true;
                    System.out.println("Restarting server...");
                    cancelTasks(Scheduler.Type.SAVE);
                    try {
                        destroyProcess(this.mcProcess);
                    } catch (Exception localException2) {
                    }
                    return true;
                case 5:
                    try {
                        this.restarting = false;
                        System.out.println("Stopping the wrapper...");
                        cancelTasks(Scheduler.Type.SAVE);
                        if (this.saveOnServerStop) {
                            saveAndWait();
                        }
                        this.console.write(("say " + (String) this.messageMap.get("toolkit-shutdown-warning") + "\n").getBytes());
                        this.console.flush();
                        Thread.sleep(3000L);
                        this.console.write("kickallstop\n".getBytes());
                        this.console.flush();
                        Thread.sleep(3000L);
                        this.console.write((this.stopCommand + "\n").getBytes());
                        this.console.flush();




                    } catch (Exception localException3) {
                    }
                    return true;
                case 6:
                    try {
                        this.restarting = false;
                        System.out.println("Forcing stop...");
                        cancelTasks(Scheduler.Type.SAVE);
                        destroyProcess(this.mcProcess);
                    } catch (Exception localException4) {
                    }
                    return true;
                case 7:
                    try {
                        System.out.println("Stopping and holding the server...");
                        this.pauseMode = true;
                        this.restarting = true;

                        if (!this.serverRunning) {
                            return true;
                        }
                        cancelTasks(Scheduler.Type.SAVE);
                        if (this.saveOnServerStop) {
                            saveAndWait();
                        }
                        this.console.write(("say " + (String) this.messageMap.get("hold-warning") + "\n").getBytes());
                        this.console.flush();
                        Thread.sleep(3000L);
                        this.console.write("kickallhold\n".getBytes());
                        this.console.flush();
                        Thread.sleep(3000L);
                        this.console.write((this.stopCommand + "\n").getBytes());
                        this.console.flush();
                    } catch (Exception localException5) {
                    }
                    return true;
                case 8:
                    this.pauseMode = false;
                    return true;
                case 9:
                    s = s.replaceAll("-", ":");
                    return rescheduleRestart(s);
            }
        }
        return false;
    }

    public boolean rescheduleRestart(String s) {
        if (s.contains(":")) {
            try {
                Scheduler.getOrderedTimes(new String[]{
                            s
                        });
            } catch (Exception exception) {
                System.out.println((new StringBuilder()).append("INVALID TIME: ").append(s).toString());
                return false;
            }
        } else if (Scheduler.convertFormattedTime(s) == -1L) {
            System.out.println((new StringBuilder()).append("INVALID TIME: ").append(s).toString());
            return false;
        }
        Iterator iterator = scheduledTasks.iterator();
        do {
            if (!iterator.hasNext()) {
                break;
            }
            ScheduledTask scheduledtask = (ScheduledTask) iterator.next();
            if (scheduledtask != null && (scheduledtask.getEvent().getType() == Scheduler.Type.RESTART || scheduledtask.getEvent().getType() == Scheduler.Type.RESTARTALERT || scheduledtask.getEvent().getType() == Scheduler.Type.KILL)) {
                scheduledtask.cancel();
            }
        } while (true);
        scheduler.purge();
        if (!s.equals("0")) {
            String s1 = toolkitProperties.getString("server-restart-alerts").toLowerCase().trim();
            ScheduledTask scheduledtask1 = scheduler.scheduleRestartEvent(s, s.contains(":"));
            scheduledTasks.add(scheduledtask1);
            restartTime = scheduledtask1.scheduledExecutionTime();
            scheduledTasks.add(scheduler.scheduleKillEvent(restartTime + Scheduler.convertFormattedTime(toolkitProperties.getString("forced-restart-delay").toLowerCase().trim())));
            String as[] = s1.split(",");
            int i = as.length;
            for (int j = 0; j < i; j++) {
                String s2 = as[j];
                scheduledTasks.add(scheduler.scheduleAlertEvent(s2.trim().toLowerCase(), Long.valueOf(restartTime), (String) messageMap.get("restart-time-warning"), Scheduler.Type.RESTARTALERT));
            }

        }
        return true;
    }

    public void destroy() {
        destroyProcess(mcProcess);
    }

    public void disable() {
        restarting = false;
    }

    public void enable() {
        restarting = true;
    }

    public void handleScheduledEvent(final ScheduledEvent e) {
        if (e.getType() != Scheduler.Type.HEARTBEAT) {
            new Thread() {

                public void run() {
                    handleScheduledEventThread(e);
                }
            }.start();
        } else {
            handleScheduledEventThread(e);
        }
    }

    public void handleScheduledEventThread(ScheduledEvent scheduledevent) {
        try {
            int i = serverID;
            if (scheduledevent.getType() == Scheduler.Type.RESTART) {
                cancelTasks(Scheduler.Type.SAVE);
                if (saveOnServerStop) {
                    saveAndWait();
                }
                try {
                    if (scheduledevent.getMessage().length() > 0) {
                        console.write((new StringBuilder()).append("say ").append(scheduledevent.getMessage()).append("\n").toString().getBytes());
                        console.flush();
                    }
                    Thread.sleep(3000L);
                    console.write("kickall\n".getBytes());
                    console.flush();
                    Thread.sleep(3000L);
                    console.write((new StringBuilder()).append(stopCommand).append("\n").toString().getBytes());
                    console.flush();
                } catch (IOException ioexception) {
                    System.err.println("[ERROR] IOException on restart");
                    McRKitLauncher.sendStackTrace(ioexception, "WR:");
                    ioexception.printStackTrace();
                } catch (InterruptedException interruptedexception) {
                    System.err.println("[ERROR] InterruptedException on restart");
                    McRKitLauncher.sendStackTrace(interruptedexception, "WR:");
                    interruptedexception.printStackTrace();
                }
            } else if (scheduledevent.getType() == Scheduler.Type.SHUTDOWN) {
                restarting = false;
                cancelTasks(Scheduler.Type.SAVE);
                if (saveOnServerStop) {
                    saveAndWait();
                }
                if (i != serverID) {
                    return;
                }
                try {
                    if (scheduledevent.getMessage().length() > 0) {
                        console.write((new StringBuilder()).append("say ").append(scheduledevent.getMessage()).append("\n").toString().getBytes());
                        console.flush();
                    }
                    Thread.sleep(3000L);
                    console.write("kickallstop\n".getBytes());
                    console.flush();
                    Thread.sleep(3000L);
                    console.write((new StringBuilder()).append(stopCommand).append("\n").toString().getBytes());
                    console.flush();
                } catch (IOException ioexception1) {
                    System.err.println("[ERROR] IOException on toolkit shutdown");
                    McRKitLauncher.sendStackTrace(ioexception1, "WR:");
                    ioexception1.printStackTrace();
                } catch (InterruptedException interruptedexception1) {
                    System.err.println("[ERROR] InterruptedException on toolkit shutdown");
                    McRKitLauncher.sendStackTrace(interruptedexception1, "WR:");
                    interruptedexception1.printStackTrace();
                }
            } else if (scheduledevent.getType() == Scheduler.Type.MESSAGE || scheduledevent.getType() == Scheduler.Type.RESTARTALERT || scheduledevent.getType() == Scheduler.Type.SHUTDOWNALERT) {
                try {
                    if ((scheduledevent.getType() != Scheduler.Type.RESTARTALERT || shutdownTime <= 0L || shutdownTime >= restartTime) && scheduledevent.getMessage().length() > 0) {
                        console.write((new StringBuilder()).append("say ").append(scheduledevent.getMessage()).append("\n").toString().getBytes());
                        console.flush();
                    }
                } catch (IOException ioexception2) {
                    System.err.println("[ERROR] IOException writing to the console");
                    McRKitLauncher.sendStackTrace(ioexception2, "WR:");
                    ioexception2.printStackTrace();
                }
            } else if (scheduledevent.getType() == Scheduler.Type.SAVE) {
                try {
                    saveInProgress = true;
                    if (scheduledevent.getMessage().length() > 0) {
                        console.write((new StringBuilder()).append("say ").append(scheduledevent.getMessage()).append("\n").toString().getBytes());
                        console.flush();
                    }
                    console.write("save-all\n".getBytes());
                    console.flush();
                } catch (IOException ioexception3) {
                    System.err.println("[ERROR] IOException writing to the console");
                    McRKitLauncher.sendStackTrace(ioexception3, "WR:");
                    ioexception3.printStackTrace();
                }
            } else if (scheduledevent.getType() == Scheduler.Type.KILL) {
                try {
                    cancelTasks(Scheduler.Type.SAVE);
                    System.out.println("Server is being forcibly restarted...");
                    McRKitLauncher.sendInfoString("FORCED_RESTART_TRIGGERED");
                    destroyProcess(mcProcess);
                } catch (Exception exception) {
                    System.err.println("[ERROR] Exception killing the server");
                    McRKitLauncher.sendStackTrace(exception, "WR:");
                    exception.printStackTrace();
                }
            } else if (scheduledevent.getType() == Scheduler.Type.HEARTBEAT) {
                if (!gotHeartbeatResponse) {
                    if (heartbeatCount >= 3) {
                        System.out.println("RemoteToolkit plugin for Bukkit was not detected. Disabling heartbeat until next restart.");
                        cancelTasks(Scheduler.Type.HEARTBEAT);
                        return;
                    }
                    if (heartbeatCount < numberWords.length) {
                        System.out.println((new StringBuilder()).append(numberWords[heartbeatCount]).append(" attempt at checking for presence of the RemoteToolkit plugin for Bukkit...").toString());
                    }
                }
                try {
                    if (debugMode) {
                        System.err.println("PING");
                        enqueueStringToShells("PING");
                    }
                    heartbeatCount++;
                    console.write("RTPING\n".getBytes());
                    console.flush();
                } catch (IOException ioexception4) {
                    System.err.println((new StringBuilder()).append("[ERROR] IOException writing to the console: ").append(ioexception4).toString());
                    if (!ioexception4.toString().equals("java.io.IOException: Bad file descriptor")) {
                        McRKitLauncher.sendStackTrace(ioexception4, "WRHB:");
                        ioexception4.printStackTrace();
                    }
                }
            } else if (scheduledevent.getType() == Scheduler.Type.HEARTBEATRESTART) {
                try {
                    cancelTasks(Scheduler.Type.SAVE);
                    System.out.println("Heartbeat failure threshold exceeded!");
                    System.out.println("Attempting to restart server...");
                    scheduler.scheduleKillEvent(scheduler.scheduleRestartEvent(System.currentTimeMillis() + 5000L).scheduledExecutionTime() + 60000L);
                    McRKitLauncher.sendInfoString((new StringBuilder()).append("HEARTBEAT_RESTART_TRIGGERED:").append(heartbeatDelay).append(":").append(heartbeatRestartDelay).toString());
                } catch (Exception exception1) {
                    System.err.println("[ERROR] Exception killing the server");
                    McRKitLauncher.sendStackTrace(exception1, "WRHBR:");
                    exception1.printStackTrace();


                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Wrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Wrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean stringConstitutesSevereRestart(String s) {
        if (severeLevel == 1) {
            if (s.contains("[SEVERE] Unexpected exception")) {
                return true;
            }
        } else if (severeLevel == 2) {
            if (s.startsWith("SEVERE:")) {
                return true;
            }
        } else if (s.contains("[SEVERE]")) {
            return true;
        }
        return false;
    }

    private void saveAndWait()
            throws IOException, InterruptedException {
        if (shutdownSave) {
            System.err.println("WARNING: A shutdown is already in progress.");
            return;
        }
        printLnToAll("Forcing a save... (Shutdown delayed)");
        shutdownSave = true;
        console.write("save-all\n".getBytes());
        console.flush();
        for (int i = 0; i++ < 70 && shutdownSave;) {
            Thread.sleep(1000L);
        }

    }

    public static void destroyProcess(Process process) {
        if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            System.out.println("Attempting a SIGKILL on the process.");
            try {
                if (killUnixProcess(process) != 0) {
                    System.err.println("The SIGKILL may not have been successful - attempting a SIGTERM...");
                    process.destroy();
                }
            } catch (Exception exception) {
                System.err.println("Error trying to destroy process:");
                exception.printStackTrace();
            }
        } else {
            process.destroy();
        }
    }

    public static int getUnixPID(Process process)
            throws Exception {
        Class class1 = process.getClass();
        Field field = class1.getDeclaredField("pid");
        field.setAccessible(true);
        Object obj = field.get(process);
        return ((Integer) obj).intValue();
    }

    public static int killUnixProcess(Process process)
            throws Exception {
        int i = getUnixPID(process);
        return Runtime.getRuntime().exec((new StringBuilder()).append("kill -9 ").append(i).toString()).waitFor();
    }

    public void writeStringToConsole(String s)
            throws IOException {
        if (serverRunning) {
            console.write(s.getBytes());
            console.flush();
        }
    }

    private void enqueueStringToShells(String s) {
        if (useTelnet) {
            synchronized (this) {
                playbackBuffer.add(s);
                if (playbackBuffer.size() > playbackCount) {
                    playbackBuffer.remove(0);
                }
                for (Iterator iterator = registeredShells.iterator(); iterator.hasNext();) {
                    ConsoleListener consolelistener = (ConsoleListener) iterator.next();
                    try {
                        consolelistener.queueOutputString(s);
                    } catch (InterruptedException interruptedexception) {
                        McRKitLauncher.sendStackTrace(interruptedexception, "WR:");
                        interruptedexception.printStackTrace();
                    }
                }

            }
        }
    }

    public synchronized void registerTelnetShell(ConsoleListener consolelistener) {
        if (!registeredShells.contains(consolelistener)) {
            registeredShells.add(consolelistener);
        }
    }

    public synchronized void deregisterTelnetShell(ConsoleListener consolelistener) {
        registeredShells.remove(consolelistener);
    }

    public synchronized boolean login(String s, String s1, boolean flag, boolean flag1) {
        return users.isValidLogon(s, s1, flag, flag1);
    }

    public synchronized void printLnToAll(String s) {
        enqueueStringToShells(s);
        System.out.println(s);
    }

    public synchronized List getPlaybackBuffer() {
        return new LinkedList(playbackBuffer);
    }

    public boolean parseConsoleInput(String s) {
        String s2;
        String s1 = s.toLowerCase();
        s2 = "";
        if (s1.length() > 0) {
            s2 = s1.split(" ")[0].trim();
        } else {
            return false;
        }
        if (s2.equals("help")) {
            printLnToAll("Minecraft Remote Toolkit help:");
            printLnToAll("  .help                       Displays this message.");
            printLnToAll("  .hold                       Stops the server until .unhold is used.");
            printLnToAll("  .unhold                     Unholds a held server.");
            printLnToAll("  .modules                    Lists the currently loaded Toolkit modules.");
            printLnToAll("  .restarttime                Print time left until server restart.");
            printLnToAll("  .shutdowntime               Print time left until toolkit shutdown.");
            printLnToAll("  .restart                    Gracefully restarts the server.");
            printLnToAll("  .forcerestart               Forces the server to restart.");
            printLnToAll("  .reschedulerestart <time>   Reschedules the next restart.");
            printLnToAll("  .rescheduleshutdown <time>  (Re)Schedules a total wrapper shutdown.");
            printLnToAll("  .cancelshutdown             Cancels a scheduled shutdown.");
            printLnToAll("  .stopwrapper                Gracefully stops the wrapper.");
            printLnToAll("  .forcestopwrapper           Forces the wrapper to stop.");
            printLnToAll("  .users                      List toolkit users.");
            printLnToAll("  .useradd <name> <password>  Add a toolkit user.");
            printLnToAll("  .userremove <name>          Remove a toolkit user.");
            printLnToAll("  .set <option> <value>       Changes a setting. Type .set help for info.");
            printLnToAll("  .version                    Displays the Remote toolkit version.");
            printLnToAll("\nMinecraft Remote Toolkit module commands:");
            for (Iterator iterator = ModuleManager.getInstance().getModules().iterator(); iterator.hasNext();) {
                Module module = (Module) iterator.next();
                Iterator iterator3 = module.getCommandMap().keySet().iterator();
                while (iterator3.hasNext()) {
                    String s11 = (String) iterator3.next();
                    printLnToAll((new StringBuilder()).append(".").append(s11).append(" ").append((String) module.getCommandMap().get(s11)).toString());
                }
            }

            return true;
        }
        if (s2.equals("hold")) {
            if (!serverRunning) {
                printLnToAll("Cannot hold server: The server is not running.");
                return true;
            }
            printLnToAll("Stopping and holding the server...");
            pauseMode = true;
            restarting = true;
            try {
                cancelTasks(Scheduler.Type.SAVE);
                if (saveOnServerStop) {
                    saveAndWait();
                }
                console.write((new StringBuilder()).append("say ").append((String) messageMap.get("hold-warning")).append("\n").toString().getBytes());
                console.flush();
                Thread.sleep(3000L);
                console.write("kickallhold\n".getBytes());
                console.flush();
                Thread.sleep(3000L);
                console.write((new StringBuilder()).append(stopCommand).append("\n").toString().getBytes());
                console.flush();
            } catch (Exception exception) {
            }
            return true;
        }
        if (s2.equals("unhold")) {
            if (serverRunning) {
                printLnToAll("Cannot start server: The server is already running.");
                return true;
            }
            if (!pauseMode) {
                printLnToAll("Cannot start server: The server is not being held.");
                return true;
            } else {
                pauseMode = false;
                return true;
            }
        }
        if (s2.equals("modules")) {
            printLnToAll("Currently loaded Toolkit modules:");
            Module module1;
            for (Iterator iterator1 = ModuleManager.getInstance().getModules().iterator(); iterator1.hasNext(); printLnToAll((new StringBuilder()).append("  ").append((String) module1.getMetadata().get("name")).append(module1.isEnabled() ? " [E]" : " [D]").toString())) {
                module1 = (Module) iterator1.next();
            }

            return true;
        }
        if (s2.equals("restarttime")) {
            printLnToAll((new StringBuilder()).append("Next restart is in ").append(Scheduler.getDetailedTimeString(restartTime - System.currentTimeMillis())).toString());
            return true;
        }
        if (s2.equals("shutdowntime")) {
            printLnToAll(shutdownTime <= 0L ? "No scheduled shutdown!" : (new StringBuilder()).append("Next shutdown is in ").append(Scheduler.getDetailedTimeString(shutdownTime - System.currentTimeMillis())).toString());
            return true;
        }
        if (s2.equals("restart")) {
            if (pauseMode) {
                pauseMode = false;
                return true;
            }
            try {
                cancelTasks(Scheduler.Type.SAVE);
                if (saveOnServerStop) {
                    saveAndWait();
                }
                console.write((new StringBuilder()).append("say ").append((String) messageMap.get("restart-warning")).append("\n").toString().getBytes());
                console.flush();
                Thread.sleep(3000L);
                console.write("kickall\n".getBytes());
                console.flush();
                Thread.sleep(3000L);
                console.write((new StringBuilder()).append(stopCommand).append("\n").toString().getBytes());
                console.flush();
            } catch (IOException ioexception) {
                System.err.println("[ERROR] IOException on restart");
                McRKitLauncher.sendStackTrace(ioexception, "WR:");
                ioexception.printStackTrace();
            } catch (InterruptedException interruptedexception) {
                System.err.println("[ERROR] InterruptedException on restart");
                McRKitLauncher.sendStackTrace(interruptedexception, "WR:");
                interruptedexception.printStackTrace();
            }
            return true;
        } else if (s2.equals("forcerestart")) {
            if (pauseMode) {
                pauseMode = false;
                return true;
            }
            try {
                cancelTasks(Scheduler.Type.SAVE);
                console.write("save-off\n".getBytes());
                console.flush();
                console.write((new StringBuilder()).append("say ").append((String) messageMap.get("restart-warning")).append("\n").toString().getBytes());
                console.flush();
                Thread.sleep(3000L);
                console.write("kickall\n".getBytes());
                console.flush();
                Thread.sleep(3000L);
                destroyProcess(mcProcess);
            } catch (IOException ioexception1) {
                System.err.println("[ERROR] IOException on restart");
                McRKitLauncher.sendStackTrace(ioexception1, "WR:");
                ioexception1.printStackTrace();
            } catch (InterruptedException interruptedexception1) {
                System.err.println("[ERROR] InterruptedException on restart");
                McRKitLauncher.sendStackTrace(interruptedexception1, "WR:");
                interruptedexception1.printStackTrace();
            }
            return true;
        } else if (s2.equals("reschedulerestart")) {
            String s3 = s.substring("reschedulerestart".length()).trim();
            if (s3.length() < 1) {
                printLnToAll("Usage: .reschedulerestart <time>");
                return true;
            }
            String s7 = s3.toLowerCase();
            if (rescheduleRestart(s7)) {
                printLnToAll("Success!");
            } else {
                printLnToAll("Failure!");
            }
            return true;
        } else if (s2.equals("rescheduleshutdown")) {
            String s4 = s.substring("rescheduleshutdown".length()).trim();
            if (s4.length() < 1) {
                printLnToAll("Usage: .rescheduleshutdown <time>");
                return true;
            }
            String s8 = s4.toLowerCase();
            if (scheduleShutdown(s8)) {
                printLnToAll(s8.equals("0") ? "Shutdown cancelled!" : "Success!");
            } else {
                printLnToAll("Failure!");
            }
            return true;
        } else if (s2.equals("cancelshutdown")) {
            String s5 = s.substring("cancelshutdown".length()).trim();
            boolean flag = false;
            Iterator iterator4 = scheduledTasks.iterator();
            do {
                if (!iterator4.hasNext()) {
                    break;
                }
                ScheduledTask scheduledtask = (ScheduledTask) iterator4.next();
                if (scheduledtask != null && (scheduledtask.getEvent().getType() == Scheduler.Type.SHUTDOWN || scheduledtask.getEvent().getType() == Scheduler.Type.SHUTDOWNALERT)) {
                    flag = true;
                    shutdownTime = -1L;
                    scheduledtask.cancel();
                }
            } while (true);
            scheduler.purge();
            printLnToAll(flag ? "Success!" : "No shutdown event to cancel!");
            return true;
        } else if (s2.equals("stopwrapper")) {
            restarting = false;
            try {
                if (serverRunning) {
                    cancelTasks(Scheduler.Type.SAVE);
                    if (saveOnServerStop) {
                        saveAndWait();
                    }
                    console.write((new StringBuilder()).append("say ").append((String) messageMap.get("toolkit-shutdown-warning")).append("\n").toString().getBytes());
                    console.flush();
                    Thread.sleep(3000L);
                    console.write("kickallstop\n".getBytes());
                    console.flush();
                    Thread.sleep(3000L);
                    console.write((new StringBuilder()).append(stopCommand).append("\n").toString().getBytes());
                    console.flush();
                } else {
                    pauseMode = false;
                }
            } catch (Exception exception1) {
                McRKitLauncher.sendStackTrace(exception1, "WR:");
                exception1.printStackTrace();
            }
            return true;
        } else if (s2.equals("set")) {
            execSet(s.substring("set".length()).trim());
            return true;
        } else if (s2.equals("version")) {
            printLnToAll("Minecraft Remote Toolkit R10 Alpha 15.3");
            return true;
        } else if (s2.equals("users")) {
            String s6 = "";
            for (Iterator iterator2 = users.getUserKeys().iterator(); iterator2.hasNext();) {
                String s10 = (String) iterator2.next();
                s6 = (new StringBuilder()).append(s6).append(s10).append(", ").toString();
            }

            printLnToAll((new StringBuilder()).append(users.getUserKeys().size()).append(" user(s):\n").append(s6.length() <= 1 ? "" : s6.substring(0, s6.length() - 2)).toString());
            return true;
        } else if (s2.equals("useradd")) {
            String as[] = s.substring("useradd".length()).trim().split(" ");
            if (as.length != 2) {
                printLnToAll("Usage: .useradd <name> <password>");
                return true;
            }
            if (!users.addUser(as[0], as[1])) {
                printLnToAll("ERROR: That user already exists!");
            } else {
                printLnToAll("Success!");
                users.save();
            }
            return true;
        } else if (s2.equals("userremove")) {
            String as1[] = s.substring("userremove".length()).trim().split(" ");
            if (as1.length != 1) {
                printLnToAll("Usage: .userremove <name>");
                return true;
            }
            if (!users.removeUser(as1[0])) {
                printLnToAll("ERROR: That user does not exist!");
            } else {
                printLnToAll("Success!");
                users.save();
            }
            return true;
        } else if (s2.equals("userauth")) {
            String as2[] = s.substring("userauth".length()).trim().split(" ");
            if (as2.length != 3) {
                printLnToAll("Usage: .userauth <name> <password> <?hashed> Test user authentication.");
                return true;
            }
            if (as2[2].trim().equalsIgnoreCase("yes") || as2[2].trim().equalsIgnoreCase("true")) {
                printLnToAll((new StringBuilder()).append("").append(users.isValidLogon(as2[0], as2[1], true, false)).toString());
            } else {
                printLnToAll((new StringBuilder()).append("").append(users.isValidLogon(as2[0], as2[1], false, false)).toString());
            }
            return true;
        } else if (s2.equals("forcestopwrapper")) {
            restarting = false;
            try {
                if (serverRunning) {
                    cancelTasks(Scheduler.Type.SAVE);
                    console.write((new StringBuilder()).append("say ").append((String) messageMap.get("toolkit-shutdown-warning")).append("\n").toString().getBytes());
                    console.flush();
                    Thread.sleep(3000L);
                    console.write("kickallstop\n".getBytes());
                    console.flush();
                    Thread.sleep(3000L);
                    destroyProcess(mcProcess);
                } else {
                    pauseMode = false;
                }
            } catch (Exception exception2) {
                McRKitLauncher.sendStackTrace(exception2, "WR:");
                exception2.printStackTrace();
            }
            return true;
        } else if (s2.equals("registeredshellcount")) {
            printLnToAll((new StringBuilder()).append("Number of currently registered Telnet shells: ").append(registeredShells.size()).toString());
            return true;
        } else if (s2.equals("configdirs")) {
            printLnToAll(McRKitLauncher.getDebugLines());
            return true;
        }
        String as3[] = s.split(" ", 2);
        String s9 = as3 == null || as3.length <= 0 ? s : as3[0];
        boolean flag1 = false;
        Iterator iterator5 = ModuleManager.getInstance().getModules().iterator();
        do {
            if (!iterator5.hasNext()) {
                break;
            }
            Module module2 = (Module) iterator5.next();
            if (module2.hasCommand(s9)) {
                flag1 = true;
                try {
                    HookQueue.getInstance().put(new HookEvent(HookEvent.Type.COMMAND, s, module2));
                } catch (InterruptedException interruptedexception2) {
                }
            }
        } while (true);
        return flag1;
    }

    private void execSet(String s) {
        String s1 = s.toLowerCase();
        if (s1.startsWith("restarts ")) {
            String s2 = s1.substring("restarts ".length());
            if (s2.equals("on")) {
                restarting = true;
                printLnToAll("Enabling restarts...");
            } else if (s2.equals("off")) {
                restarting = false;
                printLnToAll("Disabling restarts...");
            } else {
                printLnToAll("Invalid value!");
            }
        } else if (s1.startsWith("heartbeatrestarts ")) {
            String s3 = s1.substring("heartbeatrestarts ".length());
            if (s3.equals("on")) {
                heartbeatEnabled = true;
                printLnToAll("Enabling heartbeat restarts...");
            } else if (s3.equals("off")) {
                heartbeatEnabled = false;
                printLnToAll("Disabling heartbeat restarts...");
            } else {
                printLnToAll("Invalid value!");
            }
        } else if (s1.startsWith("debug ")) {
            String s4 = s1.substring("debug ".length());
            if (s4.equals("on")) {
                debugMode = true;
                printLnToAll("Enabling debug mode...");
            } else if (s4.equals("off")) {
                debugMode = false;
                printLnToAll("Disabling debug mode...");
            } else {
                printLnToAll("Invalid value!");
            }
        } else if (s1.equals("help")) {
            printLnToAll("Valid options: ");
            printLnToAll("  restarts on|off               Turns restarts on or off.");
            printLnToAll("  heartbeatrestarts on|off      Turns failed heartbeat restarts on or off.");
        } else {
            printLnToAll("Invalid option! Try .set help");
        }
    }

    public Map getMessageMap() {
        return messageMap;
    }

    public PropertiesFile getToolkitProperties() {
        return toolkitProperties;
    }
    static int WAIT_TIME = 100;
}
