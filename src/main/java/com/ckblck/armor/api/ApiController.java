package com.ckblck.armor.api;

import com.ckblck.armor.utils.ArmorModification;
import com.ckblck.armor.utils.MethodCalculator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds instances of different
 * API hooks.
 */

public class ApiController {
    private final Map<Plugin, ArmorEquipApi> hooks = new HashMap<>();

    /**
     * Adds a {@link ArmorEquipApi} implementation so that it starts
     * listening for modifications.
     */

    public void hook(Plugin plugin, ArmorEquipApi armorEquipApi) {
        hooks.put(plugin, armorEquipApi);
    }

    /**
     * Removes a {@link ArmorEquipApi} implementation so that it stops
     * listening for modifications.
     * <p>
     * NOTE: This method does not need to be called when the plugin disables.
     */

    public void unhook(Plugin plugin, ArmorEquipApi armorEquipApi) {
        hook(plugin, armorEquipApi);
    }

    /**
     * Calls every registered hook
     * as soon as an armor event is triggered.
     * <p>
     * NO PLUGIN MUST INVOKE THIS METHOD.
     *
     * @param player       player triggering the event
     * @param modification event modification data
     */

    public void callHooks(Player player, ArmorModification modification) {
        hooks.keySet().removeIf(plugin -> !plugin.isEnabled());

        MethodCalculator.Method method = modification.getModificationMethod();
        ItemStack[] modifiedArmor = modification.getModifiedArmor();

        Collection<ArmorEquipApi> apis = hooks.values();
        boolean isWear = method == MethodCalculator.Method.WEAR;

        for (ArmorEquipApi hook : apis) {
            if (isWear) {
                hook.onWear(player, modifiedArmor);

                return;
            }

            hook.onRemove(player, modifiedArmor);
        }

    }


}
