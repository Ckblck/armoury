package com.github.ckblck.armoury.tracker.calculation.piece;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.function.Predicate;

@RequiredArgsConstructor
public enum ArmorSlot {
    HEAD(0, EnumWrappers.ItemSlot.HEAD, (piece) -> piece.name().endsWith("_HELMET")),
    CHEST(1, EnumWrappers.ItemSlot.CHEST, (piece) -> piece.name().endsWith("_CHESTPLATE")),
    LEGS(2, EnumWrappers.ItemSlot.LEGS, (piece) -> piece.name().endsWith("_LEGGINGS")),
    BOOT(3, EnumWrappers.ItemSlot.FEET, (piece) -> piece.name().endsWith("_BOOTS"));

    private static final ArmorSlot[] VALUES = values();

    @Getter
    private final int slotId;
    @Getter
    private final EnumWrappers.ItemSlot itemSlot;
    private final Predicate<Material> condition;

    /**
     * Gets the slot of a specific material.
     *
     * @param material material to get the slot from
     * @throws IllegalArgumentException if no slot was found
     */

    public static ArmorSlot findByMaterial(Material material) throws IllegalArgumentException {
        for (ArmorSlot armorSlot : VALUES) {
            boolean meetsCondition = armorSlot.condition.test(material);

            if (meetsCondition) {
                return armorSlot;
            }

        }

        throw new IllegalArgumentException("Could not find ArmorSlot for material: " + material);
    }

    /**
     * Gets the armor slot given a slot id.
     *
     * @throws IllegalArgumentException if no slot was found
     */

    public static ArmorSlot findBySlot(int slot) throws IllegalArgumentException {
        for (ArmorSlot armorSlot : VALUES) {
            boolean sameId = armorSlot.slotId == slot;

            if (sameId) {
                return armorSlot;
            }

        }

        throw new IllegalArgumentException("Could not find ArmorSlot for slot: " + slot);
    }

}
