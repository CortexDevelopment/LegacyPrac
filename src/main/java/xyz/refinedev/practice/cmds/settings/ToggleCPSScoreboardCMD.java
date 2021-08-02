package xyz.refinedev.practice.cmds.settings;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.settings.meta.SettingsMeta;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/7/2021
 * Project: Array
 */

public class ToggleCPSScoreboardCMD {

    @Command(name = "", desc = "Toggle CPS on Scoreboard for your Profile")
    public void toggle(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        SettingsMeta settings = profile.getSettings();

        settings.setCpsScoreboard(!settings.isCpsScoreboard());

        String enabled = Locale.SETTINGS_ENABLED.toString().replace("<setting_name>", "CPS on Scoreboard");
        String disabled = Locale.SETTINGS_DISABLED.toString().replace("<setting_name>", "CPS on Scoreboard");

        player.sendMessage(settings.isCpsScoreboard() ? enabled : disabled);
    }

}