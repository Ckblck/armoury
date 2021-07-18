package com.github.ckblck.armor.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedAttribute;
import com.github.ckblck.armor.tracker.Tracker;
import com.github.ckblck.armor.tracker.TrackerController;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Listens to the UPDATE_ATTRIBUTES,
 * which acts as a notification of when
 * a player's equipment has changed.
 */

public class Interceptor extends PacketAdapter {
    private final TrackerController controller;

    public Interceptor(Plugin plugin, TrackerController controller) {
        super(plugin, ListenerPriority.MONITOR, PacketType.Play.Server.UPDATE_ATTRIBUTES);

        ProtocolLibrary.getProtocolManager().addPacketListener(this);

        this.controller = controller;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        int entityId = packet.getIntegers().read(0);
        Player player = event.getPlayer();

        if (entityId != player.getEntityId())
            return;

        List<WrappedAttribute> attributes = packet.getAttributeCollectionModifier().read(0);

        attributes.forEach(attribute -> {
            if (!attribute.getAttributeKey().equals("generic.armor"))
                return;

            notifyArmorChange(player);
        });

    }

    private void notifyArmorChange(Player player) {
        Tracker tracker = controller.getTracker(player);

        tracker.updateArmor();
    }

}