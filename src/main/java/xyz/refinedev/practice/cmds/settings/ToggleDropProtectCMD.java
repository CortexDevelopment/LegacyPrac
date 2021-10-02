package xyz.refinedev.practice.cmds.settings;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.settings.meta.Settings;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/29/2021
 * Project: Array
 */
@RequiredArgsConstructor
public class ToggleDropProtectCMD {

    private final Array plugin;

    @Command(name = "", desc = "Toggle Drop Protect for your Profile")
    @Require("array.profile.dropprotect")
    public void toggle(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getByPlayer(player);
        Settings settings = profile.getSettings();

        settings.setDropProtect(!settings.isDropProtect());

        String enabled = Locale.SETTINGS_ENABLED.toString().replace("<setting_name>", "Drop Protect");
        String disabled = Locale.SETTINGS_DISABLED.toString().replace("<setting_name>", "Drop Protect");

        player.sendMessage(settings.isDropProtect() ? enabled : disabled);
    }

}
