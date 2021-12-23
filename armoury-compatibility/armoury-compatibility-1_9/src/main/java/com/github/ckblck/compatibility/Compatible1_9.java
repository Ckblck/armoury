package com.github.ckblck.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dispenser;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * BlockDispenseArmorEvent does not exist in 1.9 versions.
 * The event is deduced by obtaining the player
 * who was in front of a dispenser.
 */

public class Compatible1_9 extends Compatible<BlockDispenseEvent> {
    private static final int MAXIMUM_Y_POSITIVE_DIFFERENCE = 1;
    private static final int MAXIMUM_Y_NEGATIVE_DIFFERENCE = -3;

    @Override
    public int getDamage(ItemStack itemStack) {
        return itemStack.getDurability();
    }

    @EventHandler
    public void handleEvent(BlockDispenseEvent event) {
        Vector velocity = event.getVelocity();

        if (velocity.getX() != 0D || velocity.getZ() != 0D) // When an armour is equipped, these values equal 0.0D.
            return;

        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        Block block = event.getBlock();
        ItemStack item = event.getItem();

        BlockFace blockFace = ((Dispenser) block.getState().getData()).getFacing();

        Block relativeBlock = block.getRelative(blockFace);

        int blockX = relativeBlock.getX();
        int blockY = relativeBlock.getY();
        int blockZ = relativeBlock.getZ();

        Set<? extends Player> playerInvolved = onlinePlayers
                .stream()
                .filter(player -> {
                    Location location = player.getLocation();

                    int locationBlockX = location.getBlockX();
                    int locationBlockY = location.getBlockY();
                    int locationBlockZ = location.getBlockZ();

                    boolean sameXZLocation = (blockX == locationBlockX) && (blockZ == locationBlockZ);

                    int differenceY = locationBlockY - blockY;

                    boolean withinBounds = (differenceY < MAXIMUM_Y_POSITIVE_DIFFERENCE)
                            && (differenceY > MAXIMUM_Y_NEGATIVE_DIFFERENCE);

                    return sameXZLocation && withinBounds;
                })
                .filter(player -> {
                    ItemStack[] armorContents = player.getEquipment().getArmorContents();

                    return Arrays
                            .stream(armorContents)
                            .anyMatch(item::isSimilar);
                })
                .collect(Collectors.toSet());

        // This loop makes sure the correct player has the armour event fired.
        for (Player player : playerInvolved) {
            DispenserArmorEquipWrapper wrapper = new DispenserArmorEquipWrapper(block, item, player);

            Result result = eventCallback.apply(wrapper);

            if (result == Result.HANDLED) { // Result == HANDLED, meaning that the right player was found.
                break;
            }
        }

    }

}
