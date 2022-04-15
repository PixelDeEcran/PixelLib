package fr.pixeldeecran.pixellib.particle;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Supplier;

public class ParticlesManager {

    private final JavaPlugin plugin;

    private final Map<String, ParticleInstance> particles = new HashMap<>();

    public ParticlesManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        try {
            Class.forName("com.comphenix.protocol.ProtocolLibrary");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("It seems ProtocolLib is not loaded. Please, add ProtocolLib to the depends of your plugin.", e);
        }

        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            Iterator<String> iterator = this.particles.keySet().iterator();
            while (iterator.hasNext()) {
                ParticleInstance particleInstance = this.particles.get(iterator.next());

                List<Player> viewers = particleInstance.getViewersSupplier().get();
                if (viewers == null) {
                    viewers = new ArrayList<>();
                }

                if (!particleInstance.getParticle().tick(viewers, particleInstance.getTime())) {
                    iterator.remove();
                }
            }
        }, 1L, 1L);
    }

    public void addParticle(String id, IParticle particle, Supplier<List<Player>> viewersSupplier) {
        this.particles.put(id, new ParticleInstance(particle, viewersSupplier));
    }

    public void removeParticle(String id) {
        this.particles.remove(id);
    }

    public boolean doesParticleExist(String id) {
        return this.particles.containsKey(id);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Map<String, ParticleInstance> getParticles() {
        return particles;
    }
}
