package fr.pixeldeecran.pipilib.command.arg.numbers;

public class IntegerPAR extends NumberPAR<Integer> {

    @Override
    public Integer valueOf(String string) {
        return Integer.parseInt(string);
    }

    @Override
    public String errorCause(String arg) {
        return "INTEGER_NON_VALID_FORMAT";
    }

    @Override
    public String getDisplayName() {
        return "Integer";
    }
}
