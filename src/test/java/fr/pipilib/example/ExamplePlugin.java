package fr.pipilib.example;

import fr.pipilib.example.commands.EcoCommand;
import fr.pixeldeecran.pipilib.PPlugin;

public class ExamplePlugin extends PPlugin {

    @Override
    public void onEnable() {
        this.getCommandRegistry().registerAllCommandsIn(EcoCommand.class.getPackage().getName());
    }
}
