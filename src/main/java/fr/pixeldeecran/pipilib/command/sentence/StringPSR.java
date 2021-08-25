package fr.pixeldeecran.pipilib.command.sentence;

public class StringPSR implements PSentenceReader<String> {

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

    @Override
    public String errorCause(String[] sentence) {
        return "CRITICAL";
    }

    @Override
    public String getDisplayName() {
        return "String";
    }
}
