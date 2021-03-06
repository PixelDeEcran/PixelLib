package fr.pixellib.example.commands.broadcast;

import fr.pixellib.example.commands.MessageCommand;
import fr.pixeldeecran.pixellib.command.PCommandInfo;
import fr.pixeldeecran.pixellib.command.PSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@PCommandInfo(
    name = "send",
    description = "Send a message to a specific player",
    usage = "<player> <message>",
    permission = "fr.pixellib.commands.message.send"
)
public class SendCommand extends PSubCommand<MessageCommand> {

    @Override
    public void execute(CommandSender sender) {
        // Here, we read the first argument which returns an online player
        // If the player is offline, the code will stop here
        Player player = this.readRequiredArg(0, Player.class);

        // Unlike BroadcastCommand, here the sentence start at the argument 1
        String message = this.readRequiredSentence(1, String.class);

        // We broadcast the message
        player.sendMessage("§6" + sender.getName() + " : §e" + message.replace('&', '§'));
    }
}
