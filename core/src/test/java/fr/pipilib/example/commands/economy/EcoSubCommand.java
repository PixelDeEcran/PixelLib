package fr.pipilib.example.commands.economy;

import fr.pixeldeecran.pipilib.command.PSubCommand;
import fr.pipilib.example.commands.EcoCommand;
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
