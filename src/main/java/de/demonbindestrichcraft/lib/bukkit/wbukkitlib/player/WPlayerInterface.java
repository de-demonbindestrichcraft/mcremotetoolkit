/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.player;

import com.evilmidget38.UUIDFetcher;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.files.ConcurrentConfig;
import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.common.sql.SqlInterface;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author ABC
 */
public class WPlayerInterface {

    private static ConcurrentConfig configPNAME_UUID = new ConcurrentConfig(new File("WBukkitLibPNAME_UUID"));
    private static ConcurrentConfig configUUID_PNAME = new ConcurrentConfig(new File("WBukkitLibUUID_PNAME"));
    private static SqlInterface sqlInterfacePNAME_UUID = configPNAME_UUID.getNewSqlInterface();
    private static SqlInterface sqlInterfaceUUID_PNAME = configUUID_PNAME.getNewSqlInterface();

    public static Plugin getThisPlugin(CommandSender commandServer) {
        Plugin[] plugins = commandServer.getServer().getPluginManager().getPlugins();
        return plugins[0];
    }

    public static Player[] getOnlinePlayersOld() {
        Server server = Bukkit.getServer();
        Object obj = server.getOnlinePlayers();
        try {
            if ((obj instanceof Collection)) {
                Collection<? extends Player> onlinePlayers = (Collection<? extends Player>) obj;
                int length = onlinePlayers.size();
                Player[] players = new Player[length];
                Player player = null;
                Iterator iterator = onlinePlayers.iterator();
                for (int i = 0; iterator.hasNext(); i++) {
                    player = (Player) iterator.next();
                    if (!(player instanceof Player)) {
                        players[i] = null;
                        continue;
                    }
                    players[i] = player;
                }
                return players;
            } else if (obj instanceof Player[]) {
                return (Player[]) obj;
            }
        } catch (Throwable ex) {
            return null;
        }
        return null;
    }

    public static Player[] getOnlinePlayers() {
        Player[] players = getOnlinePlayersOld();
        if (players == null) {
            return null;
        }
        int length = players.length;
        Player[] onlinePlayersPPP = new PPPlayer[length];
        for (int i = 0; i < length; i++) {
            onlinePlayersPPP[i] = getPPPlayer(players[i]);
        }
        return onlinePlayersPPP;
    }

    public static Player getOnlinePlayerOld(String name) {
        Player[] players = WPlayerInterface.getOnlinePlayersOld();
        if (players == null) {
            try {
                Player player = Bukkit.getServer().getPlayer(name);
                if (player == null) {
                    return null;
                }
                return player;
            } catch (Throwable ex) {
                return null;
            }
        }
        int length = players.length;
        for (int i = 0; i < length; i++) {
            if (!(players[i] instanceof Player)) {
                continue;
            }
            if (players[i].getName().equals(name) || players[i].getName().equalsIgnoreCase(name) || players[i].getName().toLowerCase().startsWith(name.toLowerCase())) {
                return players[i];
            }
        }
        try {
            Player player = Bukkit.getServer().getPlayer(name);
            if (player == null) {
                return null;
            }
            return player;
        } catch (Throwable ex) {
            return null;
        }

    }

    public static Player getOnlinePlayer(Plugin plugin, String name) {
        Player player = getOnlinePlayerOld(name);
        if (player == null) {
            return null;
        }
        return getPPPlayer(player);
    }

    public static Player getOnlinePlayer(String name) {
        Player player = getPlayerFromName(name);
        if (player == null) {
            return null;
        }
        return getPPPlayer(player);
    }

    public static Player getOnlinePlayerExactOld(Plugin plugin, String name) {
        Player[] players = WPlayerInterface.getOnlinePlayers();
        if (players == null) {
            return null;
        }
        int length = players.length;
        for (int i = 0; i < length; i++) {
            if (!(players[i] instanceof Player)) {
                continue;
            }
            if (players[i].getName().equals(name)) {
                return players[i];
            }
        }
        return null;
    }

    public static Player getOnlinePlayerExact(Plugin plugin, String name) {
        Player player = getOnlinePlayerExactOld(null, name);
        return getPPPlayer(player);
    }

    public static Player getPlayerOld(Player player) {
        if (!(player instanceof Player)) {
            throw new NullPointerException("player is not Instance of Player!");
        }
        return player;
    }

    public static Player getPPPlayer(Player player) {
        if (!(player instanceof Player)) {
            return null;
        }
        return new PPPlayer(player);
    }

    public static Player getPlayer(Player player) {
        return getPPPlayer(player);
    }

    public static Server getServer(Plugin plugin) {
        if (plugin == null) {
            throw new NullPointerException("Plugin is null!");
        }
        Server server = plugin.getServer();
        if (server == null) {
            throw new NullPointerException("Server is null!");
        }
        return server;
    }

    public static Player getPlayerFromName(String name) {
        try {
            Map<String, String> copyOfPropertiesPNAME_UUID = configPNAME_UUID.getCopiedOfProperties();
            Map<String, String> copyOfPropertiesUUID_PNAME = configUUID_PNAME.getCopiedOfProperties();
            UUID playerUUID = UUIDFetcher.getUUIDOf(name);
            String theUUID = playerUUID.toString();
            if (!copyOfPropertiesPNAME_UUID.containsKey(name) && !copyOfPropertiesUUID_PNAME.containsKey(theUUID)) {
                return setPlayerFromName(copyOfPropertiesPNAME_UUID, copyOfPropertiesUUID_PNAME, name, playerUUID);
            } else if (!copyOfPropertiesPNAME_UUID.containsKey(name)) {

                return setPlayerFromName(copyOfPropertiesPNAME_UUID, name, playerUUID);
            } else if (!copyOfPropertiesUUID_PNAME.containsKey(theUUID)) {
                return setPlayerFromName(copyOfPropertiesUUID_PNAME, playerUUID, name);
            } else {
                String string = configPNAME_UUID.getString(name);
                if (!string.equals(theUUID)) {
                    String nName = configUUID_PNAME.getString(theUUID);
                    if (nName.equals(name)) {
                        nName = "FakE" + nName;
                        return setPlayerFromName(copyOfPropertiesPNAME_UUID, copyOfPropertiesUUID_PNAME, nName, playerUUID);
                    }
                }
                return getPlayerOld(Bukkit.getPlayer(playerUUID));
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getPlayerName(String name) {
        try {
            Player player = getPlayerFromName(name);
            if (!(player instanceof Player)) {
                return "";
            }
            String theUUID = player.getUniqueId().toString();
            return configUUID_PNAME.getString(theUUID);
        } catch (Exception ex) {
            return "";
        }
    }

    private static Player setPlayerFromName(Map<String, String> copyOfPropertiesPNAME_UUID, Map<String, String> copyOfPropertiesUUID_PNAME, String name, UUID playerUUID) {
        try {
            Player player = Bukkit.getPlayer(playerUUID);
            if (!(player instanceof Player)) {
                return null;
            }
            String uuid = playerUUID.toString();
            copyOfPropertiesPNAME_UUID.put(name, uuid);
            copyOfPropertiesUUID_PNAME.put(uuid, name);
            configPNAME_UUID.update(copyOfPropertiesPNAME_UUID);
            configUUID_PNAME.update(copyOfPropertiesUUID_PNAME);
            configPNAME_UUID.saveToDb(sqlInterfacePNAME_UUID, "WBukkitLibPNAME_UUID", "PNAME", "UUID");
            configUUID_PNAME.saveToDb(sqlInterfaceUUID_PNAME, "WBukkitLibUUID_PNAME", "UUID", "PNAME");
            return player;
        } catch (Exception ex) {
            return null;
        }
    }

    private static Player setPlayerFromName(Map<String, String> copyOfPropertiesPNAME_UUID, String name, UUID playerUUID) {
        try {
            Player player = Bukkit.getPlayer(playerUUID);
            if (!(player instanceof Player)) {
                return null;
            }
            String uuid = playerUUID.toString();
            copyOfPropertiesPNAME_UUID.put(name, uuid);
            configPNAME_UUID.update(copyOfPropertiesPNAME_UUID);
            configPNAME_UUID.saveToDb(sqlInterfacePNAME_UUID, "WBukkitLibPNAME_UUID", "PNAME", "UUID");
            return player;
        } catch (Exception ex) {
            return null;
        }
    }

    private static Player setPlayerFromName(Map<String, String> copyOfPropertiesUUID_PNAME, UUID playerUUID, String name) {
        try {
            String uuid = playerUUID.toString();
            Player player = Bukkit.getPlayer(playerUUID);
            if (!(player instanceof Player)) {
                return null;
            }
            copyOfPropertiesUUID_PNAME.put(uuid, name);
            configUUID_PNAME.saveToDb(sqlInterfaceUUID_PNAME, "WBukkitLibUUID_PNAME", "UUID", "PNAME");
            return player;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean isPlayerNameInWbukkitLibPNAME_UUIDList(String name) {
        return configPNAME_UUID.getCopiedOfProperties().containsKey(name);
    }

    public static boolean isPlayerUUIDInWbukkitLibUUID_PNAMEList(String uuid) {
        return configUUID_PNAME.getCopiedOfProperties().containsKey(uuid);
    }
}
