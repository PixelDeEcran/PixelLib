package fr.pixeldeecran.pipilib.command.arg.numbers;

public class ShortPAR extends NumberPAR<Short> {

    @Override
    public Short valueOf(String string) {
        return Short.parseShort(string);
    }

    @Override
    public String errorCause(String arg) {
        return "SHORT_NON_VALID_FORMAT";
    }

    @Override
    public String getDisplayName() {
        return "Short";
    }
}
