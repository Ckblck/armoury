package com.github.ckblck.armor.triggers.api;

import com.github.ckblck.armor.tracker.calculation.piece.ArmorPiece;
import org.bukkit.entity.Player;

public interface ArmorEquipListenable {

    /**
     * Called when a player
     * has the equipment modified by wear.
     *
     * @param player    player involved
     * @param wornArmor armor which was worn
     *                  NOTE: A player will not be able to equip more than one armor
     *                  at the same time in game. Nevertheless, plugins are capable of doing such a modification.
     *                  Due to this, an array of {@link ArmorPiece} is used.
     */

    void onWear(Player player, ArmorPiece[] wornArmor);

    /**
     * Called when a player
     * has the equipment modified by removal.
     *
     * @param player       player involved
     * @param removedArmor armor which was removed
     *                     NOTE: A player will not be able to un-equip more than one armor
     *                     at the same time in game. Nevertheless, plugins are capable of doing such a modification.
     *                     Due to this, an array of {@link ArmorPiece} is used.
     */

    void onRemove(Player player, ArmorPiece[] removedArmor);

}
