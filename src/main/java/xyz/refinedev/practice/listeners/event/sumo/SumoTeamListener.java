package xyz.refinedev.practice.listeners.event.sumo;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/25/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class SumoTeamListener implements Listener {
    
    private final Array plugin;

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Profile profile = plugin.getProfileManager().getByUUID(event.getPlayer().getUniqueId());
        Player player = event.getPlayer();
        if (profile.isInEvent() && profile.getEvent().isSumoTeam()) {
            if (!profile.getEvent().isFighting(player.getUniqueId())) {
                event.setCancelled(true);
            }
        } else if (profile.getEvent() != null && profile.getEvent().getSpectators().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getByUUID(event.getPlayer().getUniqueId());
        if (profile.isInEvent() && profile.getEvent().isSumoTeam() || (profile.getEvent() != null && profile.getEvent().getSpectators().contains(event.getPlayer().getUniqueId()))) {
            if (profile.isInEvent() && profile.getEvent().isSumoTeam()) {
                if (!profile.getEvent().isFighting(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            } else if (profile.getEvent() != null && profile.getEvent().getSpectators().contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
    public void onHit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player player = ((Player) event.getEntity()).getPlayer();
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            if (profile.isInEvent() && profile.getEvent().isSumoTeam()) {
                if (!profile.getEvent().isFighting(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            } else if (profile.getEvent() != null && profile.getEvent().getSpectators().contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            Event sumo = profile.getEvent();
            
            if (profile.isInEvent() && profile.getEvent().isSumoTeam()) {
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                    event.setCancelled(true);
                    event.getEntity().setFireTicks(0);
                    
                    if (!profile.getEvent().isFighting() || !profile.getEvent().isFighting(player.getUniqueId())) {
                        player.teleport(plugin.getEventManager().getSpectator(sumo));
                        return;
                    }  else if (profile.getEvent() != null && profile.getEvent().getSpectators().contains(player.getUniqueId())) {
                        event.setCancelled(true);
                        return;
                    }
                    
                    player.teleport(plugin.getEventManager().getSpectator(sumo));
                    profile.getEvent().handleDeath(player);
                    plugin.getProfileManager().refreshHotbar(profile);
                    return;
                }

                if (profile.getEvent() != null) {
                    if (!profile.getEvent().isFighting() || !profile.getEvent().isFighting(player.getUniqueId())) {
                        event.setCancelled(true);
                        return;
                    } else if (profile.getEvent() != null && profile.getEvent().getSpectators().contains(player.getUniqueId())) {
                        event.setCancelled(true);
                    }

                    event.setDamage(0);
                    player.setHealth(20.0);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player attacker;

        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                attacker = (Player) ((Projectile) event.getDamager()).getShooter();
            } else {
                event.setCancelled(true);
                return;
            }
        } else {
            event.setCancelled(true);
            return;
        }

        if (attacker != null && event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Profile damagedProfile = plugin.getProfileManager().getByUUID(damaged.getUniqueId());
            Profile attackerProfile = plugin.getProfileManager().getByUUID(attacker.getUniqueId());

            if (damagedProfile.isInEvent() && damagedProfile.getEvent().isSumoTeam() && attackerProfile.isInEvent() && attackerProfile.getEvent().isSumoTeam()) {
                Event sumo = damagedProfile.getEvent();

                if (!sumo.isFighting() || !sumo.isFighting(damaged.getUniqueId()) || !sumo.isFighting(attacker.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Profile profile = plugin.getProfileManager().getByUUID(event.getPlayer().getUniqueId());

        if (profile.isInEvent() && profile.getEvent().isSumoTeam()) {
            profile.getEvent().handleLeave(event.getPlayer());
        }
    }
}