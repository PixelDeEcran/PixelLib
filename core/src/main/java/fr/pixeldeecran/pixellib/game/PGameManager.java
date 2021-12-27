package fr.pixeldeecran.pixellib.game;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class PGameManager {

    private final JavaPlugin plugin;
    private final WorldGameService worldService;

    public PGameManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.worldService = new WorldGameService(plugin);
    }

    public abstract void endGame(PGame<?, ?> game);

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public WorldGameService getWorldService() {
        return worldService;
    }
}
