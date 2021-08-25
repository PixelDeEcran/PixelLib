package fr.pixeldeecran.pipilib.command.arg.mc;

import fr.pixeldeecran.pipilib.command.arg.PArgReader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerPAR implements PArgReader<Player> {

    @Override
    public Player read(String arg) {
        return Bukkit.getPlayerExact(arg);
    }

    @Override
    public String errorCause(String arg) {
        return "NOT_ONLINE_PLAYER";
    }

    @Override
    public String getDisplayName() {
        return "Player";
    }
}
