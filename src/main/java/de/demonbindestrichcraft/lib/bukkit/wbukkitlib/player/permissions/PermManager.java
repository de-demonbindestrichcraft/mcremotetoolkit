package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.player.permissions;


import net.milkbowl.vault.permission.Permission;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ABC
 */
public class PermManager {
    private static Permission permission = null;
    private static Server server = null;
    public PermManager() throws Exception
    {
     if(server == null)
         throw new Exception();
     if(!setupPermissions())
         throw new Exception();
    }
    
    private static boolean setupPermissions()
    {
        if(permission != null)
            return true;
        RegisteredServiceProvider<Permission> permissionProvider = server.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
    
    public Permission getPermission()
    {
        return permission;
    }
    
    public static Server getServer()
    {
        return server;
    }
    
    public static void setServer(Server bukkitServer) throws Exception
    {
        server = bukkitServer;
    }
}
