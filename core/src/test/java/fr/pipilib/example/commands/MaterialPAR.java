package fr.pipilib.example.commands;

import fr.pixeldeecran.pipilib.command.arg.PArgReader;
import org.bukkit.Material;

public class MaterialPAR implements PArgReader<Material> {

    @Override
    public Material read(String arg) {
        try {
            return Material.matchMaterial(arg);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String errorCause(String arg) {
        return "UNKNOWN_MATERIAL";
    }

    @Override
    public String getDisplayName() {
        return "Material";
    }
}
