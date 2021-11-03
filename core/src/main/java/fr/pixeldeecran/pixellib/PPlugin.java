package fr.pixeldeecran.pixellib;

import fr.pixeldeecran.pixellib.command.PCommand;
import fr.pixeldeecran.pixellib.command.PCommandRegistry;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents a plugin using the library. This is not required, but it's simplify a lot the setup.
 */
public abstract class PPlugin extends JavaPlugin {

    /**
     * The command registry of this plugin.
     */
    private final PCommandRegistry commandRegistry;

    /**
     * Main constructor of the plugin
     */
    public PPlugin() {
        this.commandRegistry = new PCommandRegistry(this);
        this.commandRegistry.registerDefaults();
    }

    /**
     * Overloading of {@link PCommandRegistry#registerAllCommandsIn(String)}.
     *
     * @param packageName The name of the package
     */
    public void registerAllCommandsIn(String packageName) {
        this.commandRegistry.registerAllCommandsIn(packageName);
    }

    /**
     * Overloading of {@link PCommandRegistry#registerCommand(Class)}.
     *
     * @param commandClass The class of the command to register
     */
    public void registerCommand(Class<? extends PCommand> commandClass) {
        this.commandRegistry.registerCommand(commandClass);
    }

    /**
     * Getter of {@link PPlugin#commandRegistry}.
     *
     * @return The command registry
     */
    public PCommandRegistry getCommandRegistry() {
        return commandRegistry;
    }
}
