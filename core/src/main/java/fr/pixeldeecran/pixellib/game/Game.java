package fr.pixeldeecran.pixellib.game;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public abstract class Game<T extends Enum<T> & IEnumGameState<?>, S extends Enum<S>> implements Listener {

    private final GamesManager gamesManager;
    private final Map<Player, S> players;
    private final Map<T, GameStateManager<?>> stateManager;
    private T currentState;

    public Game(GamesManager gamesManager) {
        this.gamesManager = gamesManager;
        this.players = new HashMap<>();
        this.stateManager = new HashMap<>();
        this.currentState = this.getDefaultState(); // No real use
    }

    public abstract void start();

    public abstract void onEnded();

    public abstract boolean isAvailable(); // define a game can still accept players

    public void onPlayerLeft(Player player) {
        this.players.remove(player);
    }

    public abstract T getDefaultState();

    public abstract S getDefaultPlayerState();

    public abstract String getDisplayName();

    public void end() {
        if (this.stateManager.containsKey(this.currentState)) {
            this.stateManager.get(this.currentState).onDisable();
        }
        this.stateManager.values().forEach(HandlerList::unregisterAll);
        this.gamesManager.endGame(this);
    }

    public void addPlayer(Player player) {
        this.players.put(player, this.getDefaultPlayerState());
    }

    public void setPlayerState(Player player, S state) {
        this.players.put(player, state);
    }

    public void setState(T newState) {
        if (this.stateManager.containsKey(this.currentState)) {
            HandlerList.unregisterAll(this.stateManager.get(this.currentState));
            this.stateManager.get(this.currentState).onDisable();
        }

        this.currentState = newState;

        GameStateManager<?> manager;
        if (this.stateManager.containsKey(newState)) {
            manager = this.stateManager.get(newState);
        } else {
            manager = newState.createManager(this);
            this.stateManager.put(newState, manager);
        }
        this.gamesManager.getPlugin().getServer().getPluginManager().registerEvents(manager, this.gamesManager.getPlugin());
        manager.onEnable();
    }

    public GamesManager getGamesManager() {
        return gamesManager;
    }

    public WorldGameService getWorldService() {
        return this.gamesManager.getWorldService();
    }

    public S getPlayerState(Player player) {
        return this.players.get(player);
    }

    public Map<Player, S> getPlayers() {
        return players;
    }

    public Map<T, GameStateManager<?>> getStateManager() {
        return stateManager;
    }

    public T getState() {
        return currentState;
    }
}
