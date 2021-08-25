package fr.pixeldeecran.pipilib.command.sentence;

public interface PSentenceReader<T> {

    T read(String[] sentence);

    String errorCause(String[] sentence);

    String getDisplayName();
}
