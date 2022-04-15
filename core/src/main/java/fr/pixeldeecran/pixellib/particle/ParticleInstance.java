package fr.pixeldeecran.pixellib.particle;

import lombok.Data;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

@Data
public class ParticleInstance {

    private final IParticle particle;
    private final Supplier<List<Player>> viewersSupplier;
    private int time;
}
