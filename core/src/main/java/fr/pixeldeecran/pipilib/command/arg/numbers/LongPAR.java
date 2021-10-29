package fr.pixeldeecran.pipilib.command.arg.numbers;

/**
 * Represents the {@link Long} argument parser.
 */
public class LongPAR extends NumberPAR<Long> {

    /**
     * Parse a {@link String} as a {@link Long}
     *
     * @param string The {@link String} to parse
     * @return The {@link Long} value
     */
    @Override
    public Long valueOf(String string) {
        return Long.parseLong(string);
    }

    /**
     * Return the name of the error. <br>
     * With a {@link LongPAR}, the errors can be : <br>
     *  - "LONG_NON_VALID_FORMAT" : The argument is not a valid {@link Long}
     *
     * @param arg The argument
     * @return The error name
     */
    @Override
    public String errorCause(String arg) {
        return "LONG_NON_VALID_FORMAT";
    }

    /**
     * @return The display name
     */
    @Override
    public String getDisplayName() {
        return "Long";
    }
}
