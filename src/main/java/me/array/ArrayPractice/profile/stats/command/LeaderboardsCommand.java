package me.array.ArrayPractice.profile.stats.command;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.profile.stats.menu.RankedLeaderboardsMenu;
import org.bukkit.entity.Player;
import com.qrakn.honcho.command.CommandMeta;

@CommandMeta(label = { "leaderboards", "top" })
public class LeaderboardsCommand
{
    public void execute(final Player player) {
        new RankedLeaderboardsMenu().openMenu(player);
    }
}