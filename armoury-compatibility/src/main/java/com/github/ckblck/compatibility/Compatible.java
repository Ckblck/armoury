package com.github.ckblck.compatibility;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

@Getter
@Setter
public abstract class Compatible<T extends BlockDispenseEvent> implements Listener {
    public Function<DispenserArmorEquipWrapper, Result> eventCallback;

    public abstract int getDamage(ItemStack itemStack);

    @EventHandler
    public abstract void handleEvent(T event);

    public enum Result {
        WRONG_PLAYER,
        HANDLED
    }
}
