package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.player;


import de.demonbindestrichcraft.lib.bukkit.wbukkitlib.player.permissions.PermManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ABC
 */
public class PPPlayer extends PPlayer {
    private PermManager permManager;
    public PPPlayer(Player player)
    {
        super(player);
        try {
            permManager = new PermManager();
        } catch (Exception ex) {
            Logger.getLogger(PPPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public boolean hasPermission(String perm)
    {
        Permission permission = permManager.getPermission();
        if(permission == null)
            Logger.getLogger("Minecraft.Werri").log(Level.SEVERE, "is not a Instance Perm!");
        return permManager.getPermission().has(this.getPlayer(), perm);
    }
}
