package fr.pixeldeecran.pixellib.command.arg.numbers;

/**
 * Represents the {@link Integer} argument parser.
 */
public class IntegerPAR extends NumberPAR<Integer> {

    /**
     * Parse a {@link String} as a {@link Integer}
     *
     * @param string The {@link String} to parse
     * @return The {@link Integer} value
     */
    @Override
    public Integer valueOf(String string) {
        return Integer.parseInt(string);
    }

    /**
     * Return the name of the error. <br>
     * With a {@link IntegerPAR}, the errors can be : <br>
     * - "INTEGER_NON_VALID_FORMAT" : The argument is not a valid {@link Integer}
     *
     * @param arg The argument
     * @return The error name
     */
    @Override
    public String errorCause(String arg) {
        return "INTEGER_NON_VALID_FORMAT";
    }

    /**
     * @return The display name
     */
    @Override
    public String getDisplayName() {
        return "Integer";
    }
}
