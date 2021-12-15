package com.github.ckblck.armor.tracker;

import com.github.ckblck.armor.Bootstrap;
import com.github.ckblck.armor.hooks.ApiDispatcher;
import com.github.ckblck.armor.tracker.calculation.piece.ArmorPiece;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrackerController {
    private final Map<UUID, Tracker> trackedPlayers = new HashMap<>();
    private final ApiDispatcher dispatcher;

    public TrackerController(Bootstrap bootstrap) {
        this.dispatcher = bootstrap.getDispatcher();

        Bukkit.getOnlinePlayers().forEach(this::track); // Reload compatibility.
    }

    /**
     * Start tracking armor changes of
     * a {@link Player}.
     */

    public void track(Player player) {
        trackedPlayers.computeIfAbsent(player.getUniqueId(), pl -> new Tracker(pl, this));
    }

    /**
     * Stop tracking armor changes of
     * a {@link Player}.
     */

    public void untrack(Player player) {
        trackedPlayers.remove(player.getUniqueId());
    }

    public Tracker getTracker(Player player) {
        return trackedPlayers.get(player.getUniqueId());
    }

    protected void callApi(Player player, ArmorPiece[] modification) {
        dispatcher.dispatchInternally(player, modification);
    }

}
