package fr.pixeldeecran.pipilib.command.arg;

/**
 * Represents an argument reader.
 *
 * @param <T> The value that we want to obtain after parsing the argument
 */
public interface PArgReader<T> {

    /**
     * Parse an argument.
     *
     * @param arg The argument
     * @return The value parsed
     */
    T read(String arg);

    /**
     * Compute the error according to the specified argument. This function will only be called if the argument
     * is a required one and that the returned value is null.
     *
     * @param arg The argument
     * @return The error name
     */
    String errorCause(String arg);

    /**
     * @return The display name
     */
    String getDisplayName();
}
