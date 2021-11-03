package fr.pixellib.example.commands.broadcast;

import fr.pixellib.example.commands.MessageCommand;
import fr.pixeldeecran.pixellib.command.PCommandInfo;
import fr.pixeldeecran.pixellib.command.PSubCommand;
import org.bukkit.command.CommandSender;

@PCommandInfo(
    name = "help",
    description = "Display the available commands",
    permission = "fr.pixellib.commands.message.help"
    // We don't need to specify the usage because there are no arguments used
)
public class HelpCommand extends PSubCommand<MessageCommand> { // We specify the parent command

    @Override
    public void execute(CommandSender sender) {
        sender.sendMessage("§6<---------- /broadcast help ---------->");
        sender.sendMessage(String.format(
            "§6%1$s : §e%2$s", // String to format
            this.getParent().getFullUsage(), // The full usage of the command (name included), here, it will be /broadcast <message>
            this.getParent().getDescription() // The description of the command
        ));

        // PCommand#getAllSubCommands() returns a list with all sub commands, and even sub commands of sub commands, etc...
        // In fact, it's just a recursive function
        this.getParent().getAllSubCommands().forEach(subCommand -> {
            sender.sendMessage("§6" + subCommand.getFullUsage() + " : §e" + subCommand.getDescription());
        });
    }
}
