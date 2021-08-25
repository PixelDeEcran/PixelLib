package fr.pixeldeecran.pipilib.command.arg;

public interface PArgReader<T> {

    T read(String arg);

    String errorCause(String arg);

    String getDisplayName();
}
