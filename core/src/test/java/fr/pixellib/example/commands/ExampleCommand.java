package fr.pixellib.example.commands;

import fr.pixeldeecran.pixellib.command.PCommand;
import fr.pixeldeecran.pixellib.command.PCommandErrorHandler;
import fr.pixeldeecran.pixellib.command.PCommandExist;
import fr.pixeldeecran.pixellib.command.PCommandInfo;
import org.bukkit.command.CommandSender;

@PCommandExist
@PCommandInfo( // Describe the example command
    name = "example", // The name of the command
    aliases = "examples", // The aliases of the command
    description = "Simple Example Command", // The description of the command
    usage = "<number> [color]", // The usage of the command, <> for required args, and [] for optional args
    permission = "fr.pixellib.commands.example" // The permission of the command
)
public class ExampleCommand extends PCommand { // extends PCommand, this tells that this is a command and not a subcommand

    @Override
    public void init() {
        PCommandErrorHandler errorHandler = new PCommandErrorHandler();
        errorHandler.registerDefaults(this.getCommandRegistry());
        errorHandler.registerErrorMessage("NOT_ONLINE_PLAYER", "§cLe Joueur %2$s ne semble pas être connecté !");
        this.setErrorHandler(errorHandler);
    }

    @Override
    public void execute(CommandSender sender) { // When the command is executed

        // We read the first arg, which is required and is an int arg if the arg couldn't be read,
        // The code will stop here and will send a message to the player describing the error
        int number = this.readRequiredArg(0, int.class);

        // We also read the second arg, which is optional, if the command sender didn't specify the arg
        // Or there was an error while parsing the arg, the default value will be returned
        char color = this.readOptionalArg(1, 'e');

        // We can now send the message to the player and use the information provided by the command sender
        sender.sendMessage(String.format("§%1$sYou enter the number %2$d!", color, number));
    }
}
