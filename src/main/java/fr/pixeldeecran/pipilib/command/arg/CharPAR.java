package fr.pixeldeecran.pipilib.command.arg;

public class CharPAR implements PArgReader<Character> {

    @Override
    public Character read(String arg) {
        return arg.length() > 0 ? arg.charAt(0) : null;
    }

    @Override
    public String errorCause(String arg) {
        return "EMPTY_STRING";
    }

    @Override
    public String getDisplayName() {
        return "Character";
    }
}
