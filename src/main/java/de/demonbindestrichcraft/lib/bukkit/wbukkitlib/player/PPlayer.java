package de.demonbindestrichcraft.lib.bukkit.wbukkitlib.player;


import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ABC
 */
public class PPlayer implements Player {

    private Player cPlayer;
    public PPlayer(Player player)
    {
       try {
        cPlayer = player.getPlayer();
        } catch (ClassCastException ex) {
            player.sendMessage("Argument of PPlayer Konstrukter is not a instance of Player!");
        }
    }
    
    @Override
    public String getDisplayName() {
        return cPlayer.getDisplayName();
    }

    @Override
    public void setDisplayName(String string) {
        cPlayer.setDisplayName(string);
    }

    @Override
    public String getPlayerListName() {
        return cPlayer.getPlayerListName();
    }

    @Override
    public void setPlayerListName(String string) {
        cPlayer.setPlayerListName(string);
    }

    @Override
    public void setCompassTarget(Location lctn) {
        cPlayer.setCompassTarget(lctn);
    }

    @Override
    public Location getCompassTarget() {
        return cPlayer.getCompassTarget();
    }

    @Override
    public InetSocketAddress getAddress() {
        return cPlayer.getAddress();
    }

    @Override
    public void sendRawMessage(String string) {
        cPlayer.sendRawMessage(string);
    }

    @Override
    public void kickPlayer(String string) {
        cPlayer.kickPlayer(string);
    }

    @Override
    public void chat(String string) {
        cPlayer.chat(string);
    }

    @Override
    public boolean performCommand(String string) {
        return cPlayer.performCommand(string);
    }

    @Override
    public boolean isSneaking() {
        return cPlayer.isSneaking();
    }

    @Override
    public void setSneaking(boolean bln) {
        cPlayer.setSneaking(bln);
    }

    @Override
    public boolean isSprinting() {
        return cPlayer.isSprinting();
    }

    @Override
    public void setSprinting(boolean bln) {
       cPlayer.setSprinting(bln);
    }

    @Override
    public void saveData() {
        cPlayer.saveData();
    }

    @Override
    public void loadData() {
        cPlayer.loadData();
    }

    @Override
    public void setSleepingIgnored(boolean bln) {
        cPlayer.setSleepingIgnored(bln);
    }

    @Override
    public boolean isSleepingIgnored() {
        return cPlayer.isSleepingIgnored();
    }

    @Override
    public void playNote(Location lctn, byte b, byte b1) {
        cPlayer.playNote(lctn, b, b1);
    }

    @Override
    public void playNote(Location lctn, Instrument i, Note note) {
        cPlayer.playNote(lctn, i, note);
    }

    @Override
    public void playSound(Location lctn, Sound sound, float f, float f1) {
        cPlayer.playSound(lctn, sound, f1, f1);
    }

    @Override
    public void playEffect(Location lctn, Effect effect, int i) {
        cPlayer.playEffect(lctn, effect, i);
    }

    @Override
    public <T> void playEffect(Location lctn, Effect effect, T t) {
        cPlayer.playEffect(lctn, effect, t);
    }

    @Override
    public void sendBlockChange(Location lctn, Material mtrl, byte b) {
        cPlayer.sendBlockChange(lctn, mtrl, b);
    }

    @Override
    public boolean sendChunkChange(Location lctn, int i, int i1, int i2, byte[] bytes) {
        return cPlayer.sendChunkChange(lctn, i2, i2, i2, bytes);
    }

    @Override
    public void sendBlockChange(Location lctn, int i, byte b) {
        cPlayer.sendBlockChange(lctn, i, b);
    }

    @Override
    public void sendMap(MapView mv) {
        cPlayer.sendMap(mv);
    }

    @Override
    public void updateInventory() {
        cPlayer.updateInventory();
    }

    @Override
    public void awardAchievement(Achievement a) {
        cPlayer.awardAchievement(a);
    }

    @Override
    public void incrementStatistic(Statistic ststc) {
        cPlayer.incrementStatistic(ststc);
    }

    @Override
    public void incrementStatistic(Statistic ststc, int i) {
        cPlayer.incrementStatistic(ststc, i);
    }

    @Override
    public void incrementStatistic(Statistic ststc, Material mtrl) {
        cPlayer.incrementStatistic(ststc, mtrl);
    }

    @Override
    public void incrementStatistic(Statistic ststc, Material mtrl, int i) {
        cPlayer.incrementStatistic(ststc, mtrl, i);
    }

    @Override
    public void setPlayerTime(long l, boolean bln) {
        cPlayer.setPlayerTime(l, bln);
    }

    @Override
    public long getPlayerTime() {
        return cPlayer.getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset() {
        return cPlayer.getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return cPlayer.isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime() {
        cPlayer.resetPlayerTime();
    }

    @Override
    public void giveExp(int i) {
        cPlayer.giveExp(i);
    }

    @Override
    public void giveExpLevels(int i) {
        cPlayer.giveExpLevels(i);
    }

    @Override
    public float getExp() {
        return cPlayer.getExp();
    }

    @Override
    public void setExp(float f) {
        cPlayer.setExp(f);
    }

    @Override
    public int getLevel() {
        return cPlayer.getLevel();
    }

    @Override
    public void setLevel(int i) {
        cPlayer.setLevel(i);
    }

    @Override
    public int getTotalExperience() {
        return cPlayer.getTotalExperience();
    }

    @Override
    public void setTotalExperience(int i) {
        cPlayer.setTotalExperience(i);
    }

    @Override
    public float getExhaustion() {
        return cPlayer.getExhaustion();
    }

    @Override
    public void setExhaustion(float f) {
        cPlayer.setExhaustion(f);
    }

    @Override
    public float getSaturation() {
        return cPlayer.getSaturation();
    }

    @Override
    public void setSaturation(float f) {
       cPlayer.setSaturation(f);
    }

    @Override
    public int getFoodLevel() {
        return cPlayer.getFoodLevel();
    }

    @Override
    public void setFoodLevel(int i) {
        cPlayer.setFoodLevel(i);
    }

    @Override
    public Location getBedSpawnLocation() {
        return cPlayer.getBedSpawnLocation();
    }

    @Override
    public void setBedSpawnLocation(Location lctn) {
        cPlayer.setBedSpawnLocation(lctn);
    }

    @Override
    public void setBedSpawnLocation(Location lctn, boolean bln) {
        cPlayer.setBedSpawnLocation(lctn, bln);
    }

    @Override
    public boolean getAllowFlight() {
        return cPlayer.getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean bln) {
        cPlayer.setAllowFlight(bln);
    }

    @Override
    public void hidePlayer(Player player) {
        cPlayer.hidePlayer(player);
    }

    @Override
    public void showPlayer(Player player) {
        cPlayer.showPlayer(player);
    }

    @Override
    public boolean canSee(Player player) {
        return cPlayer.canSee(player);
    }

    @Override
    public boolean isFlying() {
        return cPlayer.isFlying();
    }

    @Override
    public void setFlying(boolean bln) {
        cPlayer.setFlying(bln);
    }

    @Override
    public void setFlySpeed(float f) throws IllegalArgumentException {
        cPlayer.setFlySpeed(f);
    }

    @Override
    public void setWalkSpeed(float f) throws IllegalArgumentException {
        cPlayer.setWalkSpeed(f);
    }

    @Override
    public float getFlySpeed() {
        return cPlayer.getFlySpeed();
    }

    @Override
    public float getWalkSpeed() {
        return cPlayer.getWalkSpeed();
    }

    @Override
    public String getName() {
        return cPlayer.getName();
    }

    @Override
    public PlayerInventory getInventory() {
        return cPlayer.getInventory();
    }

    @Override
    public Inventory getEnderChest() {
        return cPlayer.getEnderChest();
    }

    @Override
    public boolean setWindowProperty(Property prprt, int i) {
        return cPlayer.setWindowProperty(prprt, i);
    }

    @Override
    public InventoryView getOpenInventory() {
        return cPlayer.getOpenInventory();
    }

    @Override
    public InventoryView openInventory(Inventory invntr) {
        return cPlayer.openInventory(invntr);
    }

    @Override
    public InventoryView openWorkbench(Location lctn, boolean bln) {
        return cPlayer.openWorkbench(lctn, bln);
    }

    @Override
    public InventoryView openEnchanting(Location lctn, boolean bln) {
        return cPlayer.openEnchanting(lctn, bln);
    }

    @Override
    public void openInventory(InventoryView iv) {
        cPlayer.openInventory(iv);
    }

    @Override
    public void closeInventory() {
        cPlayer.closeInventory();
    }

    @Override
    public ItemStack getItemInHand() {
        return cPlayer.getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack is) {
        cPlayer.setItemInHand(is);
    }

    @Override
    public ItemStack getItemOnCursor() {
        return cPlayer.getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(ItemStack is) {
        cPlayer.setItemOnCursor(is);
    }
        
    @Override
    public boolean isSleeping() {
        return cPlayer.isSleeping();
    }

    @Override
    public int getSleepTicks() {
        return cPlayer.getSleepTicks();
    }

    @Override
    public GameMode getGameMode() {
        return cPlayer.getGameMode();
    }

    @Override
    public void setGameMode(GameMode gm) {
        cPlayer.setGameMode(gm);
    }

    @Override
    public boolean isBlocking() {
        return cPlayer.isBlocking();
    }

    @Override
    public int getExpToLevel() {
        return cPlayer.getExpToLevel();
    }

    @Override
    public double getEyeHeight() {
        return cPlayer.getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean bln) {
        return cPlayer.getEyeHeight(bln);
    }

    @Override
    public Location getEyeLocation() {
        return cPlayer.getEyeLocation();
    }

    @Override
    public List<Block> getLineOfSight(HashSet<Byte> hs, int i) {
        return cPlayer.getLineOfSight(hs, i);
    }

    @Override
    public Block getTargetBlock(HashSet<Byte> hs, int i) {
        return cPlayer.getTargetBlock(hs, i);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hs, int i) {
        return cPlayer.getLastTwoTargetBlocks(hs, i);
    }

    @Deprecated
    @Override
    public Egg throwEgg() {
        return cPlayer.throwEgg();
    }

    @Deprecated
    @Override
    public Snowball throwSnowball() {
        return cPlayer.throwSnowball();
    }

    @Deprecated
    @Override
    public Arrow shootArrow() {
        return cPlayer.shootArrow();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> type) {
        return cPlayer.launchProjectile(type);
    }

    @Override
    public int getRemainingAir() {
        return cPlayer.getRemainingAir();
    }

    @Override
    public void setRemainingAir(int i) {
        cPlayer.setRemainingAir(i);
    }

    @Override
    public int getMaximumAir() {
       return cPlayer.getMaximumAir();
    }

    @Override
    public void setMaximumAir(int i) {
        cPlayer.setMaximumAir(i);
    }

    @Override
    public int getMaximumNoDamageTicks() {
       return cPlayer.getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int i) {
        cPlayer.setMaximumNoDamageTicks(i);         
    }

    @Override
    public int getNoDamageTicks() {
        return cPlayer.getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int i) {
        cPlayer.setNoDamageTicks(i);
    }

    @Override
    public Player getKiller() {
        return cPlayer.getKiller();
    }

    @Override
    public boolean addPotionEffect(PotionEffect pe) {
        return cPlayer.addPotionEffect(pe);
    }

    @Override
    public boolean addPotionEffect(PotionEffect pe, boolean bln) {
        return cPlayer.addPotionEffect(pe, bln);
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> clctn) {
        return cPlayer.addPotionEffects(clctn);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType pet) {
        return cPlayer.hasPotionEffect(pet);
    }

    @Override
    public void removePotionEffect(PotionEffectType pet) {
        cPlayer.removePotionEffect(pet);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return cPlayer.getActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return cPlayer.hasLineOfSight(entity);
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return cPlayer.getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(boolean bln) {
        cPlayer.setRemoveWhenFarAway(bln);
    }

    @Override
    public EntityEquipment getEquipment() {
        return cPlayer.getEquipment();
    }

    @Override
    public void setCanPickupItems(boolean bln) {
        cPlayer.setCanPickupItems(bln);
    }

    @Override
    public boolean getCanPickupItems() {
        return cPlayer.getCanPickupItems();
    }

    @Override
    public Location getLocation() {
        return cPlayer.getLocation();
    }

    @Override
    public Location getLocation(Location lctn) {
        return cPlayer.getLocation(lctn);
    }

    @Override
    public void setVelocity(Vector vector) {
        cPlayer.setVelocity(vector);
    }

    @Override
    public Vector getVelocity() {
        return cPlayer.getVelocity();
    }

    @Override
    public World getWorld() {
        return cPlayer.getWorld();
    }

    @Override
    public boolean teleport(Location lctn) {
        return cPlayer.teleport(lctn);
    }

    @Override
    public boolean teleport(Location lctn, TeleportCause tc) {
        return cPlayer.teleport(lctn, tc);
    }

    @Override
    public boolean teleport(Entity entity) {
        return cPlayer.teleport(entity);
    }

    @Override
    public boolean teleport(Entity entity, TeleportCause tc) {
        return cPlayer.teleport(entity, tc);
    }

    @Override
    public List<Entity> getNearbyEntities(double d, double d1, double d2) {
        return cPlayer.getNearbyEntities(d, d1, d2);
    }

    @Override
    public int getEntityId() {
        return cPlayer.getEntityId();
    }

    @Override
    public int getFireTicks() {
        return cPlayer.getFireTicks();
    }

    @Override
    public int getMaxFireTicks() {
        return cPlayer.getMaxFireTicks();
    }

    @Override
    public void setFireTicks(int i) {
        cPlayer.setFireTicks(i);
    }

    @Override
    public void remove() {
        cPlayer.remove();
    }

    @Override
    public boolean isDead() {
        return cPlayer.isDead();
    }

    @Override
    public boolean isValid() {
        return cPlayer.isValid();
    }

    @Override
    public Server getServer() {
        return cPlayer.getServer();
    }

    @Override
    public Entity getPassenger() {
        return cPlayer.getPassenger();
    }

    @Override
    public boolean setPassenger(Entity entity) {
        return cPlayer.setPassenger(entity);
    }

    @Override
    public boolean isEmpty() {
        return cPlayer.isEmpty();
    }

    @Override
    public boolean eject() {
        return cPlayer.eject();
    }

    @Override
    public float getFallDistance() {
        return cPlayer.getFallDistance();
    }

    @Override
    public void setFallDistance(float f) {
        cPlayer.setFallDistance(f);
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent ede) {
        cPlayer.setLastDamageCause(ede);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return cPlayer.getLastDamageCause();
    }

    @Override
    public UUID getUniqueId() {
        return cPlayer.getUniqueId();
    }

    @Override
    public int getTicksLived() {
        return cPlayer.getTicksLived();
    }

    @Override
    public void setTicksLived(int i) {
        cPlayer.setTicksLived(i);
    }

    @Override
    public void playEffect(EntityEffect ee) {
        cPlayer.playEffect(ee);
    }

    @Override
    public EntityType getType() {
        return cPlayer.getType();
    }

    @Override
    public boolean isInsideVehicle() {
        return cPlayer.isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return cPlayer.leaveVehicle();
    }

    @Override
    public Entity getVehicle() {
        return cPlayer.getVehicle();
    }

    @Override
    public void setMetadata(String string, MetadataValue mv) {
        cPlayer.setMetadata(string, mv);
    }

    @Override
    public List<MetadataValue> getMetadata(String string) {
        return cPlayer.getMetadata(string);
    }

    @Override
    public boolean hasMetadata(String string) {
        return cPlayer.hasMetadata(string);
    }

    @Override
    public void removeMetadata(String string, Plugin plugin) {
        cPlayer.removeMetadata(string, plugin);
    }

    @Override
    public boolean isPermissionSet(String string) {
        return cPlayer.isPermissionSet(string);
    }

    @Override
    public boolean isPermissionSet(Permission prmsn) {
        return cPlayer.isPermissionSet(prmsn);
    }

    @Override
    public boolean hasPermission(String string) {
        return cPlayer.hasPermission(string);
    }

    @Override
    public boolean hasPermission(Permission prmsn) {
        return cPlayer.hasPermission(prmsn);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln) {
        return cPlayer.addAttachment(plugin, string, bln);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return cPlayer.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln, int i) {
        return cPlayer.addAttachment(plugin, string, bln, i);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return cPlayer.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(PermissionAttachment pa) {
        cPlayer.removeAttachment(pa);
    }

    @Override
    public void recalculatePermissions() {
        cPlayer.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return cPlayer.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return cPlayer.isOp();
    }

    @Override
    public void setOp(boolean bln) {
        cPlayer.setOp(bln);
    }

    @Override
    public boolean isConversing() {
        return cPlayer.isConversing();
    }

    @Override
    public void acceptConversationInput(String string) {
        cPlayer.acceptConversationInput(string);
    }

    @Override
    public boolean beginConversation(Conversation c) {
        return cPlayer.beginConversation(c);
    }

    @Override
    public void abandonConversation(Conversation c) {
        cPlayer.abandonConversation(c);
    }

    @Override
    public void abandonConversation(Conversation c, ConversationAbandonedEvent cae) {
        cPlayer.abandonConversation(c, cae);
    }

    @Override
    public void sendMessage(String string) {
        cPlayer.sendMessage(string);
    }

    @Override
    public void sendMessage(String[] strings) {
        cPlayer.sendMessage(strings);
    }

    @Override
    public boolean isOnline() {
        return cPlayer.isOnline();
    }

    @Override
    public boolean isBanned() {
        return cPlayer.isBanned();
    }

    @Override
    public void setBanned(boolean bln) {
        cPlayer.setBanned(bln);
    }

    @Override
    public boolean isWhitelisted() {
        return cPlayer.isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean bln) {
        cPlayer.setWhitelisted(bln);
    }

    @Override
    public Player getPlayer() {
        return cPlayer.getPlayer();
    }

    @Override
    public long getFirstPlayed() {
        return cPlayer.getFirstPlayed();
    }

    @Override
    public long getLastPlayed() {
        return cPlayer.getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return cPlayer.hasPlayedBefore();
    }

    @Override
    public Map<String, Object> serialize() {
        return cPlayer.serialize();
    }

    @Override
    public void sendPluginMessage(Plugin plugin, String string, byte[] bytes) {
        cPlayer.sendPluginMessage(plugin, string, bytes);
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return cPlayer.getListeningPluginChannels();
    }

    @Override
    public void setTexturePack(String string) {
        cPlayer.setTexturePack(string);
    }

    @Override
    public void resetMaxHealth() {
        cPlayer.resetMaxHealth();
    }

    @Override
    public void setPlayerWeather(WeatherType wt) {
        cPlayer.setPlayerWeather(wt);
    }

    @Override
    public WeatherType getPlayerWeather() {
        return cPlayer.getPlayerWeather();
    }

    @Override
    public void resetPlayerWeather() {
        cPlayer.resetPlayerWeather();
    }

    @Override
    public boolean isOnGround() {
        return cPlayer.isOnGround();
    }

    @Override
    public void setCustomName(String string) {
        cPlayer.setCustomName(string);
    }

    @Override
    public String getCustomName() {
        return cPlayer.getCustomName();
    }

    @Override
    public void setCustomNameVisible(boolean bln) {
        cPlayer.setCustomNameVisible(bln);
    }

    @Override
    public boolean isCustomNameVisible() {
        return cPlayer.isCustomNameVisible();
    }

    @Override
    public Scoreboard getScoreboard() {
        return cPlayer.getScoreboard();
    }

    @Override
    public void setScoreboard(Scoreboard scrbrd) throws IllegalArgumentException, IllegalStateException {
        cPlayer.setScoreboard(scrbrd);
    }

    @Override
    public void playSound(Location lctn, String string, float f, float f1) {
        cPlayer.playSound(lctn, Sound.BURP, f1, f1);
    }

    @Override
    public boolean isHealthScaled() {
        return cPlayer.isHealthScaled();
    }

    @Override
    public void setHealthScaled(boolean bln) {
        cPlayer.setHealthScaled(bln);
    }

    @Override
    public void setHealthScale(double d) throws IllegalArgumentException {
        cPlayer.setHealthScale(d);
    }

    @Override
    public double getHealthScale() {
       return cPlayer.getHealthScale();
    }

    @Override
    public double getLastDamage() {
        return cPlayer.getLastDamage();
    }

    @Override
    public int _INVALID_getLastDamage() {
           return 0;
    }

    @Override
    public void setLastDamage(double d) {
        cPlayer.setLastDamage(d);
    }

    @Override
    public void _INVALID_setLastDamage(int i) {
    }

    @Override
    public boolean isLeashed() {
        return cPlayer.isLeashed();
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return cPlayer.getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(Entity entity) {
        return cPlayer.setLeashHolder(entity);
    }

    @Override
    public void damage(double d) {
        cPlayer.damage(d);
    }

    @Override
    public void _INVALID_damage(int i) {
    }

    @Override
    public void damage(double d, Entity entity) {
        cPlayer.damage(d, entity);
    }

    @Override
    public void _INVALID_damage(int i, Entity entity) {
    }

    @Override
    public double getHealth() {
        return cPlayer.getHealth();
    }

    @Override
    public int _INVALID_getHealth() {
        return 0;
    }

    @Override
    public void setHealth(double d) {
        cPlayer.setHealth(d);
    }

    @Override
    public void _INVALID_setHealth(int i) {
    }

    @Override
    public double getMaxHealth() {
        return cPlayer.getMaxHealth();
    }

    @Override
    public int _INVALID_getMaxHealth() {
       return _INVALID_getMaxHealth();
    }

    @Override
    public void setMaxHealth(double d) {
        cPlayer.setMaxHealth(d);
    }

    @Override
    public void _INVALID_setMaxHealth(int i) {
    }

    @Override
    public void removeAchievement(Achievement a) {
        cPlayer.removeAchievement(a);
    }

    @Override
    public boolean hasAchievement(Achievement a) {
        return cPlayer.hasAchievement(a);
    }

    @Override
    public void decrementStatistic(Statistic ststc) throws IllegalArgumentException {
        cPlayer.decrementStatistic(ststc);
    }

    @Override
    public void decrementStatistic(Statistic ststc, int i) throws IllegalArgumentException {
        cPlayer.decrementStatistic(ststc, i);
    }

    @Override
    public void setStatistic(Statistic ststc, int i) throws IllegalArgumentException {
        cPlayer.setStatistic(ststc, i);
    }

    @Override
    public int getStatistic(Statistic ststc) throws IllegalArgumentException {
        return cPlayer.getStatistic(ststc);
    }

    @Override
    public void decrementStatistic(Statistic ststc, Material mtrl) throws IllegalArgumentException {
        cPlayer.decrementStatistic(ststc, mtrl);
    }

    @Override
    public int getStatistic(Statistic ststc, Material mtrl) throws IllegalArgumentException {
        return cPlayer.getStatistic(ststc, mtrl);
    }

    @Override
    public void decrementStatistic(Statistic ststc, Material mtrl, int i) throws IllegalArgumentException {
        cPlayer.decrementStatistic(ststc, mtrl);
    }

    @Override
    public void setStatistic(Statistic ststc, Material mtrl, int i) throws IllegalArgumentException {
        cPlayer.setStatistic(ststc, mtrl, i);
    }

    @Override
    public void incrementStatistic(Statistic ststc, EntityType et) throws IllegalArgumentException {
        cPlayer.incrementStatistic(ststc, et);
    }

    @Override
    public void decrementStatistic(Statistic ststc, EntityType et) throws IllegalArgumentException {
        cPlayer.decrementStatistic(ststc, et);
    }

    @Override
    public int getStatistic(Statistic ststc, EntityType et) throws IllegalArgumentException {
        return cPlayer.getStatistic(ststc, et);
    }

    @Override
    public void incrementStatistic(Statistic ststc, EntityType et, int i) throws IllegalArgumentException {
        cPlayer.incrementStatistic(ststc, et, i);
    }

    @Override
    public void decrementStatistic(Statistic ststc, EntityType et, int i) {
        cPlayer.decrementStatistic(ststc, et, i);
    }

    @Override
    public void setStatistic(Statistic ststc, EntityType et, int i) {
        cPlayer.setStatistic(ststc, et, i);
    }

    @Override
    public void setResourcePack(String string) {
        cPlayer.setResourcePack(string);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> type, Vector vector) {
        return cPlayer.launchProjectile(type,vector);
    }

    @Override
    public void sendSignChange(Location lctn, String[] strings) throws IllegalArgumentException {
        cPlayer.sendSignChange(lctn, strings);
    }
    
}
