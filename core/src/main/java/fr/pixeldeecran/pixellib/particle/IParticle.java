package fr.pixeldeecran.pixellib.particle;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

public interface IParticle {

    default void sendParticlePacket(Player player, PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Error while sending packet " + packet.getType().name()
                + " to " + player.getName() + ": ", e);
        }
    }

    default void sendParticlePacket(Collection<Player> players, PacketContainer packet) {
        players.forEach(player -> this.sendParticlePacket(player, packet));
    }

    default PacketContainer getBasicParticlePacket(EnumWrappers.Particle particleType, float x, float y, float z) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.WORLD_PARTICLES);
        packet.getModifier().writeDefaults();
        packet.getParticles().write(0, particleType);
        packet.getFloat().write(0, x).write(1, y).write(2, z);
        return packet;
    }

    default PacketContainer getColoredDustParticlePacket(float x, float y, float z, float red, float green,
                                                         float blue, float scale) {
        PacketContainer packet = this.getBasicParticlePacket(EnumWrappers.Particle.REDSTONE, x, y, z);
        packet.getFloat().write(3, red)
            .write(4, green)
            .write(5, blue)
            .write(6, scale);
        return packet;
    }

    boolean tick(List<Player> viewers, int time);
}
