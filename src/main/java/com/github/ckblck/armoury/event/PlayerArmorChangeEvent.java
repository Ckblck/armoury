package com.github.ckblck.armoury.event;

import com.github.ckblck.armoury.tracker.calculation.piece.ArmorPiece;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a player has his armor modified.
 * <p>
 * Damage modification might also trigger this event.
 * Due to this, it is recommended to use {@link ArmorPiece#hasBeenDamaged()}
 * to handle this scenario.
 */

@Getter
public class PlayerArmorChangeEvent extends PlayerEvent {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final ArmorPiece[] armorPieces;

    public PlayerArmorChangeEvent(Player who, ArmorPiece[] armorPieces) {
        super(who);

        this.armorPieces = armorPieces;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

}
