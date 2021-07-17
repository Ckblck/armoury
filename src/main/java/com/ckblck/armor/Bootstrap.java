package com.ckblck.armor;

import com.ckblck.armor.api.ApiController;
import com.ckblck.armor.listeners.Interceptor;
import com.ckblck.armor.listeners.JoinQuitListener;
import com.ckblck.armor.tracker.TrackerController;
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
 * <p>
 * If you do not wish to use it as an external plugin,
 * you may call the constructor and use the
 * {@link #start()} method instead.
 */

@Getter
@RequiredArgsConstructor
public class Bootstrap {
    private final Plugin plugin;

    private TrackerController trackerController;
    private JoinQuitListener joinQuitListener;
    private ApiController apiController;

    public void start() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        apiController = new ApiController();
        trackerController = new TrackerController(this);
        joinQuitListener = new JoinQuitListener(trackerController);

        pluginManager.registerEvents(joinQuitListener, plugin);

        new Interceptor(plugin, trackerController);
    }

}
