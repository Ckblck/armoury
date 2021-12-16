package com.github.ckblck.armoury.tracker;

import com.github.ckblck.armoury.tracker.calculation.Method;
import com.github.ckblck.armoury.tracker.calculation.piece.ArmorPiece;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * Keeps track of the armor changes
 * of a specific player.
 */

@ToString
@Getter
public class Tracker {
    private final UUID player;
    private final TrackerController trackerController;

    private ItemStack[] savedArmor;

    public Tracker(UUID player, TrackerController trackerController) {
        this.player = player;
        this.trackerController = trackerController;

        setSavedArmor(getCurrentArmor());
    }

    /**
     * Returns the armor the player is currently wearing.
     * <p>
     * NOTE: {@link PlayerInventory#getArmorContents()} is reversed,
     * this method handles this so that the order is correct.
     *
     * @return an ordered array from helmet to boots.
     */

    public ItemStack[] getCurrentArmor() {
        Optional<Player> playerOpt = getPlayer();
        Player player = playerOpt.orElse(null);

        assert player != null;

        return formatArmorArray(player.getInventory().getArmorContents());
    }

    /**
     * Gets the current armor the player is wearing
     * and updates it by differencing it with {@link #savedArmor}.
     */

    public void updateArmor() {
        ItemStack[] savedArmor = getSavedArmor();
        ItemStack[] currentArmor = getCurrentArmor();

        ArmorPiece[] armorPieces = Method.findModifiedArmor(savedArmor, currentArmor);

        setSavedArmor(currentArmor);

        getPlayer().ifPresent(player ->
                trackerController.callApi(player, armorPieces));
    }

    private void setSavedArmor(ItemStack[] savedArmor) {
        this.savedArmor = deepCloneArray(savedArmor);
    }

    /**
     * {@link PlayerInventory#getArmorContents()} returns the items
     * differently. (BOOTS, LEGGINGS, CHESTPLATE, HELMET).
     * <p>
     * This method reverses the array so that the order is
     * the traditional one (HELMET, CHESTPLATE, LEGGINGS, BOOTS).
     *
     * @param armorContents reversed order array
     * @return ordered array
     */

    private ItemStack[] formatArmorArray(ItemStack[] armorContents) {
        ArrayUtils.reverse(armorContents);

        return armorContents;
    }

    private Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(player));
    }

    private ItemStack[] deepCloneArray(ItemStack[] itemArray) {
        return Arrays.stream(itemArray)
                .map(stack -> stack == null ? null : new ItemStack(stack))
                .toArray(ItemStack[]::new); // Deep clone the ItemStack, as the inherent shallow clone method of the ItemStack class has no use to us.
    }

}
