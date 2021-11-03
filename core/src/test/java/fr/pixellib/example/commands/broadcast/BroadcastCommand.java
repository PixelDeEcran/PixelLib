package fr.pixellib.example.commands.broadcast;

import fr.pixellib.example.commands.MessageCommand;
import fr.pixeldeecran.pixellib.command.PCommandInfo;
import fr.pixeldeecran.pixellib.command.PSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@PCommandInfo(
    name = "broadcast",
    aliases = "bc",
    description = "Broadcast a message",
    usage = "<message>", // Specify the usage
    permission = "fr.pixellib.commands.message.broadcast"
)
public class BroadcastCommand extends PSubCommand<MessageCommand> {

    @Override
    public void execute(CommandSender sender) {
        // Sentence are groups of args
        // They can or cannot have specific length
        // Here, we read a String, starting at the first argument (included)
        String message = this.readRequiredSentence(0, String.class);

        // Broadcast the message (with colors!)
        Bukkit.broadcastMessage(message.replace('&', '§'));
    }
}
