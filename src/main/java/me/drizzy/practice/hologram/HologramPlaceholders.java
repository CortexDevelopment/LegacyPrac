package me.drizzy.practice.hologram;

import com.allatori.annotations.DoNotRename;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.drizzy.practice.ArrayCache;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.statistics.LeaderboardsAdapter;
import org.bukkit.entity.Player;
import me.drizzy.practice.kit.Kit;

@DoNotRename
public class HologramPlaceholders extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "array";
    }

    @Override
    public String getAuthor() {
        return "Drizzy";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "&7";
        }

        /**
         * Originally coded by Nick and improved by Drizzy.
         * Note: The Player Cache in profile should not be null for it to work!
         *
         * @param GlobalLeaderboards - array_global_<position>
         */

        if (identifier.contains("global")) {
            String[] splitstring = identifier.split("_");
            int number = Integer.parseInt(splitstring[1]) - 1;
            LeaderboardsAdapter leaderboardsAdapter;

            try {
                leaderboardsAdapter= Profile.getGlobalEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (leaderboardsAdapter == null) {
                return "&7";
            }

            Profile profile = Profile.getByUuid(ArrayCache.getUUID(leaderboardsAdapter.getName()));

            return "&b" + (number + 1) + ". &f" + leaderboardsAdapter.getName() + ": &b" + leaderboardsAdapter.getElo() + " &7(" + profile.getEloLeague() + ")";
        }

        /**
         * Originally coded by Nick and improved by Drizzy.
         * Note: The Player Cache in profile should not be null for it to work!
         *
         * @param LeaderboardsAdapter - array_lb_<Kit>_<position>
         */
        if (identifier.contains("lb")) {
            String[] splitstring = identifier.split("_");
            String kitString = splitstring[1];
            int number = Integer.parseInt(splitstring[2]) - 1;
            Kit kit = Kit.getByName(kitString);

            if (kit == null) return "&7";

            LeaderboardsAdapter leaderboardsAdapter;

            try {
                leaderboardsAdapter= kit.getRankedEloLeaderboards().get(number);
            } catch (Exception e) {
                return "&7";
            }

            if (leaderboardsAdapter == null) {
                return "&7";
            }

            Profile profile = Profile.getByUuid(ArrayCache.getUUID(leaderboardsAdapter.getName()));

            return "&b" + (number + 1) + ". &f" + leaderboardsAdapter.getName() + ": &b" + leaderboardsAdapter.getElo() + " &7(" + profile.getEloLeague() + ")";
        }
        return null;
    }
}