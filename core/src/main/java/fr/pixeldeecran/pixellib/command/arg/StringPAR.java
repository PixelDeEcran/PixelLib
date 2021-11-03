package fr.pixeldeecran.pixellib.command.arg;

/**
 * Represents the {@link String} argument parser.
 */
public class StringPAR implements PArgReader<String> {

    /**
     * Parse an argument as a {@link String}.
     *
     * @param arg The argument
     * @return The {@link String} value
     */
    @Override
    public String read(String arg) {
        return arg;
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
        return "String";
    }
}
