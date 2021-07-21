package com.github.ckblck.armor;

import com.github.ckblck.armor.listeners.Interceptor;
import com.github.ckblck.armor.listeners.JoinQuitListener;
import com.github.ckblck.armor.tracker.TrackerController;
import com.github.ckblck.armor.triggers.Dispatcher;
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

@Getter
@RequiredArgsConstructor
public class Bootstrap {
    private final Plugin plugin;

    private TrackerController trackerController;
    private JoinQuitListener joinQuitListener;
    private Dispatcher dispatcher;

    /**
     * Starts listening
     * and dispatching armor events.
     */

    public void start() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        dispatcher = new Dispatcher();
        trackerController = new TrackerController(this);
        joinQuitListener = new JoinQuitListener(trackerController);

        pluginManager.registerEvents(joinQuitListener, plugin);

        new Interceptor(plugin, trackerController);
    }

}
