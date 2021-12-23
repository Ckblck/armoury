package com.github.ckblck.compatibility;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class Compatible1_13_2 extends Compatible<BlockDispenseArmorEvent> {

    @Override
    public int getDamage(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return -1;
        }

        Damageable itemMeta = (Damageable) itemStack.getItemMeta();
        assert itemMeta != null;

        return itemMeta.getDamage();
    }

    @EventHandler
    public void handleEvent(BlockDispenseArmorEvent event) {
        LivingEntity targetEntity = event.getTargetEntity();

        if (!(targetEntity instanceof Player))
            return;

        DispenserArmorEquipWrapper wrapper = new DispenserArmorEquipWrapper(
                event.getBlock(), event.getItem(), (Player) targetEntity
        );

        eventCallback.apply(wrapper);
    }

}
