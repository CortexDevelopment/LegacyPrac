package xyz.refinedev.practice.queue;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.util.other.TimeUtil;

import java.util.*;

@Getter
public class Queue {

    private final Array plugin;

    private final LinkedList<QueueProfile> players = new LinkedList<>();

    private final UUID uuid;
    private final Kit kit;
    private final QueueType type;

    /**
     * Main constructor for {@link Queue}
     *
     * @param kit The kit of the queue
     * @param type The type of the queue
     */
    public Queue(Array plugin, Kit kit, QueueType type) {
        this.plugin = plugin;
        this.kit = kit;
        this.type = type;
        this.uuid = UUID.randomUUID();

        plugin.getQueueManager().getQueueMap().put(kit, this);
    }

    /**
     * Get amount of players in fight from a certain
     * queue
     *
     * @return amount of players in fight with the queue
     */
    public int getInFights() {
        int i = 0;

        for ( Match match : Match.getMatches()) {
            if (match.getQueue() == null || !match.getQueue().equals(this)) continue;
            if (!match.isFighting() && !match.isStarting()) continue;

            i += match.getTeamPlayers().size();
        }
        return i;
    }

    /**
     * Returns the Queue's Formatted Name
     *
     * @return Formatted Name in {@link String}
     */
    public String getQueueName() {
        switch (type) {
            case RANKED: return "Ranked " + kit.getDisplayName();
            case UNRANKED: return "Unranked " + kit.getDisplayName();
            case CLAN: return "Clan " + kit.getDisplayName();
            default: return kit.getDisplayName() + " Queue";
        }
    }
}
