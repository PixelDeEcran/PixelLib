package fr.pipilib.example;

import fr.pipilib.example.commands.EcoCommand;
import fr.pipilib.example.commands.ExampleCommand;
import fr.pipilib.example.commands.MaterialPAR;
import fr.pixeldeecran.pipilib.PPlugin;
import org.bukkit.Material;

public class ExamplePlugin extends PPlugin {

    @Override
    public void onEnable() {
        // Register our custom types
        this.getCommandRegistry().registerArgReader(Material.class, new MaterialPAR());

        // Register our commands
        this.getCommandRegistry().registerAllCommandsIn(ExampleCommand.class.getPackage().getName());
    }
}
