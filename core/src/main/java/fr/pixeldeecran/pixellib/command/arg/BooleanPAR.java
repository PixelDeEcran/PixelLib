package fr.pixeldeecran.pixellib.command.arg;

/**
 * Represents the {@link Boolean} argument parser.
 */
public class BooleanPAR implements PArgReader<Boolean> {

    /**
     * Parse an argument as a {@link Boolean}.
     *
     * @param arg The argument
     * @return The {@link Boolean} value
     */
    @Override
    public Boolean read(String arg) {
        if (arg != null) {
            if (arg.equalsIgnoreCase("true")) {
                return true;
            } else if (arg.equalsIgnoreCase("false")) {
                return false;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Return the name of the error. <br>
     * With a {@link BooleanPAR}, the errors can be : <br>
     * - "ARG_NULL" : The argument is null <br>
     * - "BOOLEAN_NON_VALID_FORMAT" : The argument is not a valid {@link Boolean}
     *
     * @param arg The argument
     * @return The error name
     */
    @Override
    public String errorCause(String arg) {
        return arg == null ? "ARG_NULL" : "BOOLEAN_NON_VALID_FORMAT";
    }

    /**
     * @return The display name
     */
    @Override
    public String getDisplayName() {
        return "Boolean";
    }
}
