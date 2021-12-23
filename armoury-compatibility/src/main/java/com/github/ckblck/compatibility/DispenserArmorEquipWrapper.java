package com.github.ckblck.compatibility;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Getter
public class DispenserArmorEquipWrapper extends BlockDispenseEvent {
    private static final Vector VECTOR = new Vector(0, 0, 0);

    private final Player target;

    public DispenserArmorEquipWrapper(Block block, ItemStack dispensedItem, Player target) {
        super(block, dispensedItem, VECTOR);

        this.target = target;
    }

}
