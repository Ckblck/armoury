package com.github.ckblck.armor.triggers;

import com.github.ckblck.armor.tracker.calculation.ArmorModification;
import com.github.ckblck.armor.tracker.calculation.MethodCalculator;
import com.github.ckblck.armor.tracker.calculation.piece.ArmorPiece;
import com.github.ckblck.armor.triggers.api.ArmorEquipListenable;
import com.github.ckblck.armor.triggers.event.PlayerArmorChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds and controls instances
 * of different API hooks.
 * Events are also dispatched.
 */

public class Dispatcher {
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
     * NOTE: This method does not need to be called when the plugin disables.
     */

    public void unhook(Plugin plugin, ArmorEquipListenable armorEquipApi) {
        hook(plugin, armorEquipApi);
    }

    /**
     * Dispatches an armor event and calls every {@link ArmorEquipListenable}.
     * <p>
     * NO PLUGIN MUST INVOKE THIS METHOD.
     *
     * @param player       player triggering the event
     * @param modification event modification data
     */

    public void dispatch(Player player, ArmorModification modification) {
        callHooks(player, modification);
        dispatchEvent(player, modification);
    }

    private void dispatchEvent(Player player, ArmorModification modification) {
        PlayerArmorChangeEvent event = new PlayerArmorChangeEvent(player, modification);

        Bukkit.getPluginManager().callEvent(event);
    }

    private void callHooks(Player player, ArmorModification modification) {
        hooks.keySet().removeIf(plugin -> !plugin.isEnabled());

        MethodCalculator.Method method = modification.getModificationMethod();
        ArmorPiece[] modifiedArmor = modification.getModifiedArmor();

        Collection<ArmorEquipListenable> apis = hooks.values();
        boolean isWear = method == MethodCalculator.Method.WEAR;

        for (ArmorEquipListenable hook : apis) {
            if (isWear) {
                hook.onWear(player, modifiedArmor);

                return;
            }

            hook.onRemove(player, modifiedArmor);
        }
    }


}
