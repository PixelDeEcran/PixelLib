package fr.pixeldeecran.pixellib.game;

import java.lang.reflect.InvocationTargetException;

public interface IEnumGameState<T extends Game<?, ?>> {

    Class<? extends GameStateManager<T>> getManagerClass();

    default GameStateManager<T> createManager(Game<?, ?> game) {
        try {
            return this.getManagerClass().getConstructor(game.getClass()).newInstance(game);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
