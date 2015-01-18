package com.drdanick.McRKit.plugin;

import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.player.WPlayerInterface;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class MetricsLite {

    private static final int REVISION = 7;
    private static final String BASE_URL = "http://report.mcstats.org";
    private static final String REPORT_URL = "/plugin/%s";
    private static final int PING_INTERVAL = 15;
    private final Plugin plugin;
    private final YamlConfiguration configuration;
    private final File configurationFile;
    private final String guid;
    private final boolean debug;
    private final Object optOutLock = new Object();
    private volatile BukkitTask task = null;

    public MetricsLite(Plugin paramPlugin)
            throws IOException {
        if (paramPlugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.plugin = paramPlugin;


        this.configurationFile = getConfigFile();
        this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);


        this.configuration.addDefault("opt-out", Boolean.valueOf(false));
        this.configuration.addDefault("guid", UUID.randomUUID().toString());
        this.configuration.addDefault("debug", Boolean.valueOf(false));
        if (this.configuration.get("guid", null) == null) {
            this.configuration.options().header("http://mcstats.org").copyDefaults(true);
            this.configuration.save(this.configurationFile);
        }
        this.guid = this.configuration.getString("guid");
        this.debug = this.configuration.getBoolean("debug", false);
    }

    public boolean start() {
        synchronized (this.optOutLock) {
            if (isOptOut()) {
                return false;
            }
            if (this.task != null) {
                return true;
            }
            task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

                private boolean firstPost = false;

                public void run() {
                    try {
                        synchronized (optOutLock) {
                            if (isOptOut() && task != null) {
                                task.cancel();
                                task = null;
                            }
                        }
                        firstPost = !firstPost;
                        postPlugin(firstPost);
                    } catch (IOException ioexception) {
                        if (debug) {
                            Bukkit.getLogger().log(Level.INFO, (new StringBuilder()).append("[Metrics] ").append(ioexception.getMessage()).toString());
                        }
                    }
                }
            }, 0L, 18000L);
        }
        return true;
    }

    public boolean isOptOut() {
        synchronized (this.optOutLock) {
            try {
                this.configuration.load(getConfigFile());
            } catch (IOException localIOException) {
                if (this.debug) {
                    Bukkit.getLogger().log(Level.INFO, "[Metrics] " + localIOException.getMessage());
                }
                return true;
            } catch (InvalidConfigurationException localInvalidConfigurationException) {
                if (this.debug) {
                    Bukkit.getLogger().log(Level.INFO, "[Metrics] " + localInvalidConfigurationException.getMessage());
                }
                return true;
            }
            return this.configuration.getBoolean("opt-out", false);
        }
    }

    public void enable()
            throws IOException {
        synchronized (this.optOutLock) {
            if (isOptOut()) {
                this.configuration.set("opt-out", Boolean.valueOf(false));
                this.configuration.save(this.configurationFile);
            }
            if (this.task == null) {
                start();
            }
        }
    }

    public void disable()
            throws IOException {
        synchronized (this.optOutLock) {
            if (!isOptOut()) {
                this.configuration.set("opt-out", Boolean.valueOf(true));
                this.configuration.save(this.configurationFile);
            }
            if (this.task != null) {
                this.task.cancel();
                this.task = null;
            }
        }
    }

    public File getConfigFile() {
        File localFile = this.plugin.getDataFolder().getParentFile();


        return new File(new File(localFile, "PluginMetrics"), "config.yml");
    }

    private void postPlugin(boolean paramBoolean)
            throws IOException {
        PluginDescriptionFile localPluginDescriptionFile = this.plugin.getDescription();
        String str1 = "RemoteToolkit";
        boolean bool = Bukkit.getServer().getOnlineMode();
        String str2 = localPluginDescriptionFile.getVersion();
        String str3 = Bukkit.getVersion();
        Player[] players = WPlayerInterface.getOnlinePlayersOld();
        int i = 0;
        if (players != null) {
            i = players.length;
        }



        StringBuilder localStringBuilder = new StringBuilder(1024);
        localStringBuilder.append('{');


        appendJSONPair(localStringBuilder, "guid", this.guid);
        appendJSONPair(localStringBuilder, "plugin_version", str2);
        appendJSONPair(localStringBuilder, "server_version", str3);
        appendJSONPair(localStringBuilder, "players_online", Integer.toString(i));


        String str4 = System.getProperty("os.name");
        String str5 = System.getProperty("os.arch");
        String str6 = System.getProperty("os.version");
        String str7 = System.getProperty("java.version");
        int j = Runtime.getRuntime().availableProcessors();
        if (str5.equals("amd64")) {
            str5 = "x86_64";
        }
        appendJSONPair(localStringBuilder, "osname", str4);
        appendJSONPair(localStringBuilder, "osarch", str5);
        appendJSONPair(localStringBuilder, "osversion", str6);
        appendJSONPair(localStringBuilder, "cores", Integer.toString(j));
        appendJSONPair(localStringBuilder, "auth_mode", bool ? "1" : "0");
        appendJSONPair(localStringBuilder, "java_version", str7);
        if (paramBoolean) {
            appendJSONPair(localStringBuilder, "ping", "1");
        }
        localStringBuilder.append('}');


        URL localURL = new URL("http://report.mcstats.org" + String.format("/plugin/%s", new Object[]{urlEncode(str1)}));
        URLConnection localURLConnection;
        if (isMineshafterPresent()) {
            localURLConnection = localURL.openConnection(Proxy.NO_PROXY);
        } else {
            localURLConnection = localURL.openConnection();
        }
        byte[] arrayOfByte1 = localStringBuilder.toString().getBytes();
        byte[] arrayOfByte2 = gzip(localStringBuilder.toString());


        localURLConnection.addRequestProperty("User-Agent", "MCStats/7");
        localURLConnection.addRequestProperty("Content-Type", "application/json");
        localURLConnection.addRequestProperty("Content-Encoding", "gzip");
        localURLConnection.addRequestProperty("Content-Length", Integer.toString(arrayOfByte2.length));
        localURLConnection.addRequestProperty("Accept", "application/json");
        localURLConnection.addRequestProperty("Connection", "close");

        localURLConnection.setDoOutput(true);
        if (this.debug) {
            System.out.println("[Metrics] Prepared request for " + str1 + " uncompressed=" + arrayOfByte1.length + " compressed=" + arrayOfByte2.length);
        }
        OutputStream localOutputStream = localURLConnection.getOutputStream();
        localOutputStream.write(arrayOfByte2);
        localOutputStream.flush();


        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localURLConnection.getInputStream()));
        String str8 = localBufferedReader.readLine();


        localOutputStream.close();
        localBufferedReader.close();
        if ((str8 == null) || (str8.startsWith("ERR")) || (str8.startsWith("7"))) {
            if (str8 == null) {
                str8 = "null";
            } else if (str8.startsWith("7")) {
                str8 = str8.substring(str8.startsWith("7,") ? 2 : 1);
            }
            throw new IOException(str8);
        }
    }

    public static byte[] gzip(String paramString) {

        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream localGZIPOutputStream = null;
        try {
            localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);
            localGZIPOutputStream.write(paramString.getBytes("UTF-8"));









            return localByteArrayOutputStream.toByteArray();
        } catch (IOException localIOException2) {
            localIOException2.printStackTrace();
        } finally {
            if (localGZIPOutputStream != null) {
                try {
                    localGZIPOutputStream.close();
                } catch (IOException localIOException4) {
                }
            }
        }
        return localByteArrayOutputStream.toByteArray();
    }

    private boolean isMineshafterPresent() {
        try {
            Class.forName("mineshafter.MineServer");
            return true;
        } catch (Exception localException) {
        }
        return false;
    }

    private static void appendJSONPair(StringBuilder paramStringBuilder, String paramString1, String paramString2)
            throws UnsupportedEncodingException {
        int i = 0;
        try {
            if ((paramString2.equals("0")) || (!paramString2.endsWith("0"))) {
                Double.parseDouble(paramString2);
                i = 1;
            }
        } catch (NumberFormatException localNumberFormatException) {
            i = 0;
        }
        if (paramStringBuilder.charAt(paramStringBuilder.length() - 1) != '{') {
            paramStringBuilder.append(',');
        }
        paramStringBuilder.append(escapeJSON(paramString1));
        paramStringBuilder.append(':');
        if (i != 0) {
            paramStringBuilder.append(paramString2);
        } else {
            paramStringBuilder.append(escapeJSON(paramString2));
        }
    }

    private static String escapeJSON(String paramString) {
        StringBuilder localStringBuilder = new StringBuilder();

        localStringBuilder.append('"');
        for (int i = 0; i < paramString.length(); i++) {
            char c = paramString.charAt(i);
            switch (c) {
                case '"':
                case '\\':
                    localStringBuilder.append('\\');
                    localStringBuilder.append(c);
                    break;
                case '\b':
                    localStringBuilder.append("\\b");
                    break;
                case '\t':
                    localStringBuilder.append("\\t");
                    break;
                case '\n':
                    localStringBuilder.append("\\n");
                    break;
                case '\r':
                    localStringBuilder.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        String str = "000" + Integer.toHexString(c);
                        localStringBuilder.append("\\u" + str.substring(str.length() - 4));
                    } else {
                        localStringBuilder.append(c);
                    }
                    break;
            }
        }
        localStringBuilder.append('"');

        return localStringBuilder.toString();
    }

    private static String urlEncode(String paramString)
            throws UnsupportedEncodingException {
        return URLEncoder.encode(paramString, "UTF-8");
    }
}


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.drdanick.McRKit...plugin.MetricsLite
 * JD-Core Version:    0.7.0.1
 */