/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.player;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
/**
 *
 * @author ABC
 */
public class PlayerStatistic implements java.io.Serializable {
private String PlayerName;
private int AmountKilledZombies;
private int AmountKilledSkeleton;
private int AmountKilledSpider;
private int AmountKilledCreeper;
private int AmountKilledCaveSpider;
private int AmountKilledPigZombies;
private int AmountKilledSlimes;
private int AmountKilledGhasts;
private int AmountKilledGiants;
private int AmountKilledEnderman;
private int AmountKilledEnderDragon;


    public PlayerStatistic(String PlayerName)
    {
     this.PlayerName = PlayerName;
    }

    public PlayerStatistic(String PlayerName, int amountKilledZombies, int amountKilledSkeleton, int amountKilledSpider, int amountKilledCreeper, int amountOfKilledCaveSpider, int amountOfKilledPigZombie, int amountOfKilledSlime, int amountOfKilledGhast, int amountOfKilledGiant, int amountOfKilledEnderman, int amountOfKilledEnderDragon, int doorsEventZeiger, int buttonsEventZeiger)
    {
     this.PlayerName = PlayerName;
     this.AmountKilledZombies = amountKilledZombies;
     this.AmountKilledSkeleton = amountKilledSkeleton;
     this.AmountKilledSpider = amountKilledSpider;
     this.AmountKilledCreeper = amountKilledCreeper;
     this.AmountKilledCaveSpider = amountOfKilledCaveSpider;
     this.AmountKilledPigZombies = amountOfKilledPigZombie;
     this.AmountKilledSlimes = amountOfKilledSlime;
     this.AmountKilledGhasts = amountOfKilledGhast;
     this.AmountKilledGiants = amountOfKilledGiant;
     this.AmountKilledEnderman = amountOfKilledEnderman;
     this.AmountKilledEnderDragon = amountOfKilledEnderDragon;
    }

    public String getPlayerName()
    {
        if(PlayerName != null)
        {
        return PlayerName;
        } else {
        return null;
        }
    }

    public Player getPlayer()
    {
        if(PlayerName != null)
        {
            return Bukkit.getServer().getPlayer(PlayerName);
        } else {
            return null;
        }
    }

    public OfflinePlayer getOfflinePlayer()
    {
        if(PlayerName != null)
        {
            return Bukkit.getServer().getOfflinePlayer(PlayerName);
        } else {
            return null;
        }
    }

    public int getAmountOfKilledZombies()
    {
        return AmountKilledZombies;
    }

    public int getAmountOfKilledSkeleton()
    {
        return AmountKilledSkeleton;
    }

    public int getAmountOfKilledSpider()
    {
        return AmountKilledSpider;
    }

    public int getAmountOfKilledCreeper()
    {
        return AmountKilledCreeper;
    }

    public int getAmountOfKilledCaveSpider()
    {
        return AmountKilledCaveSpider;
    }

    public int getAmountOfKilledPigZombies()
    {
        return AmountKilledPigZombies;
    }

    public int getAmountOfKilledSlimes()
    {
        return AmountKilledSlimes;
    }

    public int getAmountOfKilledGhasts()
    {
        return AmountKilledGhasts;
    }

    public int getAmountOfKilledGiants()
    {
        return AmountKilledGiants;
    }

    public int getAmountOfKilledEnderman()
    {
        return AmountKilledEnderman;
    }

    public int getAmountOfKilledEnderDragon()
    {
        return AmountKilledEnderDragon;
    }

    public void resetAmountOfKilledMonster()
    {
        this.AmountKilledZombies = 0;
        this.AmountKilledSkeleton = 0;
        this.AmountKilledSpider = 0;
        this.AmountKilledCreeper = 0;
        this.AmountKilledSlimes = 0;
        this.AmountKilledGhasts = 0;
        this.AmountKilledGiants = 0;
        this.AmountKilledEnderman = 0;
        this.AmountKilledEnderDragon = 0;
    }

    public void setAmountOfKilledZombies(int amount)
    {
        AmountKilledZombies = amount;
    }

    public void setAmountOfKilledSkeleton(int amount)
    {
        AmountKilledSkeleton = amount;
    }

    public void setAmountOfKilledSpider(int amount)
    {
        AmountKilledSpider = amount;
    }

    public void setAmountOfKilledCreeper(int amount)
    {
        AmountKilledCreeper = amount;
    }

    public void setAmountOfKilledCaveSpider(int amount)
    {
        AmountKilledCaveSpider = amount;
    }

    public void setAmountOfKilledPigZombies(int amount)
    {
        AmountKilledPigZombies = amount;
    }

    public void setAmountOfKilledSlimes(int amount)
    {
        AmountKilledSlimes = amount;
    }

    public void setAmountOfKilledGhasts(int amount)
    {
        AmountKilledGhasts = amount;
    }

    public void setAmountOfKilledGiants(int amount)
    {
        AmountKilledGiants = amount;
    }

    public void setAmountOfKilledEnderman(int amount)
    {
        AmountKilledEnderman = amount;
    }

    public void setAmountOfKilledEnderDragon(int amount)
    {
        AmountKilledEnderDragon = amount;
    }
    
  

    public void addAmountOfKilledZombies()
    {
        AmountKilledZombies++;
    }

    public void addAmountOfKilledSkeleton()
    {
        AmountKilledSkeleton++;
    }

    public void addAmountOfKilledSpider()
    {
        AmountKilledSpider++;
    }

    public void addAmountOfKilledCreeper()
    {
        AmountKilledCreeper++;
    }

    public void addAmountOfKilledCaveSpider()
    {
        AmountKilledCaveSpider++;
    }

    public void addAmountOfKilledPigZombies()
    {
        AmountKilledPigZombies++;
    }

    public void addAmountOfKilledSlimes()
    {
        AmountKilledSlimes++;
    }

    public void addAmountOfKilledGhasts()
    {
        AmountKilledGhasts++;
    }

    public void addAmountOfKilledGiants()
    {
        AmountKilledGiants++;
    }

    public void addAmountOfKilledEnderman()
    {
        AmountKilledEnderman++;
    }

    public void addAmountOfKilledEnderDragon()
    {
        AmountKilledEnderDragon++;
    }


    public void subAmountOfKilledZombies()
    {
        AmountKilledZombies--;
    }

    public void subAmountOfKilledSkeleton()
    {
        AmountKilledSkeleton--;
    }

    public void subAmountOfKilledSpider()
    {
        AmountKilledSpider--;
    }

    public void subAmountOfKilledCreeper()
    {
        AmountKilledCreeper--;
    }

    public void subAmountOfKilledCaveSpider()
    {
        AmountKilledCaveSpider--;
    }

    public void subAmountOfKilledPigZombies()
    {
        AmountKilledPigZombies--;
    }

    public void subAmountOfKilledSlimes()
    {
        AmountKilledSlimes--;
    }

    public void subAmountOfKilledGhasts()
    {
        AmountKilledGhasts--;
    }

    public void subAmountOfKilledGiants()
    {
        AmountKilledGiants--;
    }

    public void subAmountOfKilledEnderman()
    {
        AmountKilledEnderman--;
    }

    public void subAmountOfKilledEnderDragon()
    {
        AmountKilledEnderDragon--;
    }


    @Override
    public String toString()
    {
        if(PlayerName != null)
        {
            

        return PlayerName + ":" + this.AmountKilledZombies + ":" + this.AmountKilledSkeleton + ":" + this.AmountKilledSpider + ":" + this.AmountKilledCreeper;
        } else {
            return null;
        }
    }
}
