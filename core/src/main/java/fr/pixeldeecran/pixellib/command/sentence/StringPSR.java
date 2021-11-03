package fr.pixeldeecran.pixellib.command.sentence;

import fr.pixeldeecran.pixellib.command.arg.StringPAR;

/**
 * Represents the {@link String} sentence parser.
 */
public class StringPSR implements PSentenceReader<String> {

    /**
     * Parse a sentence.
     *
     * @param sentence The sentence
     * @return The value parsed
     */
    @Override
    public String read(String[] sentence) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sentence.length; i++) {
            builder.append(sentence[i]);

            if (i < sentence.length - 1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    /**
     * Return the name of the error. <br>
     * With a {@link StringPAR}, the errors can be : <br>
     * - "CRITICAL" : An unexpected error
     *
     * @param sentence The sentence
     * @return The error name
     */
    @Override
    public String errorCause(String[] sentence) {
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
