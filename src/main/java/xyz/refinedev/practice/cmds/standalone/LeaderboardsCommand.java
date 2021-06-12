package xyz.refinedev.practice.cmds.standalone;

import xyz.refinedev.practice.leaderboards.menu.LeaderboardsMenu;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;
import org.bukkit.entity.Player;

/**
 * This Project is property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/1/2021
 * Project: Array
 */

public class LeaderboardsCommand {

    @Command(name = "", desc = "Open Leaderboards Menu")
    public void leaderboard(@Sender Player player) {
        new LeaderboardsMenu().openMenu(player);
    }
}
