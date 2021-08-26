package xyz.refinedev.practice.util.nametags.listener;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.lang.reflect.Field;
import java.util.Collections;

public final class NameTagListener implements Listener {

    private final Array plugin = Array.getInstance();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setMetadata("sl-LoggedIn", new FixedMetadataValue(plugin, true));

        if (plugin.getConfigHandler().isNAMETAGS_ENABLED()) {
            try {
                PacketPlayOutScoreboardTeam a = new PacketPlayOutScoreboardTeam();
                team_mode.set(a, 3);
                team_name.set(a, "zLane");
                team_display.set(a, "zLane");
                team_color.set(a, -1);
                team_players.set(a, Collections.singletonList(event.getPlayer().getName()));

                for (Player other : Bukkit.getOnlinePlayers()) {
                    TaskUtil.runAsync(() -> ((CraftPlayer) other).getHandle().playerConnection.sendPacket(a));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        plugin.getNameTagHandler().initiatePlayer(event.getPlayer());
        plugin.getNameTagHandler().reloadPlayer(event.getPlayer());
        plugin.getNameTagHandler().reloadOthersFor(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("sl-LoggedIn", plugin);
        plugin.getNameTagHandler().getTeamMap().remove(event.getPlayer().getName());
    }


    private Field team_name;
    private Field team_display;
    private Field team_players;
    private Field team_mode;
    private Field team_color;

    {
        try {
            team_name = PacketPlayOutScoreboardTeam.class.getDeclaredField("a");
            team_name.setAccessible(true);
            team_display = PacketPlayOutScoreboardTeam.class.getDeclaredField("b");
            team_display.setAccessible(true);
            Field team_prefix = PacketPlayOutScoreboardTeam.class.getDeclaredField("c");
            team_prefix.setAccessible(true);
            Field team_suffix = PacketPlayOutScoreboardTeam.class.getDeclaredField("d");
            team_suffix.setAccessible(true);
            team_players = PacketPlayOutScoreboardTeam.class.getDeclaredField("g");
            team_players.setAccessible(true);
            team_color = PacketPlayOutScoreboardTeam.class.getDeclaredField("f");
            team_color.setAccessible(true);
            team_mode = PacketPlayOutScoreboardTeam.class.getDeclaredField("h");
            team_mode.setAccessible(true);
            Field team_nametag = PacketPlayOutScoreboardTeam.class.getDeclaredField("e");
            team_nametag.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}