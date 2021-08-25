package fr.pipilib.example.commands.broadcast;

import fr.pipilib.example.commands.MessageCommand;
import fr.pixeldeecran.pipilib.command.PCommandInfo;
import fr.pixeldeecran.pipilib.command.PSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@PCommandInfo(
    name = "broadcast",
    aliases = "bc",
    description = "Broadcast a message",
    usage = "<message>", // Specify the usage
    permission = "fr.pipilib.commands.message.broadcast"
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
