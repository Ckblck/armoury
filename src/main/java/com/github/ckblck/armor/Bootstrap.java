package com.github.ckblck.armor;

import com.github.ckblck.armor.hooks.ApiDispatcher;
import com.github.ckblck.armor.listeners.Interceptor;
import com.github.ckblck.armor.listeners.JoinQuitListener;
import com.github.ckblck.armor.listeners.PluginDisableListener;
import com.github.ckblck.armor.tracker.TrackerController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * The <i>onEnable</i> class of the library.
 * <p>
 * Aimed to provide flexibility
 * as regards library bundling.
 */

@RequiredArgsConstructor
public class Bootstrap {
    private final Plugin plugin;

    @Getter
    private ApiDispatcher dispatcher;

    /**
     * Starts listening
     * and dispatching armor events.
     */

    public void start() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        dispatcher = new ApiDispatcher();

        TrackerController trackerController = new TrackerController(this);

        pluginManager.registerEvents(new JoinQuitListener(trackerController), plugin);
        pluginManager.registerEvents(new PluginDisableListener(dispatcher), plugin);

        new Interceptor(plugin, trackerController);
    }

}
