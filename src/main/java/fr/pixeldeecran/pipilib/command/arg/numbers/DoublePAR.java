package fr.pixeldeecran.pipilib.command.arg.numbers;

public class DoublePAR extends NumberPAR<Double> {

    @Override
    public Double valueOf(String string) {
        return Double.parseDouble(string);
    }

    @Override
    public String errorCause(String arg) {
        return "DOUBLE_NON_VALID_FORMAT";
    }

    @Override
    public String getDisplayName() {
        return null;
    }
}
