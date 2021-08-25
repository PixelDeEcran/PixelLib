package fr.pixeldeecran.pipilib;

import fr.pixeldeecran.pipilib.command.PCommandRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PPlugin extends JavaPlugin {

    /**
     * The main instance of the plugin
     *
     * I know, this is bad naming, but this variable in most case is considered as a singleton instance
     */
    public static PPlugin INSTANCE;

    private final PCommandRegistry commandRegistry;

    public PPlugin() {
        INSTANCE = this;

        this.commandRegistry = new PCommandRegistry(this);
        this.commandRegistry.registerDefaults();
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public PCommandRegistry getCommandRegistry() {
        return commandRegistry;
    }
}
