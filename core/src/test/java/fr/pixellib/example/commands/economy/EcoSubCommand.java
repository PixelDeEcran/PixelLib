package fr.pixellib.example.commands.economy;

import fr.pixellib.example.commands.EcoCommand;
import fr.pixeldeecran.pixellib.command.PSubCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public abstract class EcoSubCommand extends PSubCommand<EcoCommand> {

    @Override
    public void execute(CommandSender sender) {
        Integer amount = this.readRequiredArg(0, Integer.class);

        this.operate(this.getParent().getCurrentTarget(), amount);
    }

    public abstract void operate(OfflinePlayer target, double amount);
}
