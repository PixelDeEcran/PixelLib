package fr.pixeldeecran.pixellib.game;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WorldGameService {

    private final JavaPlugin plugin;
    private final Map<PGame<?, ?>, String> worlds;

    public WorldGameService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.worlds = new HashMap<>();
    }

    public String getAvailableWorldName() {
        int i = 0;
        String worldName;
        do {
            worldName = "Game-" + i++;
        } while (Bukkit.getWorld(worldName) != null);
        return worldName;
    }

    public World createWorld(PGame<?, ?> Game, WorldCreator worldCreator) {
        return this.reserveWorld(Game, new WorldCreator(this.getAvailableWorldName()).copy(worldCreator).createWorld());
    }

    public World createEmptyWorld(PGame<?, ?> Game) {
        return this.createWorld(Game, new WorldCreator("").generator(new EmptyChunkGenerator()));
    }

    public Optional<World> createWorldFromTemplate(PGame<?, ?> Game, String worldPath) {
        File root = this.plugin.getServer().getWorldContainer().getAbsoluteFile();
        File source = new File(root, worldPath);
        File destination = new File(root, this.getAvailableWorldName());
        try {
            FileUtils.copyDirectory(source, destination);
            return Optional.of(this.createWorld(Game, new WorldCreator("")));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public World reserveWorld(PGame<?, ?> Game, World world) {
        this.worlds.put(Game, world.getName());
        return world;
    }

    public Optional<World> getWorld(PGame<?, ?> Game) {
        if (!this.worlds.containsKey(Game)) {
            return Optional.empty();
        } else {
            return Optional.of(Bukkit.getWorld(this.worlds.get(Game)));
        }
    }

    public void deleteWorld(PGame<?, ?> Game) {
        String worldName = this.worlds.remove(Game);
        File worldFolder = Bukkit.getWorld(worldName).getWorldFolder();
        Bukkit.getServer().unloadWorld(worldName, true);
        try {
            FileUtils.deleteDirectory(worldFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<PGame<?, ?>, String> getWorlds() {
        return worlds;
    }

    public static class EmptyChunkGenerator extends ChunkGenerator {

        @Override
        public List<BlockPopulator> getDefaultPopulators(World world) {
            return Collections.emptyList();
        }

        @Override
        public boolean canSpawn(World world, int x, int z) {
            return true;
        }

        @Override
        public byte[] generate(World world, Random random, int x, int z) {
            return new byte[16 * 16 * 128];
        }

        @Override
        public Location getFixedSpawnLocation(World world, Random random) {
            return new Location(world, 0, 128, 0);
        }
    }
}
