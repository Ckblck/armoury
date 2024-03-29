package com.github.ckblck.armoury.tracker.compatibility;

import com.github.ckblck.api.piece.ArmorSlot;
import com.github.ckblck.armoury.tracker.Tracker;
import com.github.ckblck.armoury.tracker.TrackerController;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public abstract class ArmorRelatedEventWrapper<T extends Event> {
    protected final TrackerController controller;
    protected final Plugin plugin;

    protected ArmorRelatedEventWrapper(TrackerController controller, T event, Plugin plugin) {
        this.controller = controller;
        this.plugin = plugin;

        run(event);
    }

    protected abstract void run(T event);

    protected void callModification(Player player, ArmorSlot armorSlot, ItemStack newPiece) {
        Tracker tracker = controller.getTracker(player);
        ItemStack[] currentArmor = tracker.getCurrentArmor();

        int slotId = armorSlot.getSlotId();
        currentArmor[slotId] = newPiece;

        tracker.updateArmor(currentArmor);
    }

    protected boolean nullOrAir(@Nullable ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

}
