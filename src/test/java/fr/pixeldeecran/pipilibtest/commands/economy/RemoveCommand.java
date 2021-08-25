package fr.pixeldeecran.pipilibtest.commands.economy;

import fr.pixeldeecran.pipilib.command.PCommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@PCommandInfo(
    name = "remove",
    usage = "<amount>",
    description = "Remove money from player",
    permission = "pipi.commands.eco.remove"
)
public class RemoveCommand extends EcoSubCommand {

    @Override
    public void operate(OfflinePlayer target, double amount) {
        Bukkit.broadcastMessage("§e$" + amount + " remove from " + target.getName());
    }
}
