package fr.pixeldeecran.pixellib.command.arg.numbers;

/**
 * Represents the {@link Double} argument parser.
 */
public class DoublePAR extends NumberPAR<Double> {

    /**
     * Parse a {@link String} as a {@link Double}
     *
     * @param string The {@link String} to parse
     * @return The {@link Double} value
     */
    @Override
    public Double valueOf(String string) {
        return Double.parseDouble(string);
    }

    /**
     * Return the name of the error. <br>
     * With a {@link DoublePAR}, the errors can be : <br>
     * - "DOUBLE_NON_VALID_FORMAT" : The argument is not a valid {@link Double}
     *
     * @param arg The argument
     * @return The error name
     */
    @Override
    public String errorCause(String arg) {
        return "DOUBLE_NON_VALID_FORMAT";
    }

    /**
     * @return The display name
     */
    @Override
    public String getDisplayName() {
        return null;
    }
}
