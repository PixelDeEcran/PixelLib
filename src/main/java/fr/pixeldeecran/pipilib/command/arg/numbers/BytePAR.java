package fr.pixeldeecran.pipilib.command.arg.numbers;

public class BytePAR extends NumberPAR<Byte> {

    @Override
    public Byte valueOf(String string) {
        return Byte.parseByte(string);
    }

    @Override
    public String errorCause(String arg) {
        return "BYTE_NON_VALID_FORMAT";
    }

    @Override
    public String getDisplayName() {
        return "Byte";
    }
}
