package com.github.ckblck.armoury.tracker.calculation;

import com.github.ckblck.armoury.tracker.calculation.piece.ArmorPiece;
import com.github.ckblck.armoury.tracker.calculation.piece.ArmorSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public enum Method {
    WEAR,
    REMOVE,
    DAMAGED,
    UNCHANGED;

    public static Method findMethod(ItemStack previousPiece, ItemStack currentPiece) {
        // Player didn't have his armor piece modified, this can occur when using Spigot-API methods.
        if (previousPiece == null && currentPiece == null) {
            return UNCHANGED;
        }

        // Player had AIR, he now has an armor piece.
        if (previousPiece == null) {
            return WEAR;
        }

        // Player had an armor piece, he now has AIR.
        if (currentPiece == null) {
            return REMOVE;
        }

        // Player has had his armor overwritten with the same armor piece, this can occur when using Spigot-API methods.
        if (previousPiece.isSimilar(currentPiece)) {
            return UNCHANGED;
        } else {
            // Player has had his armor overwritten with a different armor piece, this can occur when using Spigot-API methods.
            // OR: He was attacked and suffered from durability damage.

            if (previousPiece.hasItemMeta() && currentPiece.hasItemMeta()) {
                Damageable previousPieceItemMeta = (Damageable) previousPiece.getItemMeta();
                Damageable currentPieceItemMeta = (Damageable) currentPiece.getItemMeta();

                assert previousPieceItemMeta != null;
                assert currentPieceItemMeta != null;

                int previousItemDamage = previousPieceItemMeta.getDamage();
                int currentItemDamage = currentPieceItemMeta.getDamage();

                if (previousItemDamage != currentItemDamage) {
                    return DAMAGED;
                }

                if (previousPiece.getType() == currentPiece.getType()) {
                    return UNCHANGED;
                }
            }

            return WEAR;
        }
    }

    /**
     * Finds the armor pieces that have been modified
     * by differencing it with the previous copy.
     * <p>
     * Having an old copy of (DIAMOND_HELMET, AIR, AIR, AIR)
     * And a current copy of (AIR, AIR, AIR, AIR),
     * We may deduce that the {@link Method} of the ArmorPiece[0] is {@link Method#REMOVE}, the remaining ones are {@link Method#UNCHANGED}.
     *
     * @param previousArmor previous armor the player was wearing
     * @param currentArmor  current armor the player is wearing
     */

    public static ArmorPiece[] findModifiedArmor(ItemStack[] previousArmor, ItemStack[] currentArmor) {
        ArmorPiece[] armorPieces = new ArmorPiece[currentArmor.length];

        for (int i = 0; i < previousArmor.length; i++) {
            ItemStack previousPiece = previousArmor[i];
            ItemStack currentPiece = currentArmor[i];
            Method method = findMethod(previousPiece, currentPiece);

            armorPieces[i] = new ArmorPiece(previousPiece, currentPiece, ArmorSlot.findBySlot(i), method);
        }

        return armorPieces;
    }

}
