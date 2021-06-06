package me.drizzy.practice.cmds;

import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.profile.statistics.StatisticsData;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.annotation.Command;
import me.drizzy.practice.util.command.annotation.Require;
import me.drizzy.practice.util.command.annotation.Sender;
import me.drizzy.practice.util.other.TaskUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/21/2021
 * Project: Array
 */

public class KitCommands {

    @Command(name = "", aliases = "help", desc = "View Kit Commands")
    @Require("array.kit.admin")
    public void kit(@Sender CommandSender player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate( "&cArray &7» Kit Commands"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/kit create &8<&7kit&8> &8(&7&oCreate a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit remove &8<&7kit&8> &8(&7&oDelete a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit toggle &8<&7kit&8> &8(&7&oEnable or Disable a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit list &8(&7&oLists All Kits&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit save &8(&7&oSave All the Kits&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerules &8(&7&oView gamerules for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit setkb &8<&7kit&8> &8<&7knockback&8> &8(&7&oSet a Kit's Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit hitdelay &8<&7kit&8> &8<&71-20&8> &8&o(&7&oSet a Kit's Hit delay&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule &8<&7kit&8> &8<&7gamerule&8> &8(&7&oSet gamerules for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit seteditinv &8(&7&oSets the Edit inventory of the kit as your inventory&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit getinv &8(&7&oGet the inventory of the kit&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "list", desc = "View all kits")
    @Require("array.kit.admin")
    public void kitList(@Sender CommandSender player) {

        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&cArray &7» All Kits"));
        player.sendMessage(CC.CHAT_BAR);

        for ( Kit kit : Kit.getKits() ) {
            if (kit == null) {
                player.sendMessage(CC.translate(""));
                player.sendMessage(CC.translate("&7&oThere are no kits setup."));
                player.sendMessage(CC.translate(""));
            } else {
                player.sendMessage(CC.translate(" &8• " + (kit.isEnabled() ? CC.GREEN : CC.RED) + kit.getDisplayName()));
            }
        }

        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "save", aliases = "export", desc = "Export all Kits to Config")
    public void kitSave(@Sender CommandSender player) {
        Kit.getKits().forEach(Kit::save);
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7You saved &c" + Kit.getKits().size() +  " the kits!"));
    }

    @Command(name = "setdisplayname", aliases = "setdname", usage = "<kit> <displayname>", desc = "Set a Kit's Display name")
    public void kitDisplayname(@Sender CommandSender player, Kit kit, String displayname) {
        kit.setDisplayName(displayname);
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set &c" + kit.getName() + "'s &7display name to &c" + displayname));
    }

    @Command(name = "seticon", aliases = "icon", usage = "<kit>", desc = "Set a Kit's icon")
    public void kitIcon(@Sender Player player, Kit kit) {
        ItemStack item = player.getItemInHand();
        if (item == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Please hold a valid item in your hand!"));
        } else if (kit == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7That kit does not exist."));
        } else {
            kit.setDisplayIcon(item);
            kit.save();
        }
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully updated &c" + kit.getDisplayName() + "'s &7icon"));
    }

    @Command(name = "create", desc = "Create a kit", usage = "<name>")
    @Require("array.kit.admin")
    public void kitCreate(@Sender CommandSender player, String kitName) {
        if (Kit.getByName(kitName) != null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7A kit with that name already exists."));
            return;
        }

        Kit kit = new Kit(kitName);
        kit.setEnabled(false);
        kit.getGameRules().setRanked(true);
        kit.save();

        //Add it to profile's statistics data
        TaskUtil.runAsync(() -> {
            for ( Profile profile : Profile.getProfiles().values() ) {
                profile.getStatisticsData().put(kit, new StatisticsData());
                profile.save();
            }
        });

        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully created a new kit &c" + kit.getDisplayName() + "&7."));
    }

    @Command(name = "delete", aliases = "kit remove", usage = "<kit>", desc = "Delete a Kit")
    public void kitDelete(@Sender CommandSender player, Kit kit) {
        kit.delete();
        Kit.getKits().forEach(Kit::save);
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Removed the kit &c" + kit.getName() + "&7."));
    }

    @Command(name = "info", aliases = {"status", "desc", "description"}, desc = "View a kit's information")
    @Require("array.kit.admin")
    public void kitInfo(@Sender CommandSender player, Kit kit) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&cArray &7» " + kit.getDisplayName() + " 's Information"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &cName &7» &f" + kit.getName()));
        player.sendMessage(CC.translate(" &8• &cDisplay-Name &7» &f" + kit.getDisplayName()));
        player.sendMessage(CC.translate(" &8• &cEnabled &7» &f" + (kit.isEnabled() ? "&aTrue" : "&cFalse")));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "knockback", aliases = {"setknockback", "kb", "setkb"}, usage = "<kit> <knockback>", desc = "Set a Kit's Knockback Profile")
    public void kitKnockback(@Sender CommandSender player, Kit kit, String profile) {
        kit.setKnockbackProfile(profile);
        kit.save();

        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Updated knockback profile for &c" + kit.getName() +  " &7to &c" + profile));
    }

    @Command(name = "setinv", aliases = {"setinventory", "setloadout"}, desc = "Update a kit's inventory")
    @Require("array.kit.setup")
    public void kitSetInv(@Sender Player player, Kit kit) {
        //Update the main contents
        kit.getKitInventory().setArmor(player.getInventory().getArmorContents());
        kit.getKitInventory().setContents(player.getInventory().getContents());

        //Then update the potion effects
        List<PotionEffect> potionEffects = new ArrayList<>(player.getActivePotionEffects());
        kit.getKitInventory().setEffects(potionEffects);

        //Lastly save the kit
        kit.save();
        player.sendMessage(CC.translate("&8[&c&lArray&8] &aYou updated the kit's inventory."));
    }

    @Command(name = "getinv", aliases = {"getinventory", "getloadout"}, desc = "Receive a kit's inventory")
    @Require("array.kit.setup")
    public void kitGetInv(@Sender Player player, Kit kit) {
        player.getInventory().setContents(kit.getKitInventory().getContents());
        player.getInventory().setArmorContents(kit.getKitInventory().getArmor());

        player.sendMessage(CC.translate("&8[&c&lArray&8] &aYou received the kit's inventory."));
    }

    @Command(name = "geteditinv", aliases = {"geteditinventory", "geteditloadout"}, desc = "Receive a kit's edit inventory")
    @Require("array.kit.setup")
    public void kitGetEditInv(@Sender Player player, Kit kit) {
        player.getInventory().clear();
        kit.getEditorItems().forEach(itemStack -> player.getInventory().addItem(itemStack));

        player.sendMessage(CC.translate("&8[&c&lArray&8] &aYou received the kit's edit inventory."));
    }

    @Command(name = "seteditinv", aliases = {"seteditinventory", "seteditloadout"}, desc = "Update a kit's editor inventory")
    @Require("array.kit.setup")
    public void kitSetEditInv(@Sender Player player, Kit kit) {
        for ( ItemStack content : player.getInventory().getContents() ) {
            kit.getEditorItems().clear();
            kit.getEditorItems().add(content);
        }

        kit.save();
        player.sendMessage(CC.translate("&8[&c&lArray&8] &aYou updated the kit's editor inventory."));
    }

    @Command(name = "toggle", aliases = {"enable", "disable"}, desc = "Enable or Disable a kit")
    @Require("array.kit.admin")
    public void kitToggle(@Sender CommandSender sender, Kit kit) {
        kit.setEnabled(!kit.isEnabled());

        if (kit.isEnabled()) {
            if (kit.getUnrankedQueue() == null) kit.setUnrankedQueue(new Queue(kit, QueueType.UNRANKED));
            if (kit.getRankedQueue() == null) kit.setRankedQueue(new Queue(kit, QueueType.RANKED));
            sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully &aenabled &7the kit &c" + kit.getDisplayName()));
        } else {
            if (kit.getUnrankedQueue() != null) Queue.getQueues().remove(kit.getUnrankedQueue());
            if (kit.getRankedQueue() != null) Queue.getQueues().remove(kit.getRankedQueue());
            sender.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully &cdisabled &7the kit &c" + kit.getDisplayName()));
        }
    }

    @Command(name = "hitdelay", aliases = "sethitdelay", usage = "<kit> <amount>", desc = "Set a Kit's Hit Delay")
    public void kitHitDelay(@Sender CommandSender player, Kit kit, Integer integ) {
        kit.getGameRules().setHitDelay(integ);
        kit.save();
        player.sendMessage(CC.translate("&8[&c&lArray&8] &7Updated &c" + kit.getName() + " &7hit delay set to &c" + integ));
    }

    @Command(name = "gamerule", desc = "Toggle a Gamerule for the Kit", usage = "<kit> <gamerule>")
    @Require("array.kit.setup")
    public void gameRuleToggle(@Sender CommandSender player, Kit kit, String gamerule) {
        switch (gamerule) {
            case "elo":
            case "ranked": {
                kit.getGameRules().setRanked(!kit.getGameRules().isRanked());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isRanked() ? "&aenabled " : "&cdisabled ") + "&7ranked or &c" + kit.getDisplayName()));
                break;
            }
            case "build": {
                kit.getGameRules().setBuild(!kit.getGameRules().isBuild());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isBuild() ? "&aenabled " : "&cdisabled ") + "&7build or &c" + kit.getDisplayName()));
                break;
            }
            case "bridge": {
                kit.getGameRules().setBridge(!kit.getGameRules().isBridge());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isBridge() ? "&aenabled " : "&cdisabled ") + "&7bridge or &c" + kit.getDisplayName()));
                break;
            }
            case "combo": {
                kit.getGameRules().setCombo(!kit.getGameRules().isCombo());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isCombo() ? "&aenabled " : "&cdisabled ") + "&7combo or &c" + kit.getDisplayName()));
                break;
            }
            case "editable": {
                kit.getGameRules().setEditable(!kit.getGameRules().isEditable());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isEditable() ? "&aenabled " : "&cdisabled ") + "&7editable or &c" + kit.getDisplayName()));
                break;
            }
            case "lavakill": {
                kit.getGameRules().setLavaKill(!kit.getGameRules().isLavaKill());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isLavaKill() ? "&aenabled " : "&cdisabled ") + "&7lava-kill or &c" + kit.getDisplayName()));
                break;
            }
            case "parkour": {
                kit.getGameRules().setParkour(!kit.getGameRules().isParkour());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isParkour() ? "&aenabled " : "&cdisabled ") + "&7parkour or &c" + kit.getDisplayName()));
                break;
            }
            case "clan": {
                kit.getGameRules().setClan(!kit.getGameRules().isClan());
                if (kit.getClanQueue() == null && kit.getGameRules().isClan()) new Queue(kit, QueueType.CLAN);
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isClan() ? "&aenabled " : "&cdisabled ") + "&7clan mode or &c" + kit.getDisplayName()));
                break;
            }
            case "partyffa": {
                kit.getGameRules().setDisablePartyFFA(!kit.getGameRules().isDisablePartyFFA());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (!kit.getGameRules().isDisablePartyFFA() ? "&aenabled " : "&cdisabled ") + "&7party-brawl or &c" + kit.getDisplayName()));
                break;
            }
            case "partysplit": {
                kit.getGameRules().setDisablePartySplit(!kit.getGameRules().isDisablePartySplit());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (!kit.getGameRules().isDisablePartySplit() ? "&aenabled " : "&cdisabled ") + "&7party-split or &c" + kit.getDisplayName()));
                break;
            }
            case "hunger":
            case "food": {
                kit.getGameRules().setAntiFoodLoss(!kit.getGameRules().isAntiFoodLoss());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (!kit.getGameRules().isAntiFoodLoss() ? "&aenabled " : "&cdisabled ") + "&7hunger or &c" + kit.getDisplayName()));
                break;
            }
            case "showhealth": {
                kit.getGameRules().setShowHealth(!kit.getGameRules().isShowHealth());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isShowHealth() ? "&aenabled " : "&cdisabled ") + "&7show-health or &c" + kit.getDisplayName()));
                break;
            }
            case "healthregeneration":
            case "healthregen": {
                kit.getGameRules().setHealthRegeneration(!kit.getGameRules().isHealthRegeneration());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isHealthRegeneration() ? "&aenabled " : "&cdisabled ") + "&7health-regen or &c" + kit.getDisplayName()));
                break;
            }
            case "speed": {
                kit.getGameRules().setInfiniteSpeed(!kit.getGameRules().isInfiniteSpeed());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isInfiniteSpeed() ? "&aenabled " : "&cdisabled ") + "&7speed or &c" + kit.getDisplayName()));
                break;
            }
            case "strength": {
                kit.getGameRules().setInfiniteStrength(!kit.getGameRules().isInfiniteStrength());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isInfiniteStrength() ? "&aenabled " : "&cdisabled ") + "&7strength or &c" + kit.getDisplayName()));
                break;
            }
            case "noitems": {
                kit.getGameRules().setNoItems(!kit.getGameRules().isNoItems());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isNoItems() ? "&aenabled " : "&cdisabled ") + "&7no-items or &c" + kit.getDisplayName()));
                break;
            }
            case "bowhp": {
                kit.getGameRules().setBowHP(!kit.getGameRules().isBowHP());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isBowHP() ? "&aenabled " : "&cdisabled ") + "&7bow-hp or &c" + kit.getDisplayName()));
                break;
            }
            case "stickspawn": {
                kit.getGameRules().setStickSpawn(!kit.getGameRules().isStickSpawn());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isStickSpawn() ? "&aenabled " : "&cdisabled ") + "&7stick-spawn or &c" + kit.getDisplayName()));
                break;
            }
            case "boxuhc": {
                kit.getGameRules().setBoxUHC(!kit.getGameRules().isBoxUHC());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isBoxUHC() ? "&aenabled " : "&cdisabled ") + "&7box-uhc or &c" + kit.getDisplayName()));
                break;
            }
            case "falldamage": {
                kit.getGameRules().setDisableFallDamage(!kit.getGameRules().isDisableFallDamage());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (!kit.getGameRules().isDisableFallDamage() ? "&aenabled " : "&cdisabled ") + "&7fall damage or &c" + kit.getDisplayName()));
                break;
            }
            case "spleef": {
                kit.getGameRules().setSpleef(!kit.getGameRules().isSpleef());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isSpleef() ? "&aenabled " : "&cdisabled ") + "&7spleef or &c" + kit.getDisplayName()));
                break;
            }
            case "sumo": {
                kit.getGameRules().setSumo(!kit.getGameRules().isSumo());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isSumo() ? "&aenabled " : "&cdisabled ") + "&7sumo or &c" + kit.getDisplayName()));
                break;
            }
            case "voidspawn": {
                kit.getGameRules().setVoidSpawn(!kit.getGameRules().isVoidSpawn());
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully " + (kit.getGameRules().isVoidSpawn() ? "&aenabled " : "&cdisabled ") + "&7voidspawn for &c" + kit.getDisplayName()));
                break;
            }
            default: {
                this.kitGameRule(player);
            }
        }
    }

    @Command(name = "gamerules", desc = "View kit gamerules")
    @Require("array.kit.setup")
    public void kitGameRule(@Sender CommandSender player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate( "&cArray &7» Kit GameRules "));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/kit gamerule ranked &8<&7kit&8> &8(&7&oToggle ranked mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule clan &8<&7kit&8> &8(&7&oToggle clan mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule build &8<&7kit&8> &8(&7&oToggle build mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule sumo &8<&7kit&8> &8(&7&oToggle sumo mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule bridge &8<&7kit&8> &8(&7&oToggle bridge mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule combo &8<&7kit&8> &8(&7&oToggle combo mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule editable &8<&7kit&8> &8(&7&oToggle editable mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule lavaKill &8<&7kit&8> &8(&7&oToggle lava-kill mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule parkour &8<&7kit&8> &8(&7&oToggle parkour mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule partyffa &8<&7kit&8> &8(&7&oToggle party-brawl mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule partysplit &8<&7kit&8> &8(&7&oToggle party-split mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule hunger &8<&7kit&8> &8(&7&oToggle anti-food-loss mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule bowhp &8<&7kit&8> &8(&7&oToggle bow-hp mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule healthregen &8<&7kit&8> &8(&7&oToggle health-regen mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule speed &8<&7kit&8> &8(&7&oToggle infinite-speed mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule strength &8<&7kit&8> &8(&7&oToggle infinite-strength mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule noitems &8<&7kit&8> &8(&7&oToggle no-items mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule showhealth &8<&7kit&8> &8(&7&oToggle show-health mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule stickspawn &8<&7kit&8> &8(&7&oToggle stick-spawn mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule boxuhc &8<&7kit&8> &8(&7&oToggle box-uhc mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule falldamage &8<&7kit&8> &8(&7&oToggle fall damage for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule spleef &8<&7kit&8> &8(&7&oToggle spleef mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule timed &8<&7kit&8> &8(&7&oToggle timed mode for a Kit&8)"));
        player.sendMessage(CC.translate(" &8• &c/kit gamerule voidspawn &8<&7kit&8> &8(&7&oToggle voidspawn mode for a Kit&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

}
