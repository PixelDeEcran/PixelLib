package fr.pipilib.example.commands.economy;

import fr.pixeldeecran.pipilib.command.PCommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@PCommandInfo(
    name = "set",
    usage = "<amount>",
    description = "Set player's money",
    permission = "pipi.commands.eco.set"
)
public class SetCommand extends EcoSubCommand {

    @Override
    public void operate(OfflinePlayer target, double amount) {
        Bukkit.broadcastMessage("§e" + amount + "$ set to " + target.getName());
    }
}
