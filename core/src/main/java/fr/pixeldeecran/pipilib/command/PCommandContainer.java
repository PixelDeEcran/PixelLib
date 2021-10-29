package fr.pixeldeecran.pipilib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the command container, which actually is an interface between Bukkit and the library's commands.
 */
public class PCommandContainer extends Command implements PluginIdentifiableCommand {

    /**
     * The root command. The instance is set in the constructor of the command container.
     *
     * @see PCommandContainer#PCommandContainer(PCommand, PCommandRegistry, JavaPlugin)
     * @see PCommandContainer#execute(CommandSender, String, String[])
     * @see PCommandContainer#tabComplete(CommandSender, String, String[])
     * @see PCommandContainer#getCommand()
     */
    private final PCommand command;

    /**
     * The instance of the plugin of the command. The instance is set in the constructor of the command container.
     *
     * @see PCommandContainer#getPlugin()
     */
    private final JavaPlugin plugin;

    /**
     * Main constructor of PCommandContainer. This is where we set the command registry and call {@link PCommand#init()}.
     *
     * @param command The command
     * @param commandRegistry The command registry
     * @param plugin The plugin's instance
     */
    public PCommandContainer(PCommand command, PCommandRegistry commandRegistry, JavaPlugin plugin) {
        super(command.getName());

        this.command = command;
        this.plugin = plugin;

        this.setAliases(Arrays.asList(command.getAliases()));
        this.setDescription(command.getDescription());

        this.command.setCommandRegistry(commandRegistry);
        this.command.init();
    }

    /**
     * Called when the command is executed. It calls {@link PCommand#internallyExecute(CommandSender, String[])} and
     * catch exceptions.
     *
     * @param sender The command sender
     * @param commandLabel The label used to reference the command
     * @param args The arguments used
     * @return Was the command successful or not?
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        try {
            this.command.internallyExecute(sender, args);
            return true;
        } catch (PCommandException e) {
            this.command.getErrorHandler().whenError(this.command.getContext().setCurrentError(e.getMessage()));
        } catch (Exception e) {
            this.command.getErrorHandler().whenError(this.command.getContext().setCurrentError("EXCEPTION"), e);
        }

        return false;
    }

    /**
     * Called when the command is asked for tab completion. It calls
     * {@link PCommand#internallyTabCompleteFor(CommandSender, String[])} and catch exceptions.
     *
     * @param sender The command sender
     * @param alias The label used to reference the command
     * @param args The arguments used
     * @return The completion list
     */
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        try {
            return this.command.internallyTabCompleteFor(sender, args);
        } catch (PCommandException e) {
            this.command.getErrorHandler().whenError(this.command.getContext().setCurrentError(e.getMessage()));
        } catch (Exception e) {
            this.command.getErrorHandler().whenError(this.command.getContext().setCurrentError("EXCEPTION"), e);
        }

        return Collections.emptyList();
    }

    /**
     * Getter of {@link PCommandContainer#command}
     *
     * @return The command
     */
    public PCommand getCommand() {
        return command;
    }

    /**
     * Getter of {@link PCommandContainer#plugin}.
     *
     * @return The plugin's instance
     */
    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }
}
