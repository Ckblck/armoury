package com.github.ckblck.armor.triggers.event;

import com.github.ckblck.armor.utils.ArmorModification;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
public class PlayerArmorChangeEvent extends PlayerEvent {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final ArmorModification modification;

    public PlayerArmorChangeEvent(Player who, ArmorModification modification) {
        super(who);

        this.modification = modification;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

}
