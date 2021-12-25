package com.github.ckblck.armoury.tracker;

import com.github.ckblck.api.piece.ArmorPiece;
import com.github.ckblck.api.piece.ArmorSlot;
import com.github.ckblck.api.piece.Method;
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
 * Keeps track of the armour changes
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
     * Returns the armour the player is currently wearing.
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
     * Returns the current armor piece
     * the player is wearing at a specific slot.
     *
     * @param slot slot in which the armor is equipped
     * @return an optional with the armor piece of such slot, or empty if null
     */

    public Optional<ItemStack> getCurrentPieceAtSlot(ArmorSlot slot) {
        return Optional.ofNullable(getCurrentArmor()[slot.getSlotId()]);
    }

    /**
     * Returns the saved armor piece
     * at a specific slot.
     *
     * @param slot slot in which the armor was equipped
     * @return an optional with the armor piece of such slot, or empty if null
     */

    public Optional<ItemStack> getSavedPieceAtSlot(ArmorSlot slot) {
        return Optional.ofNullable(getSavedArmor()[slot.getSlotId()]);
    }

    /**
     * Sets a new armor piece
     * at a specific slot.
     *
     * @param slot     slot where to set the new piece
     * @param newPiece piece to set at slot's index
     */

    public void setSavedPieceAtSlot(ArmorSlot slot, ItemStack newPiece) {
        getSavedArmor()[slot.getSlotId()] = newPiece;
    }

    /**
     * Gets the current armor the player is wearing
     * and updates it by differencing it with {@link #savedArmor}.
     */

    public void updateArmor() {
        updateArmor(getCurrentArmor());
    }

    public void updateArmor(ItemStack[] currentArmor) {
        ItemStack[] savedArmor = getSavedArmor();
        ArmorPiece[] armorPieces;

        try {
            armorPieces = Method.findModifiedArmor(savedArmor, currentArmor);
        } catch (IllegalArgumentException ignored) { // Explanation provided in findModifiedArmor method.
            return;
        }

        setSavedArmor(currentArmor);

        getPlayer().ifPresent(player ->
                trackerController.callApi(player, armorPieces));
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(player));
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

    private ItemStack[] deepCloneArray(ItemStack[] itemArray) {
        return Arrays.stream(itemArray)
                .map(stack -> stack == null ? null : new ItemStack(stack))
                .toArray(ItemStack[]::new); // Deep clone the ItemStack, as the inherent shallow clone method of the ItemStack class has no use to us.
    }

}
