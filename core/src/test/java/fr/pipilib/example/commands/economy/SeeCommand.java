package fr.pipilib.example.commands.economy;

import fr.pipilib.example.commands.EcoCommand;
import fr.pixeldeecran.pipilib.command.PCommandInfo;
import fr.pixeldeecran.pipilib.command.PSubCommand;
import org.bukkit.command.CommandSender;

@PCommandInfo(
    name = "see",
    description = "See player's money",
    permission = "pipi.commands.eco.see"
)
public class SeeCommand extends PSubCommand<EcoCommand> {

    @Override
    public void execute(CommandSender sender) {
        sender.sendMessage("§6" + this.getParent().getCurrentTarget().getName() + " a $" + Math.round(Math.random() * 1000) + " !");
    }
}
