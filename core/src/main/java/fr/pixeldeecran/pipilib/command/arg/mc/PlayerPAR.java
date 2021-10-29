package fr.pixeldeecran.pipilib.command.arg.mc;

import fr.pixeldeecran.pipilib.command.arg.CharPAR;
import fr.pixeldeecran.pipilib.command.arg.PArgReader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Represents the {@link Player} argument parser.
 */
public class PlayerPAR implements PArgReader<Player> {

    /**
     * Parse an argument as a {@link Player}.
     *
     * @param arg The argument
     * @return The {@link Player} value
     */
    @Override
    public Player read(String arg) {
        return Bukkit.getPlayerExact(arg);
    }

    /**
     * Return the name of the error. <br>
     * With a {@link CharPAR}, the errors can be : <br>
     * - "NOT_ONLINE_PLAYER" : The specified player is not online
     *
     * @param arg The argument
     * @return The error name
     */
    @Override
    public String errorCause(String arg) {
        return "NOT_ONLINE_PLAYER";
    }

    /**
     * @return The display name
     */
    @Override
    public String getDisplayName() {
        return "Player";
    }
}
