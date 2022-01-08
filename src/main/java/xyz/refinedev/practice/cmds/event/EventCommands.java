package xyz.refinedev.practice.cmds.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventState;
import xyz.refinedev.practice.event.menu.EventSelectMenu;
import xyz.refinedev.practice.event.menu.EventTeamMenu;
import xyz.refinedev.practice.managers.EventManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.other.Cooldown;

import java.util.stream.Collectors;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/29/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class EventCommands {

    private final Array plugin;
    private final EventManager eventManager;

    @Command(name = "", aliases = {"menu, host"}, desc = "Open Event Host Menu")
    public void host(@Sender Player player) {
        new EventSelectMenu().openMenu(player);
    }

    @Command(name = "help", desc = "View Event Commands")
    public void help(@Sender CommandSender sender) {
        Locale.EVENT_HELP.toList().forEach(sender::sendMessage);
    }

    @Command(name = "teamselect", aliases = "teams", desc = "Choose a Team for your Event")
    public void teamSelect(@Sender Player player) {
        final Profile profile = plugin.getProfileManager().getProfileByPlayer(player);
        final Event activeEvent = eventManager.getActiveEvent();

        if (activeEvent == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }
        if (!profile.isInEvent() || !activeEvent.getPlayers().contains(player)) {
            player.sendMessage(Locale.ERROR_NOTPARTOF.toString());
            return;
        }
        if (!activeEvent.isTeam()) {
            player.sendMessage(Locale.EVENT_NOT_TEAM.toString());
            return;
        }
        new EventTeamMenu(activeEvent).openMenu(player);
    }

    /*@Command(name = "host", aliases = "start", usage = "<event>", desc = "Host an Event")
    public void host(@Sender Player player, EventType type) {
        if (eventManager.getActiveEvent() != null) {
            player.sendMessage(Locale.EVENT_ON_GOING.toString());
            return;
        }
        if (!eventManager.getCooldown().hasExpired()) {
            player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<expire_time>", eventManager.getCooldown().getTimeLeft()));
            return;
        }

        Bukkit.getOnlinePlayers().stream().map(plugin.getProfileManager()::getProfileByPlayer).filter(profile -> profile.isInLobby() && !profile.getKitEditor().isActive()).forEach(Profile::refreshHotbar);
    }*/

    @Command(name = "cancel", aliases = "stop", desc = "Cancel an ongoing event")
    @Require("array.event.admin")
    public void cancel(@Sender CommandSender sender) {
        final Event event = Array.getInstance().getEventManager().getActiveEvent();
        if (event == null) {
            sender.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }

        plugin.getProfileManager().getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(plugin.getProfileManager()::refreshHotbar);
        event.handleEnd();
    }

    @Command(name = "join", aliases = "participate", desc = "Join an active event")
    public void join(@Sender Player player) {
        final Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        final Event event =  eventManager.getActiveEvent();

        if (profile.isBusy() || profile.hasParty()) {
            player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
            return;
        }
        if (event == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }
        if (event.getState() != EventState.WAITING) {
            player.sendMessage(Locale.EVENT_ALREADY_STARTED.toString());
            return;
        }
        if (event.getEventManager().isUnfinished(event)) {
            player.sendMessage(Locale.EVENT_NOT_SETUP.toString());
            event.handleEnd();
            return;
        }

        event.handleJoin(player);
    }

    @Command(name = "leave", aliases = "quit", desc = "Leave an active event")
    public void leave(@Sender Player player) {
        final Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        final Event event = eventManager.getActiveEvent();
        if (event == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }
        if (!profile.isInEvent() || !event.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Locale.ERROR_NOTPARTOF.toString());
            return;
        }
        event.handleLeave(player);
    }

    @Command(name = "forcestart", desc = "Force start the on-going event")
    @Require("array.event.admin")
    public void forceStart(@Sender CommandSender player) {
        final Event event = eventManager.getActiveEvent();
        if (event == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }
        if (event.getState() != EventState.WAITING) {
            player.sendMessage(Locale.EVENT_ALREADY_STARTED.toString());
            return;
        }
        event.onRound();
    }

    @Command(name = "info", aliases = "information", desc = "View Information about the current event")
    public void info(@Sender CommandSender sender) {
        final Event event = eventManager.getActiveEvent();
        if (event == null) {
            sender.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }

        Locale.EVENT_INFO.toList().stream().map(line -> {
            return line
                    .replace("<event_state>", event.getState().name())
                    .replace("<event_host>", event.getHost().getUsername())
                    .replace("<event_alive_players>", String.valueOf(event.getRemainingPlayers().size()))
                    .replace("<event_max_players>", String.valueOf(event.getMaxPlayers()))
                    .replace("<event_type>", event.getType().getName())
                    .replace("<event_name>", event.getName());

        }).collect(Collectors.toList()).forEach(sender::sendMessage);
    }

    @Command(name = "cooldown", aliases = "resetcooldown", desc = "Reset the event cooldown")
    @Require("array.event.admin")
    public void cooldown(@Sender CommandSender sender) {
        eventManager.setCooldown(new Cooldown(0));
        sender.sendMessage(Locale.EVENT_COOLDOW_RESET.toString());
    }
}
