package fr.pixeldeecran.pixellib.command;

import fr.pixeldeecran.pixellib.command.arg.PArgReader;
import fr.pixeldeecran.pixellib.command.sentence.PSentenceReader;
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
     * The current {@link Action}.
     *
     * @see PCommandContext#setCurrentAction(Action)
     * @see PCommandContext#getCurrentAction()
     */
    private Action currentAction;

    /**
     * The current permission.
     *
     * @see PCommandContext#setCurrentPermission(String)
     * @see PCommandContext#getCurrentPermission()
     */
    private String currentPermission;

    /**
     * The current {@link PArgReader}.
     *
     * @see PCommandContext#setCurrentArgReader(PArgReader)
     * @see PCommandContext#getCurrentArgReader()
     */
    private PArgReader<?> currentArgReader;

    /**
     * The current {@link PSentenceReader}.
     *
     * @see PCommandContext#setCurrentSentenceReader(PSentenceReader)
     * @see PCommandContext#getCurrentSentenceReader()
     */
    private PSentenceReader<?> currentSentenceReader;

    /**
     * The current error.
     *
     * @see PCommandContext#setCurrentError(String)
     * @see PCommandContext#getCurrentError()
     */
    private String currentError;

    /**
     * Getter of {@link PCommandContext#currentCommand}.
     *
     * @return The current command
     */
    public PCommand getCurrentCommand() {
        return currentCommand;
    }

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
     * Getter of {@link PCommandContext#currentSender}.
     *
     * @return The current command sender
     */
    public CommandSender getCurrentSender() {
        return currentSender;
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
     * Getter of {@link PCommandContext#currentArgs}.
     *
     * @return The current arguments
     */
    public String[] getCurrentArgs() {
        return currentArgs;
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
     * Getter of {@link PCommandContext#currentIndex}.
     *
     * @return The current index
     */
    public int getCurrentIndex() {
        return currentIndex;
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
     * Getter of {@link PCommandContext#currentAction}.
     *
     * @return The current {@link Action}
     */
    public Action getCurrentAction() {
        return currentAction;
    }

    /**
     * Setter of {@link PCommandContext#currentAction}.
     *
     * @param currentAction The new current {@link Action}
     * @return The context for chaining
     */
    public PCommandContext setCurrentAction(Action currentAction) {
        this.currentAction = currentAction;
        return this;
    }

    /**
     * Getter of {@link PCommandContext#currentPermission}.
     *
     * @return The current permission
     */
    public String getCurrentPermission() {
        return currentPermission;
    }

    /**
     * Setter of {@link PCommandContext#currentPermission}.
     *
     * @param currentPermission The new current permission
     * @return The context for chaining
     */
    public PCommandContext setCurrentPermission(String currentPermission) {
        this.currentPermission = currentPermission;
        return this;
    }

    /**
     * Getter of {@link PCommandContext#currentArgReader}.
     *
     * @return The current {@link PArgReader}
     */
    public PArgReader<?> getCurrentArgReader() {
        return currentArgReader;
    }

    /**
     * Setter of {@link PCommandContext#currentArgReader}.
     *
     * @param currentArgReader The new current {@link PArgReader}
     * @return The context for chaining
     */
    public PCommandContext setCurrentArgReader(PArgReader<?> currentArgReader) {
        this.currentArgReader = currentArgReader;
        return this;
    }

    /**
     * Getter of {@link PCommandContext#currentSentenceReader}.
     *
     * @return The current {@link PSentenceReader}
     */
    public PSentenceReader<?> getCurrentSentenceReader() {
        return currentSentenceReader;
    }

    /**
     * Setter of {@link PCommandContext#currentSentenceReader}.
     *
     * @param currentSentenceReader The new current {@link PSentenceReader}
     * @return The context for chaining
     */
    public PCommandContext setCurrentSentenceReader(PSentenceReader<?> currentSentenceReader) {
        this.currentSentenceReader = currentSentenceReader;
        return this;
    }

    /**
     * Getter of {@link PCommandContext#currentError}.
     *
     * @return The current error
     */
    public String getCurrentError() {
        return currentError;
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

    public enum Action {

        NONE,
        READING_ARGUMENT,
        READING_SENTENCE,
        REQUIRING_PLAYER,
        CHECKING_PERMISSION
    }
}
