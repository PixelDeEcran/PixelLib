package fr.pixeldeecran.pixellib.command.arg.numbers;

/**
 * Represents the {@link Byte} argument parser.
 */
public class BytePAR extends NumberPAR<Byte> {

    /**
     * Parse a {@link String} as a {@link Byte}
     *
     * @param string The {@link String} to parse
     * @return The {@link Byte} value
     */
    @Override
    public Byte valueOf(String string) {
        return Byte.parseByte(string);
    }

    /**
     * Return the name of the error. <br>
     * With a {@link BytePAR}, the errors can be : <br>
     * - "BYTE_NON_VALID_FORMAT" : The argument is not a valid {@link Byte}
     *
     * @param arg The argument
     * @return The error name
     */
    @Override
    public String errorCause(String arg) {
        return "BYTE_NON_VALID_FORMAT";
    }

    /**
     * @return The display name
     */
    @Override
    public String getDisplayName() {
        return "Byte";
    }
}
