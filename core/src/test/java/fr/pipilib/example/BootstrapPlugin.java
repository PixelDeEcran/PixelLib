package fr.pipilib.example;

import fr.pixeldeecran.pipilib.boostrap.PBootstrapPlugin;

public class BootstrapPlugin extends PBootstrapPlugin {

    @Override
    public void onEnable() {
        this.requireByteBuddyAgent();

        this.disablePlugin(this);
        this.startPlugin("bootstrap.yml");
    }
}
