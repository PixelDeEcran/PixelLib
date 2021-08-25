package fr.pixeldeecran.pipilib.command.arg.numbers;

public class LongPAR extends NumberPAR<Long> {

    @Override
    public Long valueOf(String string) {
        return Long.parseLong(string);
    }

    @Override
    public String errorCause(String arg) {
        return "LONG_NON_VALID_FORMAT";
    }

    @Override
    public String getDisplayName() {
        return "Long";
    }
}
