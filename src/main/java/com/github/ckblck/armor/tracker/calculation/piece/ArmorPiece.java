package com.github.ckblck.armor.tracker.calculation.piece;

import com.github.ckblck.armor.tracker.calculation.Method;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.Nullable;

@Data
@RequiredArgsConstructor
public class ArmorPiece {
    @Nullable
    private final ItemStack previousPiece;
    @Nullable
    private final ItemStack piece;
    private final ArmorSlot slot;
    private final Method method;

    /**
     * Returns true if the armor pieces have been damaged.
     * <p>
     * In this case both {@link #previousPiece} and {@link #piece} are of the same {@link org.bukkit.Material}
     * but have different {@link Damageable#getDamage()} values.
     */

    public boolean hasBeenDamaged() {
        return method == Method.DAMAGED;
    }

}
