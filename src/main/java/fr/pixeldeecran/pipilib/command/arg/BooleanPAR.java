package fr.pixeldeecran.pipilib.command.arg;

import fr.pixeldeecran.pipilib.utils.INullable;

public class BooleanPAR implements PArgReader<Boolean>, INullable {

    @Override
    public Boolean read(String arg) {
        if (arg != null) {
            if (arg.equalsIgnoreCase("true")) {
                return true;
            } else if (arg.equalsIgnoreCase("false")) {
                return false;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String errorCause(String arg) {
        return arg == null ? "ARG_NULL" : "BOOLEAN_NON_VALID_FORMAT";
    }

    @Override
    public String getDisplayName() {
        return "Boolean";
    }
}
