package fr.pixeldeecran.pipilib.command;

public class PCommandException extends RuntimeException {

    public PCommandException(String reason) {
        super(reason);
    }

    public PCommandException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
