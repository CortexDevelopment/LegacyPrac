package xyz.refinedev.practice.kit;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.match.types.TeamMatch;
import xyz.refinedev.practice.match.types.kit.BoxingMatch;
import xyz.refinedev.practice.match.types.kit.solo.SoloBridgeMatch;
import xyz.refinedev.practice.match.types.kit.team.TeamBridgeMatch;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.inventory.InventoryUtil;
import xyz.refinedev.practice.util.inventory.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/21/2021
 * Project: Array
 */


@Getter @Setter
public class Kit {

    private final Array plugin;

    private final List<ItemStack> editorItems = new ArrayList<>();
    private List<LeaderboardsAdapter> eloLeaderboards = new ArrayList<>();
    private List<LeaderboardsAdapter> winLeaderboards = new ArrayList<>();

    private final KitGameRules gameRules = new KitGameRules();
    private KitInventory kitInventory = new KitInventory();

    private final String name;
    private boolean enabled;
    private String knockbackProfile;
    private List<String> kitDescription;
    private ItemStack displayIcon;
    private String displayName;
    private Queue unrankedQueue, rankedQueue, clanQueue;

    public Kit(Array plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.displayName = CC.RED + name;
        this.displayIcon = new ItemStack(Material.DIAMOND_CHESTPLATE);
        this.knockbackProfile = "default";
        this.kitDescription = new ArrayList<>();

        plugin.getKitManager().getKits().add(this);
    }

    public ItemStack getDisplayIcon() {
        return this.displayIcon.clone();
    }

    public boolean isParty() {
        return (!gameRules.isDisablePartyFFA() && !gameRules.isParkour() && !gameRules.isBridge() && !gameRules.isDisablePartySplit() && isEnabled());
    }

    public void applyToPlayer(Player player) {
        player.getInventory().setArmorContents(getKitInventory().getArmor());
        player.getInventory().setContents(getKitInventory().getContents());
        player.updateInventory();
    }
}
