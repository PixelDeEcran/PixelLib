package fr.pixeldeecran.pipilib.command;

import org.bukkit.command.CommandSender;

public class PCommandContext {

    private PCommand currentCommand;
    private CommandSender currentSender;
    private String[] currentArgs;
    private int currentIndex;
    private boolean isReadingSentence;
    private String currentError;

    public PCommandContext setCurrentCommand(PCommand currentCommand) {
        this.currentCommand = currentCommand;
        return this;
    }

    public PCommandContext setCurrentSender(CommandSender currentSender) {
        this.currentSender = currentSender;
        return this;
    }

    public PCommandContext setCurrentArgs(String[] currentArgs) {
        this.currentArgs = currentArgs;
        return this;
    }

    public PCommandContext setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        return this;
    }

    public PCommandContext setReadingSentence(boolean readingSentence) {
        isReadingSentence = readingSentence;
        return this;
    }

    public PCommandContext setCurrentError(String currentError) {
        this.currentError = currentError;
        return this;
    }

    public PCommand getCurrentCommand() {
        return currentCommand;
    }

    public CommandSender getCurrentSender() {
        return currentSender;
    }

    public String[] getCurrentArgs() {
        return currentArgs;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean isReadingSentence() {
        return isReadingSentence;
    }

    public String getCurrentError() {
        return currentError;
    }
}
