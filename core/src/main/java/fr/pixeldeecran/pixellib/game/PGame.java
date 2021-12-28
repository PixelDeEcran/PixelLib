package fr.pixeldeecran.pixellib.game;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public abstract class PGame<T extends Enum<T> & IEnumGameState<?>, S extends Enum<S>> implements Listener {

    private final PGameManager gamesManager;
    private final Map<Player, S> players;
    private final Map<T, PGameStateManager<?>> stateManagers;
    private T currentState;

    public PGame(PGameManager gamesManager) {
        this.gamesManager = gamesManager;
        this.players = new HashMap<>();
        this.stateManagers = new HashMap<>();
        this.currentState = this.getDefaultState(); // No real use
    }

    public abstract void start();

    public abstract void onEnded();

    public void onPlayerLeft(Player player) {
        this.players.put(player, this.getDefaultPlayerState());

        if (this.currentState != null && this.stateManagers.containsKey(this.currentState)) {
            this.stateManagers.get(this.currentState).onPlayerLeft(player);
        }
    }

    public abstract T getDefaultState();

    public abstract S getDefaultPlayerState();

    public void end() {
        if (this.stateManagers.containsKey(this.currentState)) {
            this.stateManagers.get(this.currentState).onDisable();
        }
        this.stateManagers.values().forEach(HandlerList::unregisterAll);
        this.gamesManager.endGame(this);
    }

    public void addPlayer(Player player) {
        this.players.put(player, this.getDefaultPlayerState());

        if (this.currentState != null && this.stateManagers.containsKey(this.currentState)) {
            this.stateManagers.get(this.currentState).onPlayerJoin(player);
        }
    }

    public void setPlayerState(Player player, S state) {
        this.players.put(player, state);
    }

    public void setState(T newState) {
        if (this.stateManagers.containsKey(this.currentState)) {
            HandlerList.unregisterAll(this.stateManagers.get(this.currentState));
            this.stateManagers.get(this.currentState).onDisable();
        }

        this.currentState = newState;

        PGameStateManager<?> manager;
        if (this.stateManagers.containsKey(newState)) {
            manager = this.stateManagers.get(newState);
        } else {
            manager = newState.createManager(this);
            this.stateManagers.put(newState, manager);
        }
        this.gamesManager.getPlugin().getServer().getPluginManager().registerEvents(manager, this.gamesManager.getPlugin());
        manager.onEnable();
    }

    public PGameManager getGamesManager() {
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

    public Map<T, PGameStateManager<?>> getStateManagers() {
        return stateManagers;
    }

    public T getState() {
        return currentState;
    }
}
