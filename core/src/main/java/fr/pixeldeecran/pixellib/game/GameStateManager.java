package fr.pixeldeecran.pixellib.game;

import org.bukkit.event.Listener;

public abstract class GameStateManager<T extends Game<?, ?>> implements Listener {

    private final T game;

    public GameStateManager(T game) {
        this.game = game;
    }

    public abstract void onEnable();

    public void onDisable() {}

    public T getGame() {
        return game;
    }
}
