package fr.pixellib.example.commands.economy;

import fr.pixeldeecran.pixellib.command.PCommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@PCommandInfo(
    name = "add",
    usage = "<amount>",
    description = "Add money to player",
    permission = "pipi.commands.eco.add"
)
public class AddCommand extends EcoSubCommand {

    @Override
    public void operate(OfflinePlayer target, double amount) {
        Bukkit.broadcastMessage("§e$" + amount + " add to " + target.getName());
    }
}
