package com.github.ckblck.armoury.tracker.calculation;

import com.github.ckblck.armoury.tracker.calculation.piece.ArmorPiece;
import com.github.ckblck.armoury.tracker.calculation.piece.ArmorSlot;
import com.github.ckblck.commons.Provider;
import com.github.ckblck.compatibility.Compatible;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Optional;

public enum Method {
    WEAR,
    REMOVE,
    DAMAGED,
    UNCHANGED;

    public static Method findMethod(ItemStack previousPiece, ItemStack currentPiece) {
        // Player didn't have his armour piece modified, this can occur when using Spigot-API methods.
        if (previousPiece == null && currentPiece == null) {
            return UNCHANGED;
        }

        // Player had AIR, he now has an armour piece.
        if (previousPiece == null) {
            return WEAR;
        }

        // Player had an armour piece, he now has AIR.
        if (currentPiece == null) {
            return REMOVE;
        }

        // Player has had his armour overwritten with the same armour piece, this can occur when using Spigot-API methods.
        if (previousPiece.isSimilar(currentPiece)) {
            return UNCHANGED;
        } else {
            // Player has had his armor overwritten with a different armor piece, this can occur when using Spigot-API methods.
            // OR: He was attacked and suffered from durability damage.

            Compatible<?> compatible = Provider.getInstance().getCompatible();

            int previousPieceDamage = compatible.getDamage(previousPiece);
            int currentPieceDamage = compatible.getDamage(currentPiece);

            if (previousPieceDamage != currentPieceDamage) {
                return DAMAGED;
            }

            if (previousPiece.getType() == currentPiece.getType()) {
                return UNCHANGED;
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
     * We may deduce that the {@link Method} of the ArmorPiece[0] is {@link Method#REMOVE},
     * the remaining ones are {@link Method#UNCHANGED}.
     *
     * @param previousArmor previous armor the player was wearing
     * @param currentArmor  current armor the player is wearing
     * @throws IllegalArgumentException if both arrays are identical (this happens due to the packet being
     *                                  sent periodically OR when the player enters the server)
     */

    public static ArmorPiece[] findModifiedArmor(ItemStack[] previousArmor, ItemStack[] currentArmor)
            throws IllegalArgumentException {
        ArmorPiece[] armorPieces = new ArmorPiece[currentArmor.length];

        for (int i = 0; i < previousArmor.length; i++) {
            ItemStack previousPiece = previousArmor[i];
            ItemStack currentPiece = currentArmor[i];

            Method method = findMethod(previousPiece, currentPiece);
            Optional<ArmorSlot> foundSlot = ArmorSlot.findBySlotId(i);

            if (!foundSlot.isPresent()) {
                throw new IllegalArgumentException("Invalid slot index: " + i);
            }

            armorPieces[i] = new ArmorPiece(previousPiece, currentPiece, foundSlot.get(), method);
        }

        long unchangedElements = Arrays.stream(armorPieces)
                .filter(armorPiece -> armorPiece.getMethod() == UNCHANGED)
                .count();

        // Packet was not sent due to a player modification,
        // this packet is sent periodically by players every 1-5 minutes.
        // OR: when the player enters the server.
        if (unchangedElements == armorPieces.length) {
            throw new IllegalArgumentException("Periodical UPDATE_ATTRIBUTES packet.");
        }

        return armorPieces;
    }

}
