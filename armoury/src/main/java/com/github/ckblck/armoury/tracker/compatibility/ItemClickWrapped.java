package com.github.ckblck.armoury.tracker.compatibility;

import com.github.ckblck.api.piece.ArmorSlot;
import com.github.ckblck.armoury.tracker.Tracker;
import com.github.ckblck.armoury.tracker.TrackerController;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public class ItemClickWrapped extends ArmorRelatedEventWrapper<PlayerInteractEvent> {

    public ItemClickWrapped(TrackerController controller, Plugin plugin, PlayerInteractEvent event) {
        super(controller, event, plugin);
    }

    @Override
    protected void run(PlayerInteractEvent event) {
        ItemStack wearableItem = event.getItem();

        if (nullOrAir(wearableItem))
            return;

        Action eventAction = event.getAction();

        boolean invalidAction = !(eventAction == Action.RIGHT_CLICK_AIR || eventAction == Action.RIGHT_CLICK_BLOCK);
        assert wearableItem != null;

        if (invalidAction)
            return;

        if (ArmorSlot.isConventionalPiece(wearableItem))
            return;

        if (!ArmorSlot.isLeftClickWearable(wearableItem))
            return;

        Optional<ArmorSlot> armorSlotOpt = ArmorSlot.findSlot(wearableItem);

        if (!armorSlotOpt.isPresent())
            return;

        Player player = event.getPlayer();
        Tracker tracker = controller.getTracker(player);
        ArmorSlot armorSlot = armorSlotOpt.get();

        Optional<ItemStack> itemAtSlot = tracker.getCurrentPieceAtSlot(armorSlot);
        boolean isSlotOccupied = itemAtSlot.isPresent();

        if (isSlotOccupied)
            return;

        callModification(player, armorSlot, wearableItem);
    }

}
