package fr.pixellib.example;

import fr.pixeldeecran.pixellib.boostrap.PBootstrapPlugin;

public class BootstrapPlugin extends PBootstrapPlugin {

    @Override
    public void onEnable() {
        this.requireByteBuddyAgent();

        this.disablePlugin(this);
        this.startPlugin("bootstrap.yml");
    }
}
