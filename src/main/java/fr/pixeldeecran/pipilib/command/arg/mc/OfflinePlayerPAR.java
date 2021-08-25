package fr.pixeldeecran.pipilib.command.arg.mc;

import fr.pixeldeecran.pipilib.command.arg.PArgReader;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerPAR implements PArgReader<OfflinePlayer> {

    @Override
    public OfflinePlayer read(String arg) {
        return Bukkit.getOfflinePlayer(arg);
    }

    @Override
    public String errorCause(String arg) {
        return "CRITICAL";
    }

    @Override
    public String getDisplayName() {
        return "OfflinePlayer";
    }
}
