package fr.pixellib.example;

import fr.pixeldeecran.pixellib.PPlugin;

public class ExamplePlugin extends PPlugin {

    @Override
    public void onEnable() {
        // Register our commands
        this.registerAllCommandsIn("fr.pixellib.example.commands");
    }
}
