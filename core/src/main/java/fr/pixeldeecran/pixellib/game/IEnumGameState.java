package fr.pixeldeecran.pixellib.game;

import java.lang.reflect.InvocationTargetException;

public interface IEnumGameState<T extends PGame<?, ?>> {

    Class<? extends PGameStateManager<T>> getManagerClass();

    default PGameStateManager<T> createManager(PGame<?, ?> game) {
        try {
            return this.getManagerClass().getConstructor(game.getClass()).newInstance(game);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
