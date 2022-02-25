package xyz.refinedev.practice.duel.menu.buttons;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.duel.menu.DuelSelectArenaMenu;
import xyz.refinedev.practice.duel.menu.DuelSelectKitMenu;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 1/14/2022
 * Project: Array
 */

//TODO: Configure
public class DuelKitButton extends Button {

    private final DuelSelectKitMenu menu;
    private final Kit kit;

    public DuelKitButton(Array plugin, DuelSelectKitMenu menu, Kit kit) {
        super(plugin);
        this.menu = menu;
        this.kit = kit;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("&cClick to send a duel with this kit.");
        return new ItemBuilder(kit.getDisplayIcon())
                .name(kit.getDisplayName())
                .lore(lore)
                .clearFlags()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = this.getPlugin().getProfileManager().getProfile(player.getUniqueId());

        if (profile.getDuelProcedure() == null) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }

        Arena arena = this.getPlugin().getArenaManager().getByKit(kit);

        profile.getDuelProcedure().setKit(kit);
        profile.getDuelProcedure().setArena(arena);

        this.menu.setClosedByMenu(true);
        player.closeInventory();

        if (player.hasPermission("array.duel.arena")) {
            new DuelSelectArenaMenu(this.getPlugin()).openMenu(player);
        } else {
            profile.getDuelProcedure().send();
        }
    }

}
