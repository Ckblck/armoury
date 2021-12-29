package com.github.ckblck.api.piece;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@RequiredArgsConstructor
public enum ArmorSlot {
    HEAD(0, 5, EnumWrappers.ItemSlot.HEAD, (piece) ->
            piece.name().endsWith("HELMET")
                    || piece.name().endsWith("SKULL")
                    || piece.name().endsWith("HEAD")
    ),
    CHEST(1, 6, EnumWrappers.ItemSlot.CHEST, (piece) -> piece.name().endsWith("CHESTPLATE") || piece == Material.ELYTRA),
    LEGS(2, 7, EnumWrappers.ItemSlot.LEGS, (piece) -> piece.name().endsWith("LEGGINGS")),
    BOOT(3, 8, EnumWrappers.ItemSlot.FEET, (piece) -> piece.name().endsWith("BOOTS"));

    private static final ArmorSlot[] VALUES = values();

    @Getter
    private final int slotId;
    @Getter
    private final int rawSlot;
    @Getter
    private final EnumWrappers.ItemSlot itemSlot;

    private final Predicate<Material> condition;

    public static boolean isLeftClickWearable(@NotNull ItemStack item) {
        String itemName = item.getType().name();

        return !(itemName.endsWith("HEAD") || itemName.endsWith("SKULL"));
    }

    /**
     * Gets the slot of a specific material.
     *
     * @param material material to get the slot from
     * @return an Optional with the specific armor slot, or an empty Optional if no slot was found.
     */

    public static Optional<ArmorSlot> findSlot(Material material) {
        return Arrays
                .stream(VALUES)
                .filter(armorSlot -> armorSlot.condition.test(material))
                .findAny();
    }

    /**
     * Gets the slot of a specific item.
     *
     * @param itemStack item to get the slot from
     * @return an Optional with the specific armour slot, or an empty Optional if no slot was found.
     */

    public static Optional<ArmorSlot> findSlot(ItemStack itemStack) {
        return findSlot(itemStack.getType());
    }

    /**
     * Checks if an itemStack is wearable.
     *
     * @param itemStack item to verify
     * @return true if such item can be worn
     */

    public static boolean isWearable(ItemStack itemStack) {
        return findSlot(itemStack).isPresent();
    }

    /**
     * Gets the armor slot given a slot id.
     *
     * @param slotId {@link #getSlotId()}
     * @return an Optional with the specific armor slot, or an empty Optional if no slot was found.
     */

    public static Optional<ArmorSlot> findBySlotId(int slotId) {
        return Arrays
                .stream(VALUES)
                .filter(armorSlot -> armorSlot.slotId == slotId)
                .findAny();
    }

    /**
     * Gets the armor slot given a slot id.
     *
     * @param rawSlot {@link InventoryClickEvent#getRawSlot()}
     * @return an Optional with the specific armor slot, or an empty Optional if no slot was found.
     */

    public static Optional<ArmorSlot> findByRawSlot(int rawSlot) {
        return Arrays
                .stream(VALUES)
                .filter(armorSlot -> armorSlot.rawSlot == rawSlot)
                .findAny();
    }

    /**
     * Checks whether an item is a
     * conventional armour piece, such as a
     * HELMET, CHESTPLATE, LEGGING or BOOT.
     * Skulls, heads and elytras are not considered conventional pieces.
     *
     * @param piece piece to check
     * @return true if such piece is HELMET, CHESTPLATE, LEGGING or BOOT
     */

    public static boolean isConventionalPiece(ItemStack piece) {
        String pieceName = piece.getType().name();

        return pieceName.endsWith("HELMET")
                || pieceName.endsWith("CHESTPLATE")
                || pieceName.endsWith("LEGGINGS")
                || pieceName.endsWith("BOOTS");
    }

}
