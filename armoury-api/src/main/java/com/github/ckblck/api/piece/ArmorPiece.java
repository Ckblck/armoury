package com.github.ckblck.api.piece;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Data
@RequiredArgsConstructor
public class ArmorPiece {
    @Nullable
    private final ItemStack previousPiece;
    @Nullable
    private final ItemStack piece;
    private final ArmorSlot slot;
    private final ModificationType modificationType;

    /**
     * Returns true if the armour pieces have been damaged.
     * <p>
     * In this case both {@link #previousPiece} and {@link #piece} are of the same {@link org.bukkit.Material}
     * but have different damage values.
     */

    public boolean hasBeenDamaged() {
        return modificationType == ModificationType.DAMAGED;
    }

}
