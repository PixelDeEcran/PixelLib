package fr.pixeldeecran.pixellib.game;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class GamesManager {

    private final JavaPlugin plugin;
    private final WorldGameService worldService;

    public GamesManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.worldService = new WorldGameService(plugin);
    }

    public abstract void endGame(Game<?, ?> game);

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public WorldGameService getWorldService() {
        return worldService;
    }
}
