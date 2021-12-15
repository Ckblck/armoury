package com.github.ckblck.armor.hooks;

import com.github.ckblck.armor.event.PlayerArmorChangeEvent;
import com.github.ckblck.armor.hooks.api.ArmorEquipListenable;
import com.github.ckblck.armor.tracker.calculation.piece.ArmorPiece;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds and controls instances
 * of different API hooks.
 * <p>
 * Events are also dispatched from this class.
 */

public class ApiDispatcher {
    private final Map<Plugin, ArmorEquipListenable> hooks = new HashMap<>();

    /**
     * Adds a {@link ArmorEquipListenable} implementation so that it starts
     * listening for modifications.
     */

    public void hook(Plugin plugin, ArmorEquipListenable armorEquipApi) {
        hooks.put(plugin, armorEquipApi);
    }

    /**
     * Removes a {@link ArmorEquipListenable} implementation so that it stops
     * listening for modifications.
     * <p>
     * Note: This method does not need to be called when the plugin disables.
     */

    public void unhook(Plugin plugin) {
        hooks.remove(plugin);
    }

    /**
     * Dispatches an armor event and calls every {@link ArmorEquipListenable}.
     * <p>
     * Note: NO PLUGIN MUST INVOKE THIS METHOD.
     *
     * @param player      player triggering the event
     * @param armorPieces previous and current pieces of armor
     */

    public void dispatchInternally(Player player, ArmorPiece[] armorPieces) {
        callHooks(player, armorPieces);
        dispatchEvent(player, armorPieces);
    }

    private void dispatchEvent(Player player, ArmorPiece[] armorPieces) {
        PlayerArmorChangeEvent event = new PlayerArmorChangeEvent(player, armorPieces);

        Bukkit.getPluginManager().callEvent(event);
    }

    private void callHooks(Player player, ArmorPiece[] armorPieces) {
        hooks.keySet().removeIf(plugin -> !plugin.isEnabled());

        Collection<ArmorEquipListenable> apis = hooks.values();

        for (ArmorEquipListenable hook : apis) {
            hook.onModification(player, armorPieces);
        }
    }


}
