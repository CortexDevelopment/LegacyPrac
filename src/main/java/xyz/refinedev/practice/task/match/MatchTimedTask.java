package xyz.refinedev.practice.task.match;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.Match;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 11/6/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class MatchTimedTask extends BukkitRunnable {

    private final Array plugin;
    private final Match match;

    @Override
    public void run() {
        if (!match.isFighting()) return;

        if (match.getDuration().equalsIgnoreCase("01:00") || (match.getDuration().equalsIgnoreCase("01:01")) || (match.getDuration().equalsIgnoreCase("01:02"))) {
            this.plugin.getMatchManager().end(match);
            this.cancel();
        }
    }
}
