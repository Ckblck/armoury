package com.github.ckblck.armoury;

import com.github.ckblck.api.hooks.ApiDispatcher;
import com.github.ckblck.armoury.listeners.Interceptor;
import com.github.ckblck.armoury.listeners.JoinQuitListener;
import com.github.ckblck.armoury.tracker.TrackerController;
import com.github.ckblck.armoury.tracker.compatibility.DispenserWrapper;
import com.github.ckblck.commons.Provider;
import com.github.ckblck.compatibility.Compatible;
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
public class Armoury {
    private final Plugin plugin;

    @Getter
    private ApiDispatcher dispatcher;

    @Getter
    private TrackerController trackerController;

    /**
     * Starts listening
     * and dispatching armour events.
     *
     * @throws IllegalArgumentException if server version < 1.9
     */

    public void start() throws IllegalArgumentException {
        PluginManager pluginManager = Bukkit.getPluginManager();

        try {
            Provider.init(plugin.getServer().getBukkitVersion());
            Compatible<?> compatible = Provider.getInstance().getCompatible();

            pluginManager.registerEvents(compatible, plugin);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

            throw e;
        }

        dispatcher = new ApiDispatcher();
        trackerController = new TrackerController(this);

        pluginManager.registerEvents(new JoinQuitListener(trackerController), plugin);

        new Interceptor(plugin, trackerController);
        new DispenserWrapper(plugin, trackerController);
    }

}
