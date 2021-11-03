package fr.pixeldeecran.pixellib.command.arg.mc;

import fr.pixeldeecran.pixellib.command.arg.PArgReader;
import fr.pixeldeecran.pixellib.command.arg.StringPAR;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Represents the {@link OfflinePlayer} argument parser.
 */
public class OfflinePlayerPAR implements PArgReader<OfflinePlayer> {

    /**
     * Parse an argument as an {@link OfflinePlayer}.
     *
     * @param arg The argument
     * @return The {@link OfflinePlayer} value
     */
    @SuppressWarnings("deprecation")
    @Override
    public OfflinePlayer read(String arg) {
        return Bukkit.getOfflinePlayer(arg);
    }

    /**
     * Return the name of the error. <br>
     * With a {@link StringPAR}, the errors can be : <br>
     * - "CRITICAL" : An unexpected error
     *
     * @param arg The argument
     * @return The error name
     */
    @Override
    public String errorCause(String arg) {
        return "CRITICAL";
    }

    /**
     * @return The display name
     */
    @Override
    public String getDisplayName() {
        return "OfflinePlayer";
    }
}
