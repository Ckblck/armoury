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
 * Holds and controls instances
 * of different API hooks.
 */

public class HooksController {
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
