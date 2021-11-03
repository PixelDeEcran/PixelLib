package fr.pixeldeecran.pixellib.command.arg;

/**
 * Represents the {@link Character} argument parser.
 */
public class CharPAR implements PArgReader<Character> {

    /**
     * Parse an argument as a {@link Character}.
     *
     * @param arg The argument
     * @return The {@link Character} value
     */
    @Override
    public Character read(String arg) {
        return arg.length() > 0 ? arg.charAt(0) : null;
    }

    /**
     * Return the name of the error. <br>
     * With a {@link CharPAR}, the errors can be : <br>
     * - "EMPTY_STRING" : The argument is an empty {@link String} -> its length is 0
     *
     * @param arg The argument
     * @return The error name
     */
    @Override
    public String errorCause(String arg) {
        return "EMPTY_STRING";
    }

    /**
     * @return The display name
     */
    @Override
    public String getDisplayName() {
        return "Character";
    }
}
