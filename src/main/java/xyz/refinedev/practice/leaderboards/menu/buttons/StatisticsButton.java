package xyz.refinedev.practice.leaderboards.menu.buttons;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.SkullCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/13/2021
 * Project: Array
 */

public class StatisticsButton extends Button {

    private final Array plugin = this.getPlugin();

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());

        lore.add(CC.MENU_BAR);
        for ( Kit kit : Kit.getKits() ) {
            if (kit.getGameRules().isRanked() && kit.isEnabled()) {
                lore.add("&c" + kit.getName() + ": &f" + profile.getStatisticsData().get(kit).getElo());
            }
        }
        lore.add(CC.MENU_BAR);
        lore.add("&aGlobal ELO: &f" + profile.getGlobalElo());
        lore.add("&aGlobal League: &f" + plugin.getProfileManager().getDivision(profile));
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(SkullCreator.itemFromUuid(player.getUniqueId()))
                .name("&c" + player.getName() + " &7\uff5c &fStatistics")
                .lore(lore)
                .clearFlags()
                .build();
    }
}