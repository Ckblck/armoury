package com.github.ckblck.armor.tracker.calculation.piece;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class ArmorPiece {
    private final ItemStack piece;
    private final ArmorSlot slot;

    public ArmorPiece(ItemStack piece) {
        this.piece = piece;
        this.slot = ArmorSlot.findByMaterial(piece.getType());
    }

}
