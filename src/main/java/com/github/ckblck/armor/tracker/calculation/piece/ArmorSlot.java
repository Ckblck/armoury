package com.github.ckblck.armor.tracker.calculation.piece;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.function.Predicate;

@RequiredArgsConstructor
public enum ArmorSlot {
    HEAD(0, (piece) -> piece.name().endsWith("_HELMET")),
    CHEST(1, (piece) -> piece.name().endsWith("_CHESTPLATE")),
    LEGGINGS(2, (piece) -> piece.name().endsWith("_LEGGINGS")),
    BOOT(3, (piece) -> piece.name().endsWith("_BOOTS"));

    private static final ArmorSlot[] VALUES = values();

    @Getter
    private final int slotId;
    private final Predicate<Material> condition;

    public static ArmorSlot findByMaterial(Material material) {
        for (ArmorSlot armorSlot : VALUES) {
            boolean meetsCondition = armorSlot.condition.test(material);

            if (meetsCondition) {
                return armorSlot;
            }

        }

        throw new IllegalArgumentException("Could not find ArmorSlot for material: " + material);
    }

    public static ArmorSlot findBySlot(int slot) {
        for (ArmorSlot armorSlot : VALUES) {
            boolean sameId = armorSlot.slotId == slot;

            if (sameId) {
                return armorSlot;
            }

        }

        throw new IllegalArgumentException("Could not find ArmorSlot for slot: " + slot);
    }

}
