package fr.pixeldeecran.pipilibtest;

import fr.pixeldeecran.pipilib.PPlugin;
import fr.pixeldeecran.pipilibtest.commands.EcoCommand;

public class TestPlugin extends PPlugin {

    @Override
    public void onEnable() {
        this.getCommandRegistry().registerAllCommandsIn(EcoCommand.class.getPackage().getName());
    }
}
