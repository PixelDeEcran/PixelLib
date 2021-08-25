package fr.pixeldeecran.pipilib.command;

import fr.pixeldeecran.pipilib.command.arg.PArgReader;
import fr.pixeldeecran.pipilib.command.sentence.PSentenceReader;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class PCommand {

    private final PCommandInfo commandInfo;
    private final Map<String, PSubCommand<?>> subCommands;

    private PCommandContext context;
    private PCommandErrorHandler errorHandler;
    private CommandRegistry commandRegistry;

    private String[] currentArgs;

    public PCommand() {
        this.subCommands = new LinkedHashMap<>();
        this.errorHandler = new PCommandErrorHandler();
        this.errorHandler.registerDefaults();

        if (this.getClass().isAnnotationPresent(PCommandInfo.class)) {
            this.commandInfo = this.getClass().getAnnotation(PCommandInfo.class);

            for (Class<? extends PSubCommand<?>> clazz : this.commandInfo.subCommands()) {
                try {
                    PSubCommand<?> subCommand = clazz.getDeclaredConstructor().newInstance();
                    subCommand.setParent(this);

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

        this.setContext(new PCommandContext());
    }

    @SuppressWarnings("unchecked")
    public <T> T readOptionalArg(int index, T defaultValue) {
        return (T) this.readArg(index, defaultValue.getClass(), defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T readRequiredArg(int index, Class<T> typeClass) {
        return (T) this.readArg(index, typeClass, null);
    }

    public Object readArg(int index, Class<?> typeClass, Object defaultValue) {
        this.context.setCurrentIndex(index);
        this.context.setCurrentArgs(this.currentArgs);
        this.context.setReadingSentence(false);

        if (index >= this.currentArgs.length) {
            if (defaultValue != null) {
                return defaultValue;
            } else {
                throw new PCommandException("WRONG_USAGE");
            }
        }

        String arg = this.currentArgs[index];
        PArgReader<?> argReader = this.commandRegistry.getArgReader(typeClass);
        Object value = argReader.read(arg);

        if (value != null) {
            return value;
        } else if (defaultValue != null) {
            return defaultValue;
        } else {
            throw new PCommandException(argReader.errorCause(arg));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T readOptionalSentence(int index, T defaultValue) {
        return (T) this.readSentence(index, defaultValue.getClass(), defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T readRequiredSentence(int index, Class<?> typeClass) {
        return (T) this.readSentence(index, typeClass, null);
    }

    public Object readSentence(int index, Class<?> typeClass, Object defaultValue) {
        this.context.setCurrentIndex(index);
        this.context.setCurrentArgs(this.currentArgs);
        this.context.setReadingSentence(true);

        if (index >= this.currentArgs.length) {
            if (defaultValue != null) {
                return defaultValue;
            } else {
                throw new PCommandException("WRONG_USAGE");
            }
        }

        String[] sentence = Arrays.copyOfRange(this.currentArgs, index, this.currentArgs.length);
        PSentenceReader<?> sentenceReader = this.commandRegistry.getSentenceReader(typeClass);
        Object value = sentenceReader.read(sentence);

        if (value != null) {
            return value;
        } else if (defaultValue != null) {
            return defaultValue;
        } else {
            throw new PCommandException(sentenceReader.errorCause(sentence));
        }
    }

    public void internallyExecute(CommandSender sender, String[] args) {
        this.currentArgs = args;

        if (this.commandInfo.subCommandIndex() == 0) {
            this.context.setCurrentCommand(this);
            this.context.setCurrentSender(sender);
            this.context.setCurrentArgs(this.currentArgs);
            this.context.setCurrentIndex(0);

            boolean result = this.executeSubCommands(sender);

            if (result) {
                return;
            }
        }

        this.context.setCurrentCommand(this);
        this.context.setCurrentSender(sender);
        this.context.setCurrentArgs(this.currentArgs);
        this.context.setCurrentIndex(0);
        this.execute(sender);
    }

    public boolean executeSubCommands(CommandSender sender) {
        int index = this.commandInfo.subCommandIndex();

        if (this.currentArgs.length > index && index >= 0) {
            this.context.setCurrentIndex(index);
            String arg = this.currentArgs[index];

            if (this.subCommands.containsKey(arg)) {
                this.subCommands.get(arg).internallyExecute(
                        sender,
                        this.currentArgs.length > index + 1 ? Arrays.copyOfRange(this.currentArgs, index + 1,
                            this.currentArgs.length) : new String[0]
                );
                return true;
            }
        }

        return false;
    }

    public void execute(CommandSender sender) {}

    public List<String> internallyTabCompleteFor(CommandSender sender, String[] args) {
        this.currentArgs = args;

        this.context.setCurrentCommand(this);
        this.context.setCurrentSender(sender);
        this.context.setCurrentArgs(this.currentArgs);
        this.context.setCurrentIndex(0);

        List<String> completions = this.tabCompleteSubCommands(sender);
        if (completions == null) {
            this.context.setCurrentCommand(this);
            this.context.setCurrentSender(sender);
            this.context.setCurrentArgs(this.currentArgs);
            this.context.setCurrentIndex(0);

            this.tabCompleteFor(sender, args.length - 1, completions = new ArrayList<>());

            List<String> validateCompletions = new ArrayList<>();

            for (String completion : completions) {
                if (StringUtils.startsWith(completion, args[args.length - 1])) {
                    validateCompletions.addAll(completions);
                }
            }

            return validateCompletions;
        } else {
            return completions;
        }
    }

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

    public void tabCompleteFor(CommandSender sender, int index, List<String> container) {}

    public void setCommandRegistry(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;

        this.subCommands.values().forEach(subCommand -> subCommand.setCommandRegistry(commandRegistry));
    }

    public void setContext(PCommandContext context) {
        this.context = context;

        this.subCommands.values().forEach(subCommand -> subCommand.setContext(context));
    }

    public void setErrorHandler(PCommandErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public Set<PSubCommand<?>> getAllSubCommands() {
        Set<PSubCommand<?>> subCommandsSet = new HashSet<>();

        this.subCommands.values().forEach(subCommand -> {
            subCommandsSet.add(subCommand);
            subCommandsSet.addAll(subCommand.getAllSubCommands());
        });

        return subCommandsSet;
    }

    public String getMainCommandName() {
        return this.getFullName();
    }

    public String getFullName() {
        return "/" + this.getName();
    }

    public String getFullUsage() {
        return this.getFullName() + (!this.getUsage().equals("") ? " " + this.getUsage() : "");
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public int getCurrentArgsLength() {
        return this.getCurrentArgs().length;
    }

    public String[] getCurrentArgs() {
        return currentArgs;
    }

    public String getName() {
        return this.getCommandInfo().name();
    }

    public String getUsage() {
        return this.getCommandInfo().usage();
    }

    public String[] getAliases() {
        return this.getCommandInfo().aliases();
    }

    public String getDescription() {
        return this.getCommandInfo().description();
    }

    public String getPermission() {
        return this.getCommandInfo().permission();
    }

    public Map<String, PSubCommand<?>> getSubCommands() {
        return subCommands;
    }

    public PCommandInfo getCommandInfo() {
        return commandInfo;
    }

    public PCommandContext getContext() {
        return context;
    }

    public PCommandErrorHandler getErrorHandler() {
        return errorHandler;
    }
}
