package fr.pixeldeecran.pixellib.command;

import fr.pixeldeecran.pixellib.command.PCommandContext.Action;
import fr.pixeldeecran.pixellib.command.arg.PArgReader;
import fr.pixeldeecran.pixellib.command.sentence.PSentenceReader;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Represents a simple PCommand.
 * <p>
 * In order to register a command, you need to add the annotation {@link PCommandExist} to the class of the command,
 * in addition to the annotation {@link PCommandInfo} describing your command.
 */
public abstract class PCommand {

    /**
     * The information about the command. The instance is actually acquired when the PCommand is instantiated.
     *
     * @see PCommand#PCommand()
     */
    private final PCommandInfo commandInfo;

    /**
     * The sub-commands, associated with their key and their aliases. These are registered when the PCommand is
     * instantiated thanks to the information given by the PCommandInfo.
     *
     * @see PCommand#PCommand()
     */
    private final Map<String, PSubCommand<?>> subCommands;

    /**
     * To keep track of the context of the command. It keeps track of which command sender used this command,
     * with what arguments, etc... By default, the sub-commands have the same instance of PCommandContext as their
     * parent's and this recursively. The instance is set in the constructor after the sub-commands have been
     * registered. It is strongly recommended not to modify the context of a sub-command. You can still modify the
     * context by calling {@link PCommand#setContext(PCommandContext)} with the root command. Keep in mind that replaces
     * the context at the execution time in a lazy way can cause a loss of context, so try to copy the data of the
     * context before replacing the context.
     *
     * @see PCommandContext
     * @see PCommand#getContext()
     * @see PCommand#setContext(PCommandContext)
     * @see PCommand#PCommand()
     */
    private PCommandContext context;

    /**
     * The error handler used when an error happened. You can edit this error handler to configure the behaviours of
     * the errors. The default instance of the PCommandErrorHandler is instantiated in the constructor. Sub-commands
     * can have a different PCommandErrorHandler (and have a different one by default) but the PCommandContainer will
     * only use the instance of the root command when an error is catche.
     *
     * @see PCommandErrorHandler
     * @see PCommand#getErrorHandler()
     * @see PCommand#setErrorHandler(PCommandErrorHandler)
     * @see PCommandContainer#execute(CommandSender, String, String[])
     * @see PCommandContainer#tabComplete(CommandSender, String, String[])
     * @see PCommand#PCommand()
     */
    private PCommandErrorHandler errorHandler;

    /**
     * The command registry which registers this command. The instance is actually sets after the PCommand was
     * instantiated. By default, the command registry is the same for its sub-commands.
     *
     * @see PCommandContainer
     * @see PCommand#getCommandRegistry()
     * @see PCommand#setCommandRegistry(PCommandRegistry)
     */
    private PCommandRegistry commandRegistry;

    /**
     * Keep track of the current args, updated when the command is executed or ask for tab completion.
     *
     * @see PCommand#internallyExecute(CommandSender, String[])
     * @see PCommand#internallyTabCompleteFor(CommandSender, String[])
     * @see PCommand#getCurrentArgs()
     * @see PCommand#getCurrentArgsLength()
     */
    private String[] currentArgs;

    /**
     * Main constructor of PCommand. This is where sub-commands are instantiated and registered, but also where
     * the PCommandErrorHandler is instantiated and the PCommandContext is created. But before registering
     * sub-commands, the PCommandInfo's instance is gathered.
     *
     * @see PCommand#commandInfo
     * @see PCommand#subCommands
     * @see PCommand#errorHandler
     * @see PCommand#setContext(PCommandContext)
     */
    public PCommand() {
        this.subCommands = new LinkedHashMap<>();
        this.setErrorHandler(new PCommandErrorHandler());

        if (this.getClass().isAnnotationPresent(PCommandInfo.class)) {
            this.commandInfo = this.getClass().getAnnotation(PCommandInfo.class);

            // Register all sub-commands...
            for (Class<? extends PSubCommand<?>> clazz : this.commandInfo.subCommands()) {
                try {
                    PSubCommand<?> subCommand = clazz.getDeclaredConstructor().newInstance();
                    subCommand.setParent(this); //

                    // We also register aliases, so that it's easier to get the sub-command by its alias
                    this.subCommands.put(subCommand.getName().toLowerCase(), subCommand);
                    for (String alias : subCommand.getAliases()) {
                        this.subCommands.put(alias.toLowerCase(), subCommand);
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new IllegalStateException("Couldn't instantiate SubCommand class " + clazz.getName(), e);
                }
            }

            this.errorHandler.setDoesPrintException(this.commandInfo.doesPrintException());
        } else {
            throw new IllegalStateException("The class " + this.getClass().getName() + " needs to have a PCommandInfo" +
                " annotation in order to get infos");
        }

        // Set the context after the sub-commands have been registered, so that this also set the same context for
        // the sub-commands
        this.setContext(new PCommandContext());
    }

    /**
     * Parse an optional argument in the current args as a usable value. This is an overloading of
     * {@link PCommand#readArg(int, Class, Object)}.
     *
     * @param index        The index of the argument
     * @param defaultValue The value returned if the argument couldn't be parsed
     * @param <T>          The type of the value wanted after passing the argument
     * @return The value of the parsed argument
     */
    @SuppressWarnings("unchecked")
    public <T> T readOptionalArg(int index, T defaultValue) {
        return (T) this.readArg(index, defaultValue.getClass(), defaultValue);
    }

    /**
     * Parse a required argument in the current args as a usable value. This is an overloading of
     * {@link PCommand#readArg(int, Class, Object)}. If it couldn't read the value, an exception will be thrown
     * and be by default caught by {@link PCommandContainer}.
     *
     * @param index     The index of the argument
     * @param typeClass The type of the value
     * @param <T>       The type of the value wanted after passing the argument
     * @return The value of the parsed argument
     */
    @SuppressWarnings("unchecked")
    public <T> T readRequiredArg(int index, Class<T> typeClass) {
        return (T) this.readArg(index, typeClass, null);
    }

    /**
     * Parse an argument in the current args as a usable value. If the default value passed is null, this is considered
     * as a required argument, and will throw an exception if it can't read the argument. <br>
     * <br>
     * If the default value is null and the user didn't specify enough arguments, a {@link PCommandException} with error
     * "WRONG_USAGE" will be thrown. Otherwise, if the {@link PArgReader} couldn't parse the argument and so return null,
     * a {@link PCommandException} will be thrown with an exception computed by the {@link PArgReader} according to the
     * argument passed. The {@link PCommandException} will then be caught by the {@link PCommandContainer}<br>
     * <br>
     * This is also where the context is updated.
     *
     * @param index        The index of the argument
     * @param typeClass    The type of the wanted value
     * @param defaultValue The default value if the argument couldn't be parsed
     * @return The value that could be read
     */
    public Object readArg(int index, Class<?> typeClass, Object defaultValue) {
        // Update context
        this.resetPartiallyContext()
            .setCurrentAction(Action.READING_ARGUMENT)
            .setCurrentIndex(index);

        // Check if the user has specified enough arguments
        if (index >= this.currentArgs.length) {
            if (defaultValue != null) { // Is it required?
                return defaultValue; // Optional
            } else {
                throw new PCommandException("WRONG_USAGE"); // Required
            }
        }

        // Parse the argument
        String arg = this.currentArgs[index];
        PArgReader<?> argReader = this.commandRegistry.getArgReader(typeClass);
        this.context.setCurrentArgReader(argReader);
        Object value = argReader.read(arg);

        if (value != null) { // Was the parsing successful?
            return value;
        } else if (defaultValue != null) { // Is it required?
            return defaultValue; // Optional
        } else {
            throw new PCommandException(argReader.errorCause(arg)); // Required
        }
    }

    /**
     * Read an optional sentence starting at the passed index. If it can't read the sentence, the
     * default value will be returned. The length of the sentence is determined by the {@link PSentenceReader}.
     *
     * @param index        The starting index of the argument
     * @param defaultValue The default value if the sentence couldn't be parsed
     * @param <T>          The type of the wanted sentence
     * @return The sentence that could be read
     */
    @SuppressWarnings("unchecked")
    public <T> T readOptionalSentence(int index, T defaultValue) {
        return (T) this.readSentence(index, defaultValue.getClass(), defaultValue);
    }

    /**
     * Read a required sentence starting at the passed index. If it can't read the sentence, an exception will be thrown
     * and be by default caught by {@link PCommandContainer}. The length of the sentence is determined by the
     * {@link PSentenceReader}.
     *
     * @param index     The starting index of the argument
     * @param typeClass The class of the type of the wanted sentence
     * @param <T>       The type of the wanted sentence
     * @return The sentence that could be read
     */
    @SuppressWarnings("unchecked")
    public <T> T readRequiredSentence(int index, Class<T> typeClass) {
        return (T) this.readSentence(index, typeClass, null);
    }

    /**
     * Parse a group of arguments, which starts at a specific index, as a sentence. If the default value passed is null,
     * this is considered as a required argument, and will throw an exception if it can't read the argument. The length
     * of a sentence depends on the {@link PSentenceReader} associated with the value type.<br>
     * <br>
     * If the default value is null and the user didn't specify enough arguments, a {@link PCommandException} with error
     * "WRONG_USAGE" will be thrown. Otherwise, if the {@link PSentenceReader} couldn't parse the group of arguments and
     * so return null, a {@link PCommandException} will be thrown with an exception computed by the
     * {@link PSentenceReader} according to the argument passed. The {@link PCommandException} will then be caught by
     * the {@link PCommandContainer}.<br>
     * <br>
     * This is also where the context is updated.
     *
     * @param index        The starting index of the argument
     * @param typeClass    The type of the wanted sentence
     * @param defaultValue The default value if the argument couldn't be parsed
     * @return The sentence that could be read
     */
    public Object readSentence(int index, Class<?> typeClass, Object defaultValue) {
        // Update context
        this.resetPartiallyContext()
            .setCurrentAction(Action.READING_SENTENCE)
            .setCurrentIndex(index);

        // Check if the user has specified enough arguments
        if (index >= this.currentArgs.length) {
            if (defaultValue != null) { // Is it required?
                return defaultValue; // Optional
            } else {
                throw new PCommandException("WRONG_USAGE"); // Required
            }
        }

        // Parse the arguments
        String[] sentence = Arrays.copyOfRange(this.currentArgs, index, this.currentArgs.length);
        PSentenceReader<?> sentenceReader = this.commandRegistry.getSentenceReader(typeClass);
        this.context.setCurrentSentenceReader(sentenceReader);
        Object value = sentenceReader.read(sentence);

        if (value != null) { // Was the parsing successful?
            return value;
        } else if (defaultValue != null) { // Is it required?
            return defaultValue; // Optional
        } else {
            throw new PCommandException(sentenceReader.errorCause(sentence)); // Required
        }
    }

    /**
     * Cast the command sender as a player in a safe way. If the command sender is not a player,
     * a {@link PCommandException} will be thrown with the error "MUST_BE_PLAYER". Keep in mind that the current sender
     * of the context is also updated with the passed command sender
     *
     * @param sender The command sender
     * @return The player instance if it could be cast
     */
    public Player requirePlayer(CommandSender sender) {
        // Update the context
        this.resetPartiallyContext()
            .setCurrentAction(Action.REQUIRING_PLAYER);

        if (sender instanceof Player) {
            return (Player) sender;
        } else {
            throw new PCommandException("MUST_BE_PLAYER");
        }
    }

    /**
     * Check if the command sender has the specified permission. If he hasn't or the permission is an empty String,
     * a {@link PCommandException} will be thrown with the error "NOT_ENOUGH_PERMISSION".
     *
     * @param sender     The command sender
     * @param permission The permission to check
     */
    public void checkPermission(CommandSender sender, String permission) {
        // Update context
        this.resetPartiallyContext()
            .setCurrentAction(Action.CHECKING_PERMISSION)
            .setCurrentPermission(permission);

        // Check permission
        if (!sender.hasPermission(permission) && !permission.equals("")) {
            throw new PCommandException("NOT_ENOUGH_PERMISSION");
        }
    }

    /**
     * This function after the PCommandRegistry was set. This is where we register the default messages.
     * <p>
     * You can listen to this function in order to replace the error handler
     */
    public void init() {
        this.errorHandler.registerDefaults(this.commandRegistry);
    }

    /**
     * This function is internally used in order to manage sub-command execution if
     * {@link PCommandInfo#autoManagingSubCommands()} was enabled. It also updates the context, and check permission.
     *
     * @param sender The command sender
     * @param args   The arguments specified by the command sender
     */
    public void internallyExecute(CommandSender sender, String[] args) {
        // Set current args
        this.currentArgs = args;

        // Check permission
        if (this.commandInfo.autoCheckPermission()) {
            this.checkPermission(sender, this.getPermission());
        }

        // Auto-manage sub-commands if it is enabled
        if (this.commandInfo.autoManagingSubCommands() && this.commandInfo.subCommandIndex() == 0) {

            // Update the context
            this.updateContext(sender, args);

            boolean result = this.executeSubCommands(sender);

            if (result) { // Has a sub-command been executed?
                return; // Yes!
            }
        }

        // Update the context
        this.updateContext(sender, args);

        // Called the main execute function
        this.execute(sender);
    }

    /**
     * Try to execute a sub-command. This function can be used in order to handle the case where none sub-commands could
     * be executed. This is also required when the sub-command index is not equals to 0, and so that you need to gather
     * some arguments before.
     *
     * @param sender The command sender
     * @return Has a sub-command been executed or not?
     */
    public boolean executeSubCommands(CommandSender sender) {
        int index = this.commandInfo.subCommandIndex();

        if (this.currentArgs.length > index && index >= 0) { // Check if the command sender specifies enough arguments

            // Update the context
            this.context.setCurrentIndex(index);
            String arg = this.currentArgs[index];

            if (this.subCommands.containsKey(arg)) { // Tries to find a sub-command

                // Execute the sub-command
                this.subCommands.get(arg).internallyExecute(
                    sender,
                    this.currentArgs.length > index + 1 ? Arrays.copyOfRange(this.currentArgs, index + 1,
                        this.currentArgs.length) : new String[0]
                );

                return true; // A sub-command have been executed
            }
        }

        return false; // No sub-command have been executed
    }

    /**
     * Called when the command is executed.
     *
     * @param sender The command sender
     */
    public void execute(CommandSender sender) {
    }

    /**
     * This function is internally used in order to manage sub-command tab completion
     * if {@link PCommandInfo#autoManagingSubCommands()} was enabled. It also updates the context.
     *
     * @param sender The command sender
     * @param args   The arguments specified by the command sender
     * @return The tab completion list which will be sent to the player
     */
    public List<String> internallyTabCompleteFor(CommandSender sender, String[] args) {
        this.currentArgs = args;

        // Update context
        this.updateContext(sender, args);

        // TODO : Auto-completion the sub-command's name and aliases
        List<String> completions = this.tabCompleteSubCommands(sender); // TODO : handle PCommandInfo#autoManagingSubCommands()
        if (completions == null) { // Has a tab completion been found in the sub-commands?

            // Re-update context
            this.updateContext(sender, args);

            // Called the main tab completion function
            this.tabCompleteFor(sender, args.length - 1, completions = new ArrayList<>());

            List<String> validateCompletions = new ArrayList<>();

            // Filter the completions with the one who starts with the last argument specified by the command sender.
            for (String completion : completions) {
                if (StringUtils.startsWith(completion, args[args.length - 1])) {
                    validateCompletions.addAll(completions);
                }
            }

            return validateCompletions; // Tab completion of this command
        } else {
            return completions; // Tab completion of sub-commands
        }
    }

    /**
     * Try to ask sub-commands for tab completion. This function can be used in order to handle the case where non
     * sub-commands could respond with a tab completion. This is also required when the sub-command index is not equal
     * to 0 if there is a need to gather information before. <br>
     * <br>
     * If the returned tab completion list is null, no tab completion have been found.
     *
     * @param sender The command sender
     * @return The tab completion (it may be null)
     */
    public List<String> tabCompleteSubCommands(CommandSender sender) {
        int index = this.commandInfo.subCommandIndex();

        if (this.currentArgs.length > index && index >= 0) {
            this.context.setCurrentIndex(index);
            String firstArg = this.currentArgs[index];

            if (this.subCommands.containsKey(firstArg)) {
                return this.subCommands.get(firstArg).internallyTabCompleteFor(
                    sender,
                    this.currentArgs.length > index + 1 ? Arrays.copyOfRange(this.currentArgs, index + 1, this.currentArgs.length) : new String[0]
                );
            }
        }

        return null;
    }

    /**
     * Called when the command is ask for tab completion. In order to add tab completion, you just need to add it
     * to the container, and the library will filter it, so that only string which starts with what enters the sender
     * will be sent.
     *
     * @param sender    The command sender
     * @param index     The index of the argument ask for tab completion
     * @param container The container of the tab completion
     */
    public void tabCompleteFor(CommandSender sender, int index, List<String> container) {
    }

    /**
     * Used to update the context, especially reset the context and set the current command sender, the current args, and
     * the current command.
     *
     * @param sender The command sender
     * @param args   The arguments
     */
    public void updateContext(CommandSender sender, String[] args) {
        this.resetPartiallyContext()
            .setCurrentCommand(this)
            .setCurrentSender(sender)
            .setCurrentArgs(args);
    }

    /**
     * Reset partially the context.
     *
     * @return The context
     */
    public PCommandContext resetPartiallyContext() {
        return this.context
            .setCurrentArgs(this.currentArgs)
            .setCurrentIndex(0)
            .setCurrentAction(Action.NONE)
            .setCurrentPermission("")
            .setCurrentArgReader(null)
            .setCurrentSentenceReader(null);
    }

    /**
     * Getter of all sub-commands recursively. So that, it also calls this function with the sub-commands. Be aware that
     * the returned {@link Set} will not contain the command itself.
     *
     * @return All the sub-commands
     */
    public Set<PSubCommand<?>> getAllSubCommands() {
        Set<PSubCommand<?>> subCommandsSet = new HashSet<>();

        this.subCommands.values().forEach(subCommand -> {
            subCommandsSet.add(subCommand);
            subCommandsSet.addAll(subCommand.getAllSubCommands());
        });

        return subCommandsSet;
    }

    /**
     * Get the main command name. This is mainly used by {@link PSubCommand#getFullUsage()} in order to just have the
     * root name. If this is sub-command, this will call this method on his parent.
     *
     * @return The root command name
     * @see PSubCommand#getRootCommandName()
     */
    public String getRootCommandName() {
        return this.getFullName();
    }

    /**
     * Get he full name of the command. If this is called on a sub-command, the sub-command name will be added with
     * his parent name. Be aware that this is not made to be displayed with the usage, this will not take into account
     * {@link PCommandInfo#subCommandIndex()} of the command. See {@link PCommand#getFullUsage()}, which is more
     * suitable for this usage.
     *
     * @return The full name of the command
     * @see PSubCommand#getFullName()
     */
    public String getFullName() {
        return "/" + this.getName();
    }

    /**
     * Get the full usage, with the command name included. If this is called on a sub-command, the sub-command usage
     * will be inserted in the usage at the index {@link PCommandInfo#subCommandIndex()}.
     *
     * @return The full usage of the command
     */
    public String getFullUsage() {
        return this.getFullName() + (!this.getUsage().equals("") ? " " + this.getUsage() : "");
    }

    /**
     * Getter of {@link PCommand#commandRegistry}.
     *
     * @return The command registry of the command
     * @see PCommand#setCommandRegistry(PCommandRegistry)
     */
    public PCommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    /**
     * Setter of the {@link PCommand#commandRegistry}. This will also set the command registry for the sub-commands.
     * Use only this method if you know what you are doing. By default, the context is set in
     * {@link PCommandContainer#PCommandContainer(PCommand, PCommandRegistry, JavaPlugin)}.
     *
     * @param commandRegistry The new command registry
     * @see PCommand#getCommandRegistry()
     */
    public void setCommandRegistry(PCommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;

        this.subCommands.values().forEach(subCommand -> subCommand.setCommandRegistry(commandRegistry));
    }

    /**
     * @return The length of the current args
     * @see PCommand#getCurrentArgs()
     */
    public int getCurrentArgsLength() {
        return this.getCurrentArgs().length;
    }

    /**
     * Getter of {@link PCommand#currentArgs}.
     *
     * @return The current args of the command
     */
    public String[] getCurrentArgs() {
        return currentArgs;
    }

    /**
     * Overloading getter of {@link PCommandInfo#name()}.
     *
     * @return The name of the command
     */
    public String getName() {
        return this.getCommandInfo().name();
    }

    /**
     * Overloading getter of {@link PCommandInfo#usage()}.
     *
     * @return The usage of the command
     */
    public String getUsage() {
        return this.getCommandInfo().usage();
    }

    /**
     * Overloading getter of {@link PCommandInfo#aliases()}.
     *
     * @return The aliases of the command
     */
    public String[] getAliases() {
        return this.getCommandInfo().aliases();
    }

    /**
     * Overloading getter of {@link PCommandInfo#description()}.
     *
     * @return The description of the command
     */
    public String getDescription() {
        return this.getCommandInfo().description();
    }

    /**
     * Overloading getter of {@link PCommandInfo#permission()}.
     *
     * @return The permission of the command
     */
    public String getPermission() {
        return this.getCommandInfo().permission();
    }

    /**
     * Getter of {@link PCommand#subCommands}.
     *
     * @return The sub-commands of the command
     */
    public Map<String, PSubCommand<?>> getSubCommands() {
        return subCommands;
    }

    /**
     * Getter of {@link PCommand#commandInfo}.
     *
     * @return The command information
     */
    public PCommandInfo getCommandInfo() {
        return commandInfo;
    }

    /**
     * Getter of {@link PCommand#context}.
     *
     * @return The context of the command
     * @see PCommand#setContext(PCommandContext)
     */
    public PCommandContext getContext() {
        return context;
    }

    /**
     * Setter of the {@link PCommand#context}. This will also set the command context for the sub-commands.
     * Use only this method if you know what you are doing. By default, the context is set in {@link PCommand#PCommand()}.
     *
     * @param context The new command context
     * @see PCommand#getContext()
     */
    public void setContext(PCommandContext context) {
        this.context = context;

        this.subCommands.values().forEach(subCommand -> subCommand.setContext(context));
    }

    /**
     * Getter of {@link PCommand#errorHandler}.
     *
     * @return The error handler of the command
     * @see PCommand#setErrorHandler(PCommandErrorHandler)
     */
    public PCommandErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Setter of the {@link PCommand#errorHandler}. If you want to use your own {@link PCommandErrorHandler},
     * we recommend you to call this method in {@link PCommand#init()}.
     *
     * @param errorHandler The new error handler
     * @see PCommand#getErrorHandler()
     */
    public void setErrorHandler(PCommandErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
}
