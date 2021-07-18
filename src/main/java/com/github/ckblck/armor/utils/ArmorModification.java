package com.github.ckblck.armor.utils;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

/**
 * Data class that holds
 * the method and the modified armors
 * of an armor equip event.
 */

@Data
@RequiredArgsConstructor
public class ArmorModification {
    private final MethodCalculator.Method modificationMethod;
    private final ItemStack[] modifiedArmor;
}
