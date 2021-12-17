package com.github.ckblck.armoury;

import com.github.ckblck.armoury.hooks.ApiDispatcher;
import com.github.ckblck.armoury.listeners.Interceptor;
import com.github.ckblck.armoury.listeners.JoinQuitListener;
import com.github.ckblck.armoury.listeners.PluginDisableListener;
import com.github.ckblck.armoury.tracker.TrackerController;
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

    @Getter
    private TrackerController trackerController;

    /**
     * Starts listening
     * and dispatching armor events.
     */

    public void start() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        dispatcher = new ApiDispatcher();
        trackerController = new TrackerController(this);

        pluginManager.registerEvents(new JoinQuitListener(trackerController), plugin);
        pluginManager.registerEvents(new PluginDisableListener(dispatcher), plugin);

        new Interceptor(plugin, trackerController);
    }

}
