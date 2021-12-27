package fr.pixeldeecran.pixellib.game;

import org.bukkit.event.Listener;

public abstract class PGameStateManager<T extends PGame<?, ?>> implements Listener {

    private final T game;

    public PGameStateManager(T game) {
        this.game = game;
    }

    public abstract void onEnable();

    public void onDisable() {}

    public T getGame() {
        return game;
    }
}
