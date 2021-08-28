package xyz.refinedev.practice.profile.settings.menu;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.settings.SettingsType;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 3/12/2021
 * Project: Array
 */

public class SettingsMenu extends Menu {

    private final Array plugin = Array.getInstance();
    private final FoldersConfigurationFile config = plugin.getMenuManager().getConfigByName("settings");

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Player player) {
        return config.getString("TITLE");
    }

    /**
     * Size of the inventory
     *
     * @return {@link Integer}
     */
    @Override
    public int getSize() {
        return config.getInteger("SIZE");
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link HashMap}
     */
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        String key = "BUTTONS.";

        for ( SettingsType type : SettingsType.values() ) {
            if (type.equals(SettingsType.TOGGLETABSTYLE) && !plugin.getConfigHandler().isTAB_ENABLED()) continue;

            buttons.put(config.getInteger(key + type.name() + ".SLOT"), new SettingsButton(type));
        }
        return buttons;
    }
}