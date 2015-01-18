 package com.drdanick.McRKit.plugin;

import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.player.WPlayerInterface;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStreamWriter;
 import java.io.UnsupportedEncodingException;
 import java.net.Proxy;
 import java.net.URL;
 import java.net.URLConnection;
 import java.net.URLEncoder;
 import java.util.UUID;
 import java.util.logging.Level;
 import java.util.logging.Logger;
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

 public class BukkitMetricsLite  {
     private static final int REVISION = 6;
     private static final String BASE_URL = "http://mcstats.org";
     private static final String REPORT_URL = "/report/%s";
     private static final int PING_INTERVAL = 10;
     private final Plugin plugin;
     private final YamlConfiguration configuration;
     private final File configurationFile;
     private final String guid;
     private final boolean debug;
     private final Object optOutLock = new Object();
     private volatile BukkitTask task = null;
    
     public BukkitMetricsLite(Plugin paramPlugin)
             throws IOException  {
         if (paramPlugin == null) {
             throw new IllegalArgumentException("Plugin cannot be null");
                    }
         this.plugin = paramPlugin;
        
        
         this.configurationFile = getConfigFile();
         this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
        
        
         this.configuration.addDefault("opt-out", Boolean.valueOf(false));
         this.configuration.addDefault("guid", UUID.randomUUID().toString());
         this.configuration.addDefault("debug", Boolean.valueOf(false));
         if (this.configuration.get("guid", null) == null)  {
             this.configuration.options().header("http://mcstats.org").copyDefaults(true);
             this.configuration.save(this.configurationFile);
                    }
         this.guid = this.configuration.getString("guid");
         this.debug = this.configuration.getBoolean("debug", false);
            }
    
     public boolean start()  {
         synchronized (this.optOutLock)  {
             if (isOptOut()) {
                 return false;
                            }
             if (this.task != null) {
                 return true;
                            }

             this.task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {

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
                        postPlugin(firstPost);;
                    } catch (IOException ioexception) {
                        if (debug) {
                            Bukkit.getLogger().log(Level.INFO, (new StringBuilder()).append("[Metrics] ").append(ioexception.getMessage()).toString());
                        }
                    }
                }
            }, 0L, 12000L);
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
             return true;
                    }
            }  



    public boolean isOptOut()  {
         synchronized (this.optOutLock)  {
             try  {
                 this.configuration.load(getConfigFile());
                            }  catch (IOException localIOException)  {
                 if (this.debug) {
                     Bukkit.getLogger().log(Level.INFO, "[Metrics] " + localIOException.getMessage());
                                    }
                 return true;
                            }  catch (InvalidConfigurationException localInvalidConfigurationException)  {
                 if (this.debug) {
                     Bukkit.getLogger().log(Level.INFO, "[Metrics] " + localInvalidConfigurationException.getMessage());
                                    }
                 return true;
                            }
             return this.configuration.getBoolean("opt-out", false);
                    }
            }
    
     public void enable()
             throws IOException  {
         synchronized (this.optOutLock)  {
             if (isOptOut())  {
                 this.configuration.set("opt-out", Boolean.valueOf(false));
                 this.configuration.save(this.configurationFile);
                            }
             if (this.task == null) {
                 start();
                            }
                    }
            }
    
     public void disable()
             throws IOException  {
         synchronized (this.optOutLock)  {
             if (!isOptOut())  {
                 this.configuration.set("opt-out", Boolean.valueOf(true));
                 this.configuration.save(this.configurationFile);
                            }
             if (this.task != null)  {
                 this.task.cancel();
                 this.task = null;
                            }
                    }
            }
    
     public File getConfigFile()  {
         File localFile = this.plugin.getDataFolder().getParentFile();
        
        
         return new File(new File(localFile, "PluginMetrics"), "config.yml");
            }
    
     private void postPlugin(boolean paramBoolean)
             throws IOException  {
         PluginDescriptionFile localPluginDescriptionFile = this.plugin.getDescription();
         Player[] players = WPlayerInterface.getOnlinePlayersOld();
         String str1 = "RemoteToolkit";
         boolean bool = Bukkit.getServer().getOnlineMode();
         String str2 = localPluginDescriptionFile.getVersion();
         String str3 = Bukkit.getVersion();
         int i = 0;
        if(players!=null)
        {
            i=players.length;
        }
        
        
         StringBuilder localStringBuilder = new StringBuilder();
        
        
         localStringBuilder.append(encode("guid")).append('=').append(encode(this.guid));
         encodeDataPair(localStringBuilder, "version", str2);
         encodeDataPair(localStringBuilder, "server", str3);
         encodeDataPair(localStringBuilder, "players", Integer.toString(i));
         encodeDataPair(localStringBuilder, "revision", String.valueOf(6));
        
        
         String str4 = System.getProperty("os.name");
         String str5 = System.getProperty("os.arch");
         String str6 = System.getProperty("os.version");
         String str7 = System.getProperty("java.version");
         int j = Runtime.getRuntime().availableProcessors();
         if (str5.equals("amd64")) {
             str5 = "x86_64";
                    }
         encodeDataPair(localStringBuilder, "osname", str4);
         encodeDataPair(localStringBuilder, "osarch", str5);
         encodeDataPair(localStringBuilder, "osversion", str6);
         encodeDataPair(localStringBuilder, "cores", Integer.toString(j));
         encodeDataPair(localStringBuilder, "online-mode", Boolean.toString(bool));
         encodeDataPair(localStringBuilder, "java_version", str7);
         if (paramBoolean) {
             encodeDataPair(localStringBuilder, "ping", "true");
                    }
         URL localURL = new URL("http://mcstats.org" + String.format("/report/%s", new Object[]{encode(str1)}));
         URLConnection localURLConnection;
         if (isMineshafterPresent()) {
             localURLConnection = localURL.openConnection(Proxy.NO_PROXY);
                    } else {
             localURLConnection = localURL.openConnection();
                    }
         localURLConnection.setDoOutput(true);
        
        
         OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localURLConnection.getOutputStream());
         localOutputStreamWriter.write(localStringBuilder.toString());
         localOutputStreamWriter.flush();
        
        
         BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localURLConnection.getInputStream()));
         String str8 = localBufferedReader.readLine();
        
        
         localOutputStreamWriter.close();
         localBufferedReader.close();
         if ((str8 == null) || (str8.startsWith("ERR"))) {
             throw new IOException(str8);
                    }
            }
    
     private boolean isMineshafterPresent()  {
         try  {
             Class.forName("mineshafter.MineServer");
             return true;
                    }  catch (Exception localException) {
        }
         return false;
            }
    
     private static void encodeDataPair(StringBuilder paramStringBuilder, String paramString1, String paramString2)
             throws UnsupportedEncodingException  {
         paramStringBuilder.append('&').append(encode(paramString1)).append('=').append(encode(paramString2));
            }
    
     private static String encode(String paramString)
             throws UnsupportedEncodingException  {
         return URLEncoder.encode(paramString, "UTF-8");
            }
     }
/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.drdanick.McRKit...plugin.BukkitMetricsLite
 * JD-Core Version:    0.7.0.1
 */// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BukkitMetricsLite.java