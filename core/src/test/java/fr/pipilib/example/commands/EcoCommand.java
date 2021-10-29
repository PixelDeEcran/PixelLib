package fr.pipilib.example.commands;

import fr.pipilib.example.commands.economy.AddCommand;
import fr.pipilib.example.commands.economy.RemoveCommand;
import fr.pipilib.example.commands.economy.SetCommand;
import fr.pixeldeecran.pipilib.command.PCommand;
import fr.pixeldeecran.pipilib.command.PCommandInfo;
import fr.pipilib.example.commands.economy.SeeCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@PCommandInfo(
    name = "eco",
    aliases = {"economy", "money", "bal", "balance"},
    subCommandUsage = "<target>",
    description = "Display your money",
    permission = "pipi.commands.eco",
    subCommands = {
        AddCommand.class,
        RemoveCommand.class,
        SetCommand.class,
        SeeCommand.class
    },
    subCommandIndex = 1
)
public class EcoCommand extends PCommand {

    private OfflinePlayer currentTarget;

    @Override
    public void execute(CommandSender sender) {
        if (this.getCurrentArgsLength() == 0) {
            sender.sendMessage("§6Vous avez $" + Math.round(Math.random() * 1000) + " !");
            return;
        }

        String firstArg = this.readRequiredArg(0, String.class);
        if (firstArg.equalsIgnoreCase("help")) {
            this.sendHelpMenu(sender);
            return;
        }

        this.currentTarget = this.readRequiredArg(0, OfflinePlayer.class);

        boolean result = this.executeSubCommands(sender);
        if (!result) {
            this.sendHelpMenu(sender);
        }
    }

    private void sendHelpMenu(CommandSender sender) {
        sender.sendMessage(String.format("§6%1$s : §e%2$s", this.getFullUsage(), this.getDescription()));
        sender.sendMessage("§6/eco help : §eDisplay Help Menu");
        this.getAllSubCommands().forEach(subCommand -> {
            sender.sendMessage(String.format("§6%1$s : §e%2$s", subCommand.getFullUsage(), subCommand.getDescription()));
        });
    }

    public OfflinePlayer getCurrentTarget() {
        return currentTarget;
    }
}
