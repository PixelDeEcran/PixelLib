package fr.pipilib.example;

import fr.pixeldeecran.pipilib.PPlugin;
import net.bytebuddy.agent.ByteBuddyAgent;

public class ExamplePlugin extends PPlugin {

    @Override
    public void onEnable() {
        // Register our commands
        this.registerAllCommandsIn("fr.pipilib.example.commands");

        ByteBuddyAgent.install();
    }
}
