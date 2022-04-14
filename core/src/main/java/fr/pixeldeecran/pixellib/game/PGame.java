package fr.pixeldeecran.pixellib.game;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public abstract class PGame<T extends Enum<T> & IEnumGameState<?>, S extends Enum<S> & IEnumPlayerState<?>> implements Listener {

    private final PGameManager gameManager;
    private final Map<Player, S> players;
    private final Map<T, PGameStateManager<?>> gameStateManagers;
    private final Map<S, PPlayerStateManager<?>> playersStateManagers;
    private T currentGameState;

    public PGame(PGameManager gameManager) {
        this.gameManager = gameManager;
        this.players = new HashMap<>();
        this.gameStateManagers = new HashMap<>();
        this.playersStateManagers = new HashMap<>();
        this.currentGameState = this.getDefaultState(); // No real use
    }

    public abstract void start();

    public abstract void onEnded();

    public void onPlayerLeft(Player player) {
        this.playersStateManagers.get(this.players.get(player)).onQuit(player);

        if (this.currentGameState != null && this.gameStateManagers.containsKey(this.currentGameState)) {
            this.gameStateManagers.get(this.currentGameState).onPlayerLeft(player);
        }
    }

    public abstract T getDefaultState();

    public abstract S getDefaultPlayerState();

    public void end() {
        if (this.gameStateManagers.containsKey(this.currentGameState)) {
            this.gameStateManagers.get(this.currentGameState).onDisable();
        }
        this.gameStateManagers.values().forEach(HandlerList::unregisterAll);

        this.playersStateManagers.values().forEach(HandlerList::unregisterAll);
    }

    public void addPlayer(Player player) {
        if (this.players.containsKey(player)) {
            this.playersStateManagers.get(this.players.get(player)).onRejoin(player);
        } else {
            this.setPlayerState(player, this.getDefaultPlayerState());
        }


        if (this.currentGameState != null && this.gameStateManagers.containsKey(this.currentGameState)) {
            this.gameStateManagers.get(this.currentGameState).onPlayerJoin(player);
        }
    }

    public void setPlayerState(Player player, S state) {
        if (this.players.containsKey(player)) {
            S oldState = this.players.remove(player);
            this.playersStateManagers.get(oldState).onStateLeft(player);
        }

        this.players.put(player, state);

        PPlayerStateManager<?> manager;
        if (this.playersStateManagers.containsKey(state)) {
            manager = this.playersStateManagers.get(state);
        } else {
            try {
                manager = state.getStateManager();
                manager.setup(this, state);
                this.gameManager.getPlugin().getServer().getPluginManager().registerEvents(manager, this.gameManager.getPlugin());
                this.playersStateManagers.put(state, manager);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }


        manager.onStateJoined(player);
    }

    public void setGameState(T newState) {
        if (this.gameStateManagers.containsKey(this.currentGameState)) {
            HandlerList.unregisterAll(this.gameStateManagers.get(this.currentGameState));
            this.gameStateManagers.get(this.currentGameState).onDisable();
        }

        this.currentGameState = newState;

        PGameStateManager<?> manager;
        if (this.gameStateManagers.containsKey(newState)) {
            manager = this.gameStateManagers.get(newState);
        } else {
            manager = newState.createManager(this);
            this.gameStateManagers.put(newState, manager);
        }
        this.gameManager.getPlugin().getServer().getPluginManager().registerEvents(manager, this.gameManager.getPlugin());
        manager.onEnable();
    }

    public <M extends PGameStateManager<?>> M getCurrentGameStateManager() {
        return (M) this.gameStateManagers.get(this.currentGameState);
    }

    public PGameManager getGameManager() {
        return gameManager;
    }

    public WorldGameService getWorldService() {
        return this.gameManager.getWorldService();
    }

    public S getPlayerState(Player player) {
        return this.players.get(player);
    }

    public Map<Player, S> getPlayers() {
        return players;
    }

    public Map<T, PGameStateManager<?>> getGameStateManagers() {
        return gameStateManagers;
    }

    public Map<S, PPlayerStateManager<?>> getPlayersStateManagers() {
        return playersStateManagers;
    }

    public T getGameState() {
        return currentGameState;
    }
}
