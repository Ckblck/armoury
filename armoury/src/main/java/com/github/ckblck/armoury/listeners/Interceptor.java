package com.github.ckblck.armoury.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedAttribute;
import com.github.ckblck.armoury.tracker.Tracker;
import com.github.ckblck.armoury.tracker.TrackerController;
import com.github.ckblck.armoury.tracker.calculation.compatibility.InventoryClickWrapped;
import com.github.ckblck.armoury.tracker.calculation.compatibility.ItemClickWrapped;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Listens to the UPDATE_ATTRIBUTES packet,
 * which acts as a notification of when
 * a player's equipment has changed.
 */

public class Interceptor extends PacketAdapter implements Listener {
    private final TrackerController controller;

    public Interceptor(Plugin plugin, TrackerController controller) {
        super(plugin, ListenerPriority.MONITOR, PacketType.Play.Server.UPDATE_ATTRIBUTES);

        ProtocolLibrary.getProtocolManager().addPacketListener(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.controller = controller;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        new InventoryClickWrapped(controller, plugin, event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(PlayerInteractEvent event) {
        new ItemClickWrapped(controller, plugin, event);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Entity entity = packet.getEntityModifier(event).read(0);

        if (!(entity instanceof Player)) {
            return;
        }

        List<WrappedAttribute> attributes = packet.getAttributeCollectionModifier().read(0);

        boolean notArmorRelated = attributes.size() > 2 || attributes
                .stream()
                .map(WrappedAttribute::getAttributeKey)
                .anyMatch(attribute -> !attribute.contains("armor"));

        if (notArmorRelated)
            return;

        attributes.forEach(attribute -> {
            if (!attribute.getAttributeKey().equals("generic.armor"))
                return;

            notifyArmorChange((Player) entity);
        });

    }

    private void notifyArmorChange(Player player) {
        Tracker tracker = controller.getTracker(player);

        tracker.updateArmor();
    }

}