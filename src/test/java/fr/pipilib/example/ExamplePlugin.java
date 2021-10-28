package fr.pipilib.example;

import fr.pipilib.example.commands.EcoCommand;
import fr.pipilib.example.commands.ExampleCommand;
import fr.pipilib.example.commands.MaterialPAR;
import fr.pixeldeecran.pipilib.PPlugin;
import org.bukkit.Material;

public class ExamplePlugin extends PPlugin {

    @Override
    public void onEnable() {
        // Register our commands
        this.registerAllCommandsIn("fr.pipilib.example.commands");
    }
}
