package fr.pixeldeecran.pipilib.command;

import org.bukkit.command.CommandSender;

/**
 * Represents the context of the command. This keep track of a lot of information.
 */
public class PCommandContext {

    /**
     * The current command.
     *
     * @see PCommandContext#setCurrentCommand(PCommand)
     * @see PCommandContext#getCurrentCommand()
     */
    private PCommand currentCommand;

    /**
     * The current command sender.
     *
     * @see PCommandContext#setCurrentSender(CommandSender)
     * @see PCommandContext#getCurrentSender()
     */
    private CommandSender currentSender;

    /**
     * The current arguments.
     *
     * @see PCommandContext#setCurrentArgs(String[])
     * @see PCommandContext#getCurrentArgs()
     */
    private String[] currentArgs;

    /**
     * The current index.
     *
     * @see PCommandContext#setCurrentIndex(int)
     * @see PCommandContext#getCurrentIndex()
     */
    private int currentIndex;

    /**
     * Are we currently reading a sentence?
     *
     * @see PCommandContext#setReadingSentence(boolean)
     * @see PCommandContext#isReadingSentence()
     */
    private boolean isReadingSentence;

    /**
     * The current error.
     *
     * @see PCommandContext#setCurrentError(String)
     * @see PCommandContext#getCurrentError()
     */
    private String currentError;

    /**
     * Setter of {@link PCommandContext#currentCommand}.
     *
     * @param currentCommand The new current command
     * @return The context for chaining
     */
    public PCommandContext setCurrentCommand(PCommand currentCommand) {
        this.currentCommand = currentCommand;
        return this;
    }

    /**
     * Setter of {@link PCommandContext#currentSender}.
     *
     * @param currentSender The new current command sender
     * @return The context for chaining
     */
    public PCommandContext setCurrentSender(CommandSender currentSender) {
        this.currentSender = currentSender;
        return this;
    }

    /**
     * Setter of {@link PCommandContext#currentArgs}.
     *
     * @param currentArgs The new current arguments
     * @return The context for chaining
     */
    public PCommandContext setCurrentArgs(String[] currentArgs) {
        this.currentArgs = currentArgs;
        return this;
    }

    /**
     * Setter of {@link PCommandContext#currentIndex}.
     *
     * @param currentIndex The new current index
     * @return The context for chaining
     */
    public PCommandContext setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        return this;
    }

    /**
     * Setter of {@link PCommandContext#isReadingSentence}.
     *
     * @param readingSentence Are we now reading a sentence?
     * @return The context for chaining
     */
    public PCommandContext setReadingSentence(boolean readingSentence) {
        isReadingSentence = readingSentence;
        return this;
    }

    /**
     * Setter of {@link PCommandContext#currentError}.
     *
     * @param currentError The new current error
     * @return The context for chaining
     */
    public PCommandContext setCurrentError(String currentError) {
        this.currentError = currentError;
        return this;
    }

    /**
     * Getter of {@link PCommandContext#currentCommand}.
     *
     * @return The current command
     */
    public PCommand getCurrentCommand() {
        return currentCommand;
    }

    /**
     * Getter of {@link PCommandContext#currentSender}.
     *
     * @return The current command sender
     */
    public CommandSender getCurrentSender() {
        return currentSender;
    }

    /**
     * Getter of {@link PCommandContext#currentArgs}.
     *
     * @return The current arguments
     */
    public String[] getCurrentArgs() {
        return currentArgs;
    }

    /**
     * Getter of {@link PCommandContext#currentIndex}.
     *
     * @return The current index
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Getter of {@link PCommandContext#isReadingSentence}.
     *
     * @return Are we currently reading a sentence?
     */
    public boolean isReadingSentence() {
        return isReadingSentence;
    }

    /**
     * Getter of {@link PCommandContext#currentError}.
     *
     * @return The current error
     */
    public String getCurrentError() {
        return currentError;
    }
}
