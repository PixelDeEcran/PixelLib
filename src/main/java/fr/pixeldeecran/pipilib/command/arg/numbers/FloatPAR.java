package fr.pixeldeecran.pipilib.command.arg.numbers;

public class FloatPAR extends NumberPAR<Float> {

    @Override
    public Float valueOf(String string) {
        return Float.parseFloat(string);
    }

    @Override
    public String errorCause(String arg) {
        return "FLOAT_NON_VALID_FORMAT";
    }

    @Override
    public String getDisplayName() {
        return "Float";
    }
}
