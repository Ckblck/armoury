package com.ckblck.armor.utils;

import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodCalculator {

    /**
     * Finds the armors that have been modified
     * by differencing it with the last copy.
     * <p>
     * Having an old copy of (DIAMOND_HELMET, AIR, AIR, AIR)
     * And a current copy of (AIR, AIR, AIR, AIR),
     * We may deduce that the {@link Method} is {@link Method#REMOVE},
     * and that the modified armor is {@code ItemStack[] modifiedArmor = {new ItemStack(Material.DIAMOND_HELMET)};}
     * <p>
     * NOTE: Programmatic armor modification will be labeled as {@link Method#WEAR}.
     *
     * @param savedArmor   previous armor the player was wearing
     * @param currentArmor current armor the player is wearing
     */

    public ArmorModification findModifiedArmor(ItemStack[] savedArmor, ItemStack[] currentArmor) {
        Set<ItemStack> savedArmorFiltered = filterAirOrNull(savedArmor);
        Set<ItemStack> currentArmorFiltered = filterAirOrNull(currentArmor);

        Method method = findMethod(savedArmorFiltered, currentArmorFiltered);
        Sets.SetView<ItemStack> difference;

        if (method == Method.WEAR) {
            difference = Sets.difference(currentArmorFiltered, savedArmorFiltered);
        } else {
            difference = Sets.difference(savedArmorFiltered, currentArmorFiltered);
        }

        ItemStack[] modifiedArmor = difference.toArray(new ItemStack[0]);

        if (modifiedArmor.length == 0) {
            method = Method.CORRUPT;
        }

        return new ArmorModification(method, modifiedArmor);
    }

    public Method findMethod(Collection<ItemStack> savedArmor, Collection<ItemStack> currentArmor) {
        int savedArmorLength = savedArmor.size();
        int currentArmorLength = currentArmor.size();

        if (savedArmorLength > currentArmorLength) {
            return Method.REMOVE;
        }

        return Method.WEAR;
    }

    private Set<ItemStack> filterAirOrNull(ItemStack[] array) {
        return Arrays.stream(array)
                .filter(Objects::nonNull)
                .filter(item -> item.getType() != Material.AIR)
                .collect(Collectors.toSet());
    }

    public enum Method {
        WEAR,
        REMOVE,
        CORRUPT // Only used internally. Will not be used in the API.
    }
}
