package fr.pixeldeecran.pipilib.command.arg.numbers;

import fr.pixeldeecran.pipilib.command.arg.PArgReader;

public abstract class NumberPAR<T extends Number> implements PArgReader<T> {

    @Override
    public T read(String arg) {
        try {
            return valueOf(arg);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public abstract T valueOf(String string);
}
