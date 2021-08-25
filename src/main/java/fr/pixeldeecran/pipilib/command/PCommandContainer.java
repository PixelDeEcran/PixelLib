package fr.pixeldeecran.pipilib.command;

import fr.pixeldeecran.pipilib.PPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PCommandContainer extends Command implements PluginIdentifiableCommand {

    private final PCommand command;
    private final PPlugin plugin;

    public PCommandContainer(PCommand command, PCommandRegistry commandRegistry, PPlugin plugin) {
        super(command.getName());

        this.command = command;
        this.plugin = plugin;

        this.setAliases(Arrays.asList(command.getAliases()));
        this.setDescription(command.getDescription());

        this.command.setCommandRegistry(commandRegistry);
    }

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

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        try {
            return this.command.internallyTabCompleteFor(sender, args);
        } catch (PCommandException e) {
            this.command.getErrorHandler().whenError(this.command.getContext().setCurrentError(e.getMessage()));
        } catch (Exception e) {
            this.command.getErrorHandler().whenError(this.command.getContext().setCurrentError("EXCEPTION"), e);
        }

        return Collections.emptyList();
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }
}
