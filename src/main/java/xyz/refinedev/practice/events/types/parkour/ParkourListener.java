package xyz.refinedev.practice.events.types.parkour;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.types.parkour.player.ParkourPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.events.types.parkour.player.ParkourPlayerState;
import xyz.refinedev.practice.util.location.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;

public class ParkourListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Profile profile = Profile.getByUuid(player.getUniqueId());
			if (profile.isInParkour()) {
				event.setCancelled(true);
				if (event.getCause() == EntityDamageEvent.DamageCause.VOID || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
					event.setCancelled(true);
					event.getEntity().setFireTicks(0);
					if (profile.getParkour().isFighting(player)) {
						if (profile.getParkour().getEventPlayer(player).getLastLocation() != null) {
							player.teleport(profile.getParkour().getEventPlayer(player).getLastLocation());
						} else {
							player.teleport(Array.getInstance().getParkourManager().getParkourSpawn());
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.isInParkour()) {
			profile.getParkour().handleLeave(event.getPlayer());
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
		if (profile.isInParkour()) {
			if (!profile.getParkour().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent event) {
		Profile profile = Profile.getByUuid(event.getWhoClicked().getUniqueId());
		if (profile.isInParkour()) {
			if (!profile.getParkour().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInParkour()) {
			if (!profile.getParkour().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled=true)
	public void onBreak(BlockBreakEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInParkour()) {
			event.setCancelled(true);
		}
	}


	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
		if (profile.isInParkour()) {
			if (!profile.getParkour().isFighting(profile.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
			Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

			if (profile.isInParkour()) {
				if (profile.getParkour().getEventPlayer(event.getPlayer()).getState().equals(ParkourPlayerState.ELIMINATED)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onButton(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Profile profile = Profile.getByUuid(player.getUniqueId());
		if (profile.isInParkour()) {
			Parkour parkour = profile.getParkour();
			ParkourPlayer parkourPlayer = parkour.getEventPlayer(player);
			if (!parkour.getState().equals(ParkourState.ROUND_ENDING)) {
				try {
					if (event.getClickedBlock().getLocation() != null && profile.getPlates() != null) {
						if (profile.getPlates().contains(event.getClickedBlock().getLocation())) {
							event.setCancelled(true);
							return;
						}

						if (event.getAction().equals(Action.PHYSICAL) && (!event.getAction().name().contains("LEFT") || !event.getAction().name().contains("RIGHT"))) {
							if (event.getClickedBlock().getType() == Material.GOLD_PLATE) {
								if (parkour.getEventPlayer(player).getState().equals(ParkourPlayerState.WAITING)) {
									parkour.handleWin(player);
									parkour.broadcastMessage(Locale.EVENT_PARKOUR_WON.toString().replace("<winner>", player.getDisplayName()));
									profile.getPlates().add(event.getClickedBlock().getLocation());
								}
							} else if (event.getClickedBlock().getType() == Material.IRON_PLATE) {
								if (parkourPlayer.getState().equals(ParkourPlayerState.WAITING)) {
									parkourPlayer.setLastLocation(player.getLocation());
									player.sendMessage(Locale.MATCH_CHECKPOINT.toString());
									profile.getPlates().add(event.getClickedBlock().getLocation());
								}
							}
							event.setCancelled(true);
						}
					}
				} catch (Exception e) {
					//
				}
			}
		}
	}


	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerMove(PlayerMoveEvent event) {
		Profile profile=Profile.getByUuid(event.getPlayer().getUniqueId());
		Player player=event.getPlayer();
		Location to=event.getTo();

		if (profile.isInParkour()) {
			if (profile.getParkour().getState() == ParkourState.ROUND_FIGHTING) {
				if (BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1)) {
					if (profile.getParkour().getEventPlayer(player).getLastLocation() != null) {
						player.teleport(profile.getParkour().getEventPlayer(player).getLastLocation());
					} else {
						player.teleport(Array.getInstance().getParkourManager().getParkourSpawn());
					}
				}
			}
			if (profile.getParkour().getState() == ParkourState.WAITING || profile.getParkour().getState() == ParkourState.ROUND_STARTING) {
				player.teleport(player.getLocation());
			}
		}
	}
}