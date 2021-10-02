package xyz.refinedev.practice.party;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.duel.DuelRequest;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.task.PartyInviteExpireTask;
import xyz.refinedev.practice.task.PartyPublicTask;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
public class Party extends Team {

    private final Array plugin = Array.getInstance();

    @Getter private static List<Party> parties = new ArrayList<>();

    private final Map<UUID, String> kits;
    private final List<PartyInvite> invites;
    private final List<Player> banned;

    private int limit = 10;

    private boolean isPublic;
    private boolean inTournament;
    private boolean disbanded;

    /**
     * Create a new Party for a Player
     * and assign him as the leader
     *
     * @param player The Leader of the party
     */
    public Party(Player player) {
        super(new TeamPlayer(player.getUniqueId(), player.getName()));

        if (player.hasPermission("array.donator")) {
            this.limit = 50;
        }

        this.isPublic = false;
        this.invites = new ArrayList<>();
        this.banned = new ArrayList<>();
        this.kits = new HashMap<>();
        this.kits.put(player.getUniqueId(), getRandomClass());

        parties.add(this);
    }

    /**
     * Start the essential party tasks
     */
    public static void preLoad() {
        TaskUtil.runTimerAsync(new PartyInviteExpireTask(), 100L, 100L);
        TaskUtil.runTimerAsync(new PartyPublicTask(), 1000L, 1000L);
    }

    /**
     * Update the party's privacy type
     *
     * @param privacy True or false
     */
    public void setPublic(boolean privacy) {
        this.isPublic = privacy;
        this.broadcast(Locale.PARTY_PRIVACY.toString().replace("<privacy>", (isPublic ? "&aOpen" : "&eClose")));
    }

    public String getPrivacy() {
        return (isPublic ? "&aOpen" : "&eClose");
    }

    /**
     * Get a Party Invite from a player's UUID
     *
     * @param uuid The Player's UUID
     * @return {@link PartyInvite}
     */
    public PartyInvite getInvite(final UUID uuid) {
        for ( PartyInvite invite : this.invites )
            if (invite.getUuid().equals(uuid)) {
                if (invite.hasExpired()) {
                    return null;
                }
                return invite;
            }
        return null;
    }

    /**
     * Invite a specific player to the party
     *
     * @param target The player being in invited
     */
    public void invite(Player target) {
        invites.add(new PartyInvite(target.getUniqueId()));

        List<String> strings = new ArrayList<>();

        strings.add(Locale.PARTY_INVITED.toString().replace("<leader>", getLeader().getUsername()));
        strings.add(Locale.PARTY_CLICK_TO_JOIN.toString());

        strings.forEach(string -> new Clickable(string, Locale.PARTY_INVITE_HOVER.toString(), "/party join " + getLeader().getUsername()).sendToPlayer(target));

        this.broadcast(Locale.PARTY_PLAYER_INVITED.toString().replace("<invited>", target.getName()));
    }

    /**
     * Ban the targeted player from the party
     *
     * @param target The player being banned
     */
    public void ban(final Player target) {
        this.banned.add(target);
    }

    /**
     * Unban the targeted player form the party
     *
     * @param target The player being unbanned
     */
    public void unban(final Player target) {
        this.broadcast(Locale.PARTY_UNBANNED.toString().replace("<target>", target.getName()));
        this.banned.remove(target);
    }

    /**
     * Execute party join for the player
     *
     * @param player The player joining the party
     */
    public void join(final Player player) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        profile.setParty(this);

        this.kits.put(player.getUniqueId(), getRandomClass());

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);

        this.invites.removeIf(invite -> invite.getUuid().equals(player.getUniqueId()));
        this.getTeamPlayers().add(new TeamPlayer(player));

        this.broadcast(Locale.PARTY_PLAYER_JOINED.toString().replace("<joiner>", player.getName()));

        for (Player teamPlayer : this.getPlayers()) {
            Profile teamProfile = plugin.getProfileManager().getByUUID(teamPlayer.getUniqueId());

            plugin.getProfileManager().handleVisibility(teamProfile, player);
            plugin.getProfileManager().refreshHotbar(teamProfile);
        }

        if (this.isFighting()) {
            Match match = this.getMatch();
            if (match == null) return;

            match.addSpectator(player, null);
        }
    }

    /**
     * Execute party leave tasks for the player leaving
     *
     * @param player The player leaving
     * @param kick If the leave is a forced kick or not
     */
    public void leave(Player player, boolean kick) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        profile.setParty(null);

        this.getTeamPlayers().removeIf(member -> member.getUniqueId().equals(player.getUniqueId()));
        this.getPlayers().removeIf(member -> member.getUniqueId().equals(player.getUniqueId()));
        this.kits.remove(player.getUniqueId());

        if (kick) {
            this.broadcast(Locale.PARTY_PLAYER_KICKED.toString().replace("<leaver>", player.getName()));
        } else {
            this.broadcast(Locale.PARTY_PLAYER_LEFT.toString().replace("<leaver>", player.getName()));
        }

        if (profile.isInLobby() || profile.isInQueue()) {
            plugin.getProfileManager().handleVisibility(profile);
            plugin.getProfileManager().refreshHotbar(profile);
        }

        /*
         * If the player is in Fight then reset and teleport them to spawn
         */
        if (profile.isInFight()) {
            profile.getMatch().handleDeath(player, null, true);

            if (profile.isSpectating()) {
                profile.getMatch().removeSpectator(player);
            }

            profile.setState(ProfileState.IN_LOBBY);
            profile.setMatch(null);

            plugin.getProfileManager().teleportToSpawn(profile);
        }

        for (Player teamPlayer : this.getPlayers()) {
            Profile teamProfile = plugin.getProfileManager().getByUUID(teamPlayer.getUniqueId());

            plugin.getProfileManager().handleVisibility(teamProfile, player);
            plugin.getProfileManager().refreshHotbar(teamProfile);
        }

        plugin.getNameTagHandler().reloadPlayer(player);
        plugin.getNameTagHandler().reloadOthersFor(player);
    }

    /**
     * Make the targeted player, the leader of the party
     *
     * @param player The Original Leader of the Party
     * @param target The New Leader of the Party
     */
    public void leader(Player player, Player target) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        Profile targetProfile = plugin.getProfileManager().getByUUID(target.getUniqueId());

        for (TeamPlayer teamPlayer : this.getTeamPlayers()) {
            if (teamPlayer.getPlayer().equals(targetProfile.getPlayer())) {
                targetProfile.getParty().setLeader(teamPlayer);
            }
        }

        this.broadcast(Locale.PARTY_PROMOTED.toString().replace("<promoted>", target.getName()));

        if (player.isOnline() && profile.isInLobby()) {
            plugin.getProfileManager().refreshHotbar(profile);
        }
        if (targetProfile.isInLobby()) {
            plugin.getProfileManager().refreshHotbar(targetProfile);
        }
    }

    /**
     * Execute tasks for disbanding the party
     */
    public void disband() {
        this.broadcast(Locale.PARTY_DISBANDED.toString());

        Profile leaderProfile = plugin.getProfileManager().getByUUID(this.getLeader().getUniqueId());
        leaderProfile.getSentDuelRequests().values().removeIf(DuelRequest::isParty);

        for ( Player player : this.getPlayers() ) {
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            profile.setParty(null);

            if (profile.isInFight()) {
                profile.getMatch().handleDeath(player, null, true);
            }

            if (profile.isInLobby() || profile.isInQueue()) {
                plugin.getNameTagHandler().reloadPlayer(player);
                plugin.getNameTagHandler().reloadOthersFor(player);
            }
        }

        parties.remove(this);
        this.disbanded = true;
    }

    /**
     * Send Party information message to the specified player
     *
     * @param player The player receiving the information
     */
    public void sendInformation(Player player) {
        String members = "None";

        if (this.getPlayers().size() != 1) {
            members = this.getPlayers().stream().map(Player::getName).collect(Collectors.joining(", "));
        }

        String finalMembers = members;
        Locale.PARTY_INFO.toList().forEach(string -> {
            String main = string.replace("<party_leader_name>", this.getLeader().getUsername())
                                .replace("<party_privacy>", getPrivacy())
                                .replace("<party_members_formatted>", CC.GRAY + "(" + (this.getTeamPlayers().size() - 1) + ") " + finalMembers)
                                .replace("<party_members>", String.valueOf(getTeamPlayers().size()));

            player.sendMessage(CC.translate(main));
        });
    }

    public String getName() {
        return this.getLeader().getUsername() + "'s Party";
    }

    public boolean isFighting() {
        return this.getPlayers().stream().map(plugin.getProfileManager()::getByPlayer).anyMatch(profile -> profile.isInFight() || profile.isInTournament());
    }

    public Match getMatch() {
        return this.getPlayers().stream().map(plugin.getProfileManager()::getByPlayer).filter(profile -> profile.isInFight() || profile.isInTournament()).map(Profile::getMatch).findAny().orElse(null);
    }

    public boolean isMember(UUID uuid) {
       return this.getPlayers().stream().map(Player::getUniqueId).anyMatch(id -> id.equals(uuid));
    }

    public String getRandomClass() {
        List<String> classes = Arrays.asList(
                "Diamond",
                "Bard",
                "Archer",
                "Rogue"
        );
        Collections.shuffle(classes);
        return classes.get(0);
    }
}
