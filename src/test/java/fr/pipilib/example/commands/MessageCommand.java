package fr.pipilib.example.commands;

import fr.pipilib.example.commands.broadcast.BroadcastCommand;
import fr.pipilib.example.commands.broadcast.HelpCommand;
import fr.pipilib.example.commands.broadcast.SendCommand;
import fr.pixeldeecran.pipilib.command.PCommand;
import fr.pixeldeecran.pipilib.command.PCommandExist;
import fr.pixeldeecran.pipilib.command.PCommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@PCommandExist
@PCommandInfo(
    name = "message",
    aliases = {"msg"},
    description = "A Message command",
    permission = "fr.pipilib.commands.message",
    subCommands = {
        HelpCommand.class,
        BroadcastCommand.class,
        SendCommand.class
    },
    autoManagingSubCommands = false // Disable the auto sub command managing
)
public class MessageCommand extends PCommand {

    @Override
    public void execute(CommandSender sender) {

        // Manually execute the sub command manager
        // This will return true if a sub command was executed
        boolean result = this.executeSubCommands(sender);

        if (!result) { // Check if a sub command was executed

            // No sub commands were executed, we can display the help message
            // (here, we force the command sender to execute a command for simplicity)
            Bukkit.getServer().dispatchCommand(sender, "message help");
        }
    }
}
