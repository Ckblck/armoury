package com.github.ckblck.armoury.tracker;

import com.github.ckblck.api.hooks.ApiDispatcher;
import com.github.ckblck.api.piece.ArmorPiece;
import com.github.ckblck.armoury.Armoury;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrackerController {
    private final Map<UUID, Tracker> trackedPlayers = new HashMap<>();
    private final ApiDispatcher dispatcher;

    public TrackerController(Armoury armoury) {
        this.dispatcher = armoury.getDispatcher();

        Bukkit.getOnlinePlayers().forEach(this::track); // Reload compatibility.
    }

    /**
     * Start tracking armour changes of
     * a {@link Player}.
     */

    public void track(Player player) {
        trackedPlayers.computeIfAbsent(player.getUniqueId(), pl -> new Tracker(pl, this));
    }

    /**
     * Stop tracking armour changes of
     * a {@link Player}.
     */

    public void untrack(Player player) {
        trackedPlayers.remove(player.getUniqueId());
    }

    /**
     * Returns the tracker instance of a specific
     * player.
     */

    public Tracker getTracker(Player player) {
        return trackedPlayers.get(player.getUniqueId());
    }

    protected void callApi(Player player, ArmorPiece[] modification) {
        dispatcher.dispatchInternally(player, modification);
    }

}
