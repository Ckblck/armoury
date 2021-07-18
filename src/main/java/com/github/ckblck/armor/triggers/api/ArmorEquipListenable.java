package com.github.ckblck.armor.triggers.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ArmorEquipListenable {

    /**
     * Called when a player
     * has the equipment modified by wear.
     *
     * @param player    player involved
     * @param wornArmor armor which was worn
     *                  NOTE: A player will not be able to equip more than one armor
     *                  at the same time in game. Nevertheless, plugins are capable of doing such a modification.
     *                  Due to this, {@link ItemStack[]} is used.
     */

    void onWear(Player player, ItemStack[] wornArmor);

    /**
     * Called when a player
     * has the equipment modified by removal.
     *
     * @param player       player involved
     * @param removedArmor armor which was removed
     *                     NOTE: A player will not be able to un-equip more than one armor
     *                     at the same time in game. Nevertheless, plugins are capable of doing such a modification.
     *                     Due to this, {@link ItemStack[]} is used.
     */

    void onRemove(Player player, ItemStack[] removedArmor);

}
