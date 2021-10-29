package fr.pixeldeecran.pipilib.command.sentence;

/**
 * Represents a sentence reader. A sentence is considered as a group of arguments.
 *
 * @param <T> The value that we want to obtain after parsing the sentence
 */
public interface PSentenceReader<T> {

    /**
     * Parse a sentence.
     *
     * @param sentence The sentence
     * @return The value parsed
     */
    T read(String[] sentence);

    /**
     * Compute the error according to the specified sentence. This function will only be called if the sentence
     * is a required one and that the returned value is null.
     *
     * @param sentence The sentence
     * @return The error name
     */
    String errorCause(String[] sentence);

    /**
     * @return The display name
     */
    String getDisplayName();
}
