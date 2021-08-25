package fr.pixeldeecran.pipilib.command.arg;

public class StringPAR implements PArgReader<String> {

    @Override
    public String read(String arg) {
        return arg;
    }

    @Override
    public String errorCause(String arg) {
        return "CRITICAL";
    }

    @Override
    public String getDisplayName() {
        return "String";
    }
}
