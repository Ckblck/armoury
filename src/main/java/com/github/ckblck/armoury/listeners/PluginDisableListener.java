package com.github.ckblck.armoury.listeners;

import com.github.ckblck.armoury.hooks.ApiDispatcher;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class PluginDisableListener implements Listener {
    private final ApiDispatcher dispatcher;

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        Plugin plugin = event.getPlugin();

        dispatcher.unhook(plugin);
    }

}
