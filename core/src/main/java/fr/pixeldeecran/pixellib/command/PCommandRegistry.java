package fr.pixeldeecran.pixellib.command;

import fr.pixeldeecran.pixellib.command.arg.BooleanPAR;
import fr.pixeldeecran.pixellib.command.arg.CharPAR;
import fr.pixeldeecran.pixellib.command.arg.PArgReader;
import fr.pixeldeecran.pixellib.command.arg.StringPAR;
import fr.pixeldeecran.pixellib.command.arg.mc.OfflinePlayerPAR;
import fr.pixeldeecran.pixellib.command.arg.mc.PlayerPAR;
import fr.pixeldeecran.pixellib.command.arg.numbers.*;
import fr.pixeldeecran.pixellib.command.sentence.PSentenceReader;
import fr.pixeldeecran.pixellib.command.sentence.StringPSR;
import fr.pixeldeecran.pixellib.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles {@link PCommand}, {@link PArgReader} and {@link PSentenceReader} registration.
 */
public class PCommandRegistry {

    /**
     * The plugin's instance of this command registry.
     *
     * @see PCommandRegistry#PCommandRegistry(JavaPlugin)
     * @see PCommandRegistry#getPlugin()
     */
    private final JavaPlugin plugin;

    /**
     * Registry of {@link PArgReader}.
     *
     * @see PCommandRegistry#registerArgReader(Class, PArgReader)
     * @see PCommandRegistry#registerDefaults()
     * @see PCommandRegistry#getArgReader(Object)
     * @see PCommandRegistry#getArgReaderRegistry()
     */
    private final Map<Class<?>, PArgReader<?>> argReaderRegistry;

    /**
     * Registry of {@link PSentenceReader}.
     *
     * @see PCommandRegistry#registerSentenceReader(Class, PSentenceReader)
     * @see PCommandRegistry#registerDefaults()
     * @see PCommandRegistry#getSentenceReader(Object)
     * @see PCommandRegistry#getSentenceReaderRegistry()
     */
    private final Map<Class<?>, PSentenceReader<?>> sentenceReaderRegistry;

    /**
     * Template of the default {@link PCommandErrorHandler}.
     *
     * @see PCommandRegistry#getErrorHandlerTemplate()
     * @see PCommandRegistry#registerDefaults()
     */
    private final PCommandErrorHandler errorHandlerTemplate;

    /**
     * The server {@link CommandMap} instance.
     *
     * @see PCommandRegistry#getCommandMap()
     */
    private CommandMap commandMap;

    /**
     * Main constructor of {@link PCommandRegistry}. This is where we also call
     * {@link PCommandRegistry#lookForCommandMap()} to get the {@link CommandMap} instance.
     *
     * @param plugin The plugin's instance
     */
    public PCommandRegistry(JavaPlugin plugin) {
        this.plugin = plugin;

        this.argReaderRegistry = new HashMap<>();
        this.sentenceReaderRegistry = new HashMap<>();
        this.errorHandlerTemplate = new PCommandErrorHandler();

        this.lookForCommandMap();
    }

    /**
     * Register the defaults {@link PArgReader}, {@link PSentenceReader} and default error actions in the
     * {@link PCommandRegistry#errorHandlerTemplate}.
     */
    public void registerDefaults() {

        // Register default argument readers
        this.registerArgReader(byte.class, new BytePAR());
        this.registerArgReader(Byte.class, new BytePAR());
        this.registerArgReader(short.class, new ShortPAR());
        this.registerArgReader(Short.class, new ShortPAR());
        this.registerArgReader(int.class, new IntegerPAR());
        this.registerArgReader(Integer.class, new IntegerPAR());
        this.registerArgReader(long.class, new LongPAR());
        this.registerArgReader(Long.class, new LongPAR());
        this.registerArgReader(float.class, new FloatPAR());
        this.registerArgReader(Float.class, new FloatPAR());
        this.registerArgReader(double.class, new DoublePAR());
        this.registerArgReader(Double.class, new DoublePAR());

        this.registerArgReader(boolean.class, new BooleanPAR());
        this.registerArgReader(Boolean.class, new BooleanPAR());
        this.registerArgReader(char.class, new CharPAR());
        this.registerArgReader(Character.class, new CharPAR());
        this.registerArgReader(String.class, new StringPAR());

        this.registerArgReader(Player.class, new PlayerPAR());
        this.registerArgReader(OfflinePlayer.class, new OfflinePlayerPAR());


        // Register default sentence readers
        this.registerSentenceReader(String.class, new StringPSR());


        // Register default error actions
        this.errorHandlerTemplate.registerErrorMessage("EXCEPTION", "§cException happened while reading the arg \"%2$s\" at index %1$d of the command \"%3$s\".");
        this.errorHandlerTemplate.registerErrorMessage("CRITICAL", "§cCritical error happened at arg \"%2$s\" of index %1$d with the command \"%3$s\".");

        this.errorHandlerTemplate.registerErrorMessage("ARG_NULL", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is null!");
        this.errorHandlerTemplate.registerErrorMessage("EMPTY_STRING", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is an empty String!");

        this.errorHandlerTemplate.registerErrorMessage("BOOLEAN_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Boolean!");
        this.errorHandlerTemplate.registerErrorMessage("BYTE_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Byte!");
        this.errorHandlerTemplate.registerErrorMessage("SHORT_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Short!");
        this.errorHandlerTemplate.registerErrorMessage("INTEGER_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Integer!");
        this.errorHandlerTemplate.registerErrorMessage("LONG_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Long!");
        this.errorHandlerTemplate.registerErrorMessage("FLOAT_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Float!");
        this.errorHandlerTemplate.registerErrorMessage("DOUBLE_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Double!");

        this.errorHandlerTemplate.registerErrorMessage("NOT_ONLINE_PLAYER", "§cThe player \"%2$s\" is not connected!");

        this.errorHandlerTemplate.registerErrorMessage("MUST_BE_PLAYER", "§cYou need to be a player to be able to do that!");
        this.errorHandlerTemplate.registerErrorMessage("WRONG_USAGE", "§cWrong usage : \"%4$s\"");
        this.errorHandlerTemplate.registerErrorMessage("NOT_ENOUGH_PERMISSION", "§cYou don't have enough permissions to do that!");
    }

    /**
     * Register a {@link PArgReader}.
     *
     * @param typeClass      The type class
     * @param argReaderClass The argument reader instance
     * @param <T>            The type parameter
     * @see PCommandRegistry#getArgReader(Object)
     */
    public <T> void registerArgReader(Class<T> typeClass, PArgReader<T> argReaderClass) {
        this.argReaderRegistry.put(typeClass, argReaderClass);
    }

    /**
     * Register a {@link PSentenceReader}
     *
     * @param typeClass           The type class
     * @param sentenceReaderClass The sentence reader instance
     * @param <T>                 The type parameter
     * @see PCommandRegistry#getSentenceReader(Object)
     */
    public <T> void registerSentenceReader(Class<T> typeClass, PSentenceReader<T> sentenceReaderClass) {
        this.sentenceReaderRegistry.put(typeClass, sentenceReaderClass);
    }

    /**
     * Get a {@link PArgReader} from a type.
     *
     * @param typeClass The type class
     * @param <T>       The type parameter
     * @return The argument reader instance for the specified type
     */
    @SuppressWarnings("all")
    public <T> PArgReader<T> getArgReader(T typeClass) {
        return (PArgReader<T>) this.argReaderRegistry.get(typeClass);
    }

    /**
     * Get a {@link PSentenceReader} from a type.
     *
     * @param typeClass The type class
     * @param <T>       The type paramter
     * @return The sentence reader instance for the specified type
     */
    @SuppressWarnings("all")
    public <T> PSentenceReader<T> getSentenceReader(T typeClass) {
        return (PSentenceReader<T>) this.sentenceReaderRegistry.get(typeClass);
    }

    /**
     * Register all commands of a specified package. It will search for all class in the package which are subtype of
     * {@link PCommand}. Before trying to register a command, it will check these conditions : <br>
     * - The class is not PSubCommand <br>
     * - The class is not abstract and is not an interface <br>
     * - The annotation {@link PCommandExist} is present <br>
     * - The class is not a sub-command (the class doesn't have {@link PSubCommand} in his superclasses) <br>
     * - The class has an empty constructor <br>
     * If these conditions are met, the command will be registered.
     *
     * @param packageName The package name
     */
    public void registerAllCommandsIn(String packageName) {
        Reflections reflections = new Reflections(packageName);

        reflections.getSubTypesOf(PCommand.class).forEach(commandClass -> {
            try {
                if (commandClass.getPackage().getName().startsWith(packageName) && commandClass != PSubCommand.class
                    && !Modifier.isAbstract(commandClass.getModifiers()) && !Modifier.isInterface(commandClass.getModifiers())
                    && commandClass.isAnnotationPresent(PCommandExist.class)) {
                    try {
                        commandClass.asSubclass(PSubCommand.class); // check if it's not a sub command
                        return;
                    } catch (ClassCastException ignored) {
                    }
                    commandClass.getDeclaredConstructor(); // check if it has an empty constructor

                    this.registerCommand(commandClass);
                }
            } catch (NoSuchMethodException ignored) {
            }
        });
    }

    /**
     * Register a command. This will try to instantiate the command, and will put the instance in a
     * {@link PCommandContainer}. Then, the command will be registered with {@link CommandMap#register(String, Command)}.
     *
     * @param clazz The command class
     */
    public void registerCommand(Class<? extends PCommand> clazz) {
        try {
            PCommand command = clazz.getDeclaredConstructor().newInstance();
            PCommandContainer container = new PCommandContainer(command, this, plugin);

            if (this.commandMap != null) {
                this.commandMap.register(this.plugin.getDescription().getName().toLowerCase(), container);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Couldn't instantiate Command class " + clazz.getName(), e);
        }
    }

    /**
     * Look for the server {@link CommandMap}'s instance.
     */
    public void lookForCommandMap() {
        this.commandMap = (CommandMap) ReflectionUtils.getFieldValue(this.plugin.getServer().getClass(), "commandMap",
            Bukkit.getServer());
    }

    /**
     * Getter of {@link PCommandRegistry#plugin}.
     *
     * @return The plugin's instance
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Getter of {@link PCommandRegistry#argReaderRegistry}.
     *
     * @return The {@link PArgReader} registry
     */
    public Map<Class<?>, PArgReader<?>> getArgReaderRegistry() {
        return argReaderRegistry;
    }

    /**
     * Getter of {@link PCommandRegistry#sentenceReaderRegistry}.
     *
     * @return The {@link PSentenceReader} registry
     */
    public Map<Class<?>, PSentenceReader<?>> getSentenceReaderRegistry() {
        return sentenceReaderRegistry;
    }

    /**
     * Getter of {@link PCommandRegistry#errorHandlerTemplate}.
     *
     * @return The error handler template
     */
    public PCommandErrorHandler getErrorHandlerTemplate() {
        return errorHandlerTemplate;
    }

    /**
     * Getter of {@link PCommandRegistry#commandMap}.
     *
     * @return The server {@link CommandMap}'s instance
     */
    public CommandMap getCommandMap() {
        return commandMap;
    }
}
