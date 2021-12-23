package com.github.ckblck.armoury.tracker.calculation.compatibility;

import com.github.ckblck.armoury.tracker.Tracker;
import com.github.ckblck.armoury.tracker.TrackerController;
import com.github.ckblck.armoury.tracker.calculation.piece.ArmorSlot;
import com.github.ckblck.commons.Provider;
import com.github.ckblck.compatibility.Compatible;
import com.github.ckblck.compatibility.DispenserArmorEquipWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.function.Function;

public class DispenserWrapper extends ArmorRelatedEventWrapper<Event> {

    public DispenserWrapper(Plugin plugin, TrackerController controller) {
        super(controller, null, plugin);

        Provider provider = Provider.getInstance();
        Compatible<?> compatible = provider.getCompatible();

        compatible.setEventCallback(onDispense());
    }

    private Function<DispenserArmorEquipWrapper, Compatible.Result> onDispense() {
        return (wrapper -> {
            Player player = wrapper.getTarget();

            ItemStack armorPiece = wrapper.getItem();

            if (ArmorSlot.isConventionalPiece(armorPiece))
                return Compatible.Result.HANDLED;

            Tracker tracker = controller.getTracker(player);

            ArmorSlot armorSlot = ArmorSlot
                    .findSlot(armorPiece)
                    .orElseThrow(Error::new);

            Optional<ItemStack> savedPieceAtSlot = tracker.getSavedPieceAtSlot(armorSlot);

            if (savedPieceAtSlot.isPresent()) {
                ItemStack previousPiece = savedPieceAtSlot.get();
                boolean hasAnotherPiece = armorPiece.isSimilar(previousPiece);

                if (hasAnotherPiece) {
                    return Compatible.Result.WRONG_PLAYER;
                }
            }

            callModification(player, armorSlot, armorPiece);

            return Compatible.Result.HANDLED;
        });
    }

    @Override
    protected void run(Event event) {
        // No use.
    }

}
