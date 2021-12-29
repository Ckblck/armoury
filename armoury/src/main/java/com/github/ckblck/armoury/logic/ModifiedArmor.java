package com.github.ckblck.armoury.logic;

import com.github.ckblck.api.piece.ArmorPiece;
import com.github.ckblck.api.piece.ArmorSlot;
import com.github.ckblck.api.piece.ModificationType;
import com.github.ckblck.commons.Provider;
import com.github.ckblck.compatibility.Compatible;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Optional;

import static com.github.ckblck.api.piece.ModificationType.*;

@RequiredArgsConstructor
public class ModifiedArmor {
    private final ItemStack[] previousArmor;
    private final ItemStack[] newArmor;

    public ModificationType getModificationType(ItemStack previousPiece, ItemStack currentPiece) {
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
     * We may deduce that the {@link ModificationType} of the ArmorPiece[0] is {@link ModificationType#REMOVE},
     * the remaining ones are {@link ModificationType#UNCHANGED}.
     *
     * @throws IllegalArgumentException if both arrays are identical (this happens due to the packet being
     *                                  sent periodically OR when the player enters the server)
     */

    public ArmorPiece[] findModifiedArmor() throws IllegalArgumentException {
        ArmorPiece[] armorPieces = new ArmorPiece[newArmor.length];

        for (int i = 0; i < previousArmor.length; i++) {
            ItemStack previousPiece = previousArmor[i];
            ItemStack currentPiece = newArmor[i];

            ModificationType modificationType = getModificationType(previousPiece, currentPiece);
            Optional<ArmorSlot> foundSlot = ArmorSlot.findBySlotId(i);

            if (!foundSlot.isPresent()) {
                throw new IllegalArgumentException("Invalid slot index: " + i);
            }

            armorPieces[i] = new ArmorPiece(previousPiece, currentPiece, foundSlot.get(), modificationType);
        }

        long unchangedElements = Arrays
                .stream(armorPieces)
                .map(ArmorPiece::getModificationType)
                .filter(modificationType -> modificationType == UNCHANGED)
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
