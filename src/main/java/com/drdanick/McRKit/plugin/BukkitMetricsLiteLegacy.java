// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BukkitMetricsLiteLegacy.java

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
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
import java.util.LinkedHashSet;
 import java.util.Set;
 import java.util.UUID;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 import org.bukkit.Bukkit;
 import org.bukkit.Server;
 import org.bukkit.configuration.InvalidConfigurationException;
 import org.bukkit.configuration.file.YamlConfiguration;
 import org.bukkit.configuration.file.YamlConfigurationOptions;
 import org.bukkit.plugin.Plugin;
 import org.bukkit.plugin.PluginDescriptionFile;
 import org.bukkit.scheduler.BukkitScheduler;

 public class BukkitMetricsLiteLegacy  {
     private static final int REVISION = 5;
     private static final String BASE_URL = "http://mcstats.org";
     private static final String REPORT_URL = "/report/%s";
     private static final String CONFIG_FILE = "plugins/PluginMetrics/config.yml";
     private static final String CUSTOM_DATA_SEPARATOR = "~~";
     private static final int PING_INTERVAL = 10;
     private final Plugin plugin;
     private final Set graphs = Collections.synchronizedSet(new HashSet());
     private final Graph defaultGraph = new Graph("Default");
     private final YamlConfiguration configuration;
     private final File configurationFile;
     private final String guid;
     private final Object optOutLock = new Object();
     private volatile int taskId = -1;
    

    public static abstract class Plotter {

        public abstract int getValue();

        public String getColumnName() {
            return name;
        }

        public void reset() {
        }

        public int hashCode() {
            return getColumnName().hashCode() + getValue();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Plotter)) {
                return false;
            } else {
                Plotter plotter = (Plotter) obj;
                return plotter.name.equals(name) && plotter.getValue() == getValue();
            }
        }
        private final String name;

        public Plotter() {
            this("Default");
        }

        public Plotter(String s) {
            name = s;
        }
    }

    public static class Graph {

        public String getName() {
            return name;
        }

        public void addPlotter(Plotter plotter) {
            plotters.add(plotter);
        }

        public void removePlotter(Plotter plotter) {
            plotters.remove(plotter);
        }

        public Set getPlotters() {
            return Collections.unmodifiableSet(plotters);
        }

        public int hashCode() {
            return name.hashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Graph)) {
                return false;
            } else {
                Graph graph = (Graph) obj;
                return graph.name.equals(name);
            }
        }
        private final String name;
        private final Set plotters;

        private Graph(String s) {
            plotters = new LinkedHashSet();
            name = s;
        }
    }

     public BukkitMetricsLiteLegacy(Plugin paramPlugin)
             throws IOException  {
         if (paramPlugin == null) {
             throw new IllegalArgumentException("Plugin cannot be null");
                    }
         this.plugin = paramPlugin;
        
        
         this.configurationFile = new File("plugins/PluginMetrics/config.yml");
         this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
        
        
         this.configuration.addDefault("opt-out", Boolean.valueOf(false));
         this.configuration.addDefault("guid", UUID.randomUUID().toString());
         if (this.configuration.get("guid", null) == null)  {
             this.configuration.options().header("http://mcstats.org").copyDefaults(true);
             this.configuration.save(this.configurationFile);
                    }
         this.guid = this.configuration.getString("guid");
            }
    
     public Graph createGraph(String paramString)  {
         if (paramString == null) {
             throw new IllegalArgumentException("Graph name cannot be null");
                    }
         Graph localGraph = new Graph(paramString);
        
        
         this.graphs.add(localGraph);
        
        
         return localGraph;
            }
    
     public void addCustomData(BukkitMetricsLiteLegacy.Plotter paramPlotter)  {
         if (paramPlotter == null) {
             throw new IllegalArgumentException("Plotter cannot be null");
                    }
         this.defaultGraph.addPlotter(paramPlotter);
        
        
         this.graphs.add(this.defaultGraph);
            }

    
     public boolean start()  {
         synchronized (this.optOutLock)  {
             if (isOptOut()) {
                 return false;
                            }
             if (this.taskId >= 0) {
                 return true;
                            }
            taskId = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {

                private boolean firstPost = false;

                public void run() {
                    try {
                        synchronized (optOutLock) {
                            if (isOptOut() && taskId > 0) {
                                plugin.getServer().getScheduler().cancelTask(taskId);
                                taskId = -1;
                            }
                        }
                        firstPost = !firstPost;
                        postPlugin(firstPost);
                    } catch (IOException ioexception) {
                        Bukkit.getLogger().log(Level.INFO, (new StringBuilder()).append("[Metrics] ").append(ioexception.getMessage()).toString());
                    }
                }
            }, 0L, 12000L);
        }
        return true;
    }

     public boolean isOptOut()  {
         synchronized (this.optOutLock)  {
             try  {
                 this.configuration.load("plugins/PluginMetrics/config.yml");
                            }  catch (IOException localIOException)  {
                 Bukkit.getLogger().log(Level.INFO, "[Metrics] " + localIOException.getMessage());
                 return true;
                            }  catch (InvalidConfigurationException localInvalidConfigurationException)  {
                 Bukkit.getLogger().log(Level.INFO, "[Metrics] " + localInvalidConfigurationException.getMessage());
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
             if (this.taskId < 0) {
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
             if (this.taskId > 0)  {
                 this.plugin.getServer().getScheduler().cancelTask(this.taskId);
                 this.taskId = -1;
                            }
                    }
            }
    
   private void postPlugin(boolean flag)
        throws IOException
    {
        PluginDescriptionFile plugindescriptionfile = plugin.getDescription();
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(encode("guid")).append('=').append(encode(guid));
        encodeDataPair(stringbuilder, "version", plugindescriptionfile.getVersion());
        encodeDataPair(stringbuilder, "server", Bukkit.getVersion());
        encodeDataPair(stringbuilder, "players", Integer.toString(WPlayerInterface.getOnlinePlayersOld().length));
        encodeDataPair(stringbuilder, "revision", String.valueOf(5));
        if(flag)
            encodeDataPair(stringbuilder, "ping", "true");
        synchronized(graphs)
        {
            for(Iterator iterator = graphs.iterator(); iterator.hasNext();)
            {
                Graph graph = (Graph)iterator.next();
                Iterator iterator1 = graph.getPlotters().iterator();
                while(iterator1.hasNext()) 
                {
                    Plotter plotter = (Plotter)iterator1.next();
                    String s1 = String.format("C%s%s%s%s", new Object[] {
                        "~~", graph.getName(), "~~", plotter.getColumnName()
                    });
                    String s2 = Integer.toString(plotter.getValue());
                    encodeDataPair(stringbuilder, s1, s2);
                }
            }

        }
        URL url = new URL((new StringBuilder()).append("http://mcstats.org").append(String.format("/report/%s", new Object[] {
            encode("RemoteToolkit")
        })).toString());
        URLConnection urlconnection;
        if(isMineshafterPresent())
            urlconnection = url.openConnection(Proxy.NO_PROXY);
        else
            urlconnection = url.openConnection();
        urlconnection.setDoOutput(true);
        OutputStreamWriter outputstreamwriter = new OutputStreamWriter(urlconnection.getOutputStream());
        outputstreamwriter.write(stringbuilder.toString());
        outputstreamwriter.flush();
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
        String s = bufferedreader.readLine();
        outputstreamwriter.close();
        bufferedreader.close();
        if(s == null || s.startsWith("ERR"))
            throw new IOException(s);
        if(s.contains("OK This is your first update this hour"))
            synchronized(graphs)
            {
                for(Iterator iterator2 = graphs.iterator(); iterator2.hasNext();)
                {
                    Graph graph1 = (Graph)iterator2.next();
                    Iterator iterator3 = graph1.getPlotters().iterator();
                    while(iterator3.hasNext()) 
                    {
                        Plotter plotter1 = (Plotter)iterator3.next();
                        plotter1.reset();
                    }
                }

            }
    }

    private boolean isMineshafterPresent()
    {
        try
        {
            Class.forName("mineshafter.MineServer");
            return true;
        }
        catch(Exception exception)
        {
            return false;
        }
    }

    private static void encodeDataPair(StringBuilder stringbuilder, String s, String s1)
        throws UnsupportedEncodingException
    {
        stringbuilder.append('&').append(encode(s)).append('=').append(encode(s1));
    }

    private static String encode(String s)
        throws UnsupportedEncodingException
    {
        return URLEncoder.encode(s, "UTF-8");
    }
}