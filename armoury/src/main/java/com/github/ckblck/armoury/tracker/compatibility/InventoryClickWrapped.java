package com.github.ckblck.armoury.tracker.compatibility;

import com.github.ckblck.api.piece.ArmorSlot;
import com.github.ckblck.armoury.tracker.Tracker;
import com.github.ckblck.armoury.tracker.TrackerController;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InventoryClickWrapped extends ArmorRelatedEventWrapper<InventoryClickEvent> {
    private Player player;
    private ItemStack cursorItem;
    private ItemStack currentItem;
    private int rawSlot;

    @Nullable
    private ArmorSlot cursorItemSlot;
    @Nullable
    private ArmorSlot currentItemSlot;

    public InventoryClickWrapped(TrackerController controller, Plugin plugin, InventoryClickEvent event) {
        super(controller, event, plugin);
    }

    @Override
    protected void run(InventoryClickEvent event) {
        try {
            InventoryAction action = event.getAction();

            if (action == InventoryAction.NOTHING)
                return;

            player = (Player) event.getWhoClicked();
            Inventory clickedInventory = event.getClickedInventory();
            PlayerInventory playerInventory = player.getInventory();

            if (playerInventory != clickedInventory)
                return;

            InventoryType topInventory = player.getOpenInventory().getTopInventory().getType();

            if (topInventory != InventoryType.CRAFTING)
                return;

            cursorItem = event.getCursor();
            currentItem = event.getCurrentItem();

            if (currentItem == null)
                return;

            rawSlot = event.getRawSlot();
            Material cursorItemType = cursorItem.getType();
            Material currentItemType = currentItem.getType();

            cursorItemSlot = ArmorSlot.findSlot(cursorItemType)
                    .orElse(null);
            currentItemSlot = ArmorSlot.findSlot(currentItemType)
                    .orElse(null);

            InventoryType.SlotType slotType = event.getSlotType();

            boolean armorSlotsWereClicked = slotType == InventoryType.SlotType.ARMOR;

            if (armorSlotsWereClicked) { // Player clicked armor slot.
                boolean cursorEmpty = nullOrAir(cursorItem);

                if (cursorEmpty) { // Player is clicking over an item (taking out the armour piece).
                    handleRemove();
                } else if (nullOrAir(currentItem)) {
                    // Player's cursor is not empty, but the clicked armour slot is. (when in survival mode)
                    // This condition is also triggered when shift-clicking in creative mode.
                    // When in creative mode, even if the player's cursor IS EMPTY in-game, cursorEmpty actually returns false.
                    // This is nonsensical, but this is the unfortunate way it works.
                    handleWear();
                } else { // Both the cursor and the clicked slot have an item.
                    handleReplace();
                }

                return;
            }

            boolean couldBeShiftClick = slotType == InventoryType.SlotType.CONTAINER
                    || slotType == InventoryType.SlotType.QUICKBAR;

            // Shift-click handling only.
            if (couldBeShiftClick) {
                handleShiftClick(event);
            }
        } catch (Exception e) {
            e.printStackTrace();

            plugin.getLogger().warning("Error: " + new ReflectionToStringBuilder(event).toString());
        }
    }

    private void handleReplace() {
        Optional<ArmorSlot> cursorSlot = ArmorSlot.findSlot(cursorItem.getType());

        if (!cursorSlot.isPresent())
            return;

        boolean isConventional = ArmorSlot.isConventionalPiece(cursorItem)
                || ArmorSlot.isConventionalPiece(currentItem);

        if (isConventional) // Modifications of conventional pieces are handled with packets.
            return;

        boolean bothSlotsAreSame = cursorSlot.get() == currentItemSlot;

        if (bothSlotsAreSame) {
            boolean currentPieceIsConventional = ArmorSlot.isConventionalPiece(currentItem);

            // Provided it was conventional, it would get updated with packets,
            // so we would be incorrectly calling the update method twice.
            if (!currentPieceIsConventional) {
                callModification(player, cursorSlot.get(), cursorItem);
            }

        }

    }

    private void handleShiftClick(InventoryClickEvent event) {
        ClickType eventClick = event.getClick();
        boolean isShiftClick = (eventClick == ClickType.SHIFT_LEFT) || (eventClick == ClickType.SHIFT_RIGHT);

        // Creative shift-click actions are handled in the CreativeInventoryClickWrapped class.
        if (!isShiftClick || eventClick.isCreativeAction()) {
            return;
        }

        boolean notAnArmorPiece = currentItemSlot == null;

        if (notAnArmorPiece)
            return;

        boolean conventionalPiece = ArmorSlot.isConventionalPiece(currentItem);

        if (conventionalPiece) // Conventional pieces modifications are handled with packets.
            return;

        Tracker tracker = controller.getTracker(player);
        Optional<ItemStack> item = tracker.getCurrentPieceAtSlot(currentItemSlot);

        boolean isSlotOccupied = item.isPresent();

        if (isSlotOccupied)
            return;

        callModification(player, currentItemSlot, currentItem);
    }

    private void handleRemove() {
        boolean conventionalPiece = ArmorSlot.isConventionalPiece(currentItem);
        assert currentItemSlot != null; // Checked before method call.

        if (conventionalPiece)
            return;

        callModification(player, currentItemSlot, null);
    }

    private void handleWear() {
        boolean wrongSlotClicked = ArmorSlot.findByRawSlot(rawSlot)
                .orElseThrow(Error::new) != cursorItemSlot;

        if (wrongSlotClicked) // Player attempted to wear an armour piece in an invalid slot.
            return;

        boolean conventionalPiece = ArmorSlot.isConventionalPiece(cursorItem);
        assert cursorItemSlot != null; // Checked before method call.

        if (conventionalPiece)
            return;

        callModification(player, cursorItemSlot, cursorItem);
    }

}
