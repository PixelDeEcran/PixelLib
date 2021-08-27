package fr.pixeldeecran.pipilib.command;

/**
 * Represents when an error occurs. This is used in order to avoid a lot of if conditions, so that when we think an
 * error is occurring (for example, requiring a player, or reading an integer but the specified argument is not a number),
 * we can throw this error and handle it in the {@link PCommandErrorHandler}.
 */
public class PCommandException extends RuntimeException {

    /**
     * Constructor of {@link PCommandException}.
     *
     * @param errorName The name of the error
     */
    public PCommandException(String errorName) {
        super(errorName);
    }

    /**
     * Constructor of {@link PCommandException}. This constructor is mostly called when an unexpected error occurs.
     *
     * @param errorName The name of the error
     * @param cause The cause of the error
     */
    public PCommandException(String errorName, Throwable cause) {
        super(errorName, cause);
    }
}
