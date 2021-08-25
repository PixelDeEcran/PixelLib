package fr.pixeldeecran.pipilib.command;

import fr.pixeldeecran.pipilib.PPlugin;
import fr.pixeldeecran.pipilib.command.arg.*;
import fr.pixeldeecran.pipilib.command.arg.mc.OfflinePlayerPAR;
import fr.pixeldeecran.pipilib.command.arg.mc.PlayerPAR;
import fr.pixeldeecran.pipilib.command.arg.numbers.*;
import fr.pixeldeecran.pipilib.command.sentence.PSentenceReader;
import fr.pixeldeecran.pipilib.command.sentence.StringPSR;
import fr.pixeldeecran.pipilib.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class PCommandRegistry {

    private final PPlugin plugin;
    private final Map<Class<?>, PArgReader<?>> argReaderRegistry = new HashMap<>();
    private final Map<Class<?>, PSentenceReader<?>> sentenceReaderRegistry = new HashMap<>();

    private CommandMap commandMap;

    public PCommandRegistry(PPlugin plugin) {
        this.plugin = plugin;

        this.lookForCommandMap();
    }

    public void registerDefaults() {
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


        this.registerSentenceReader(String.class, new StringPSR());
    }

    public <T> void registerArgReader(Class<T> typeClass, PArgReader<T> argReaderClass) {
        this.argReaderRegistry.put(typeClass, argReaderClass);
    }

    public <T> void registerSentenceReader(Class<T> typeClass, PSentenceReader<T> sentenceReaderClass) {
        this.sentenceReaderRegistry.put(typeClass, sentenceReaderClass);
    }

    @SuppressWarnings("all")
    public <T> PArgReader<T> getArgReader(T typeClass) {
        return (PArgReader<T>) this.argReaderRegistry.get(typeClass);
    }

    @SuppressWarnings("all")
    public <T> PSentenceReader<T> getSentenceReader(T typeClass) {
        return (PSentenceReader<T>) this.sentenceReaderRegistry.get(typeClass);
    }

    public void registerAllCommandsIn(String packageName) {
        Reflections reflections = new Reflections(packageName);

        reflections.getSubTypesOf(PCommand.class).forEach(commandClass -> {
            try {
                if (commandClass.getPackage().getName().startsWith(packageName) && commandClass != PSubCommand.class
                        && !Modifier.isAbstract(commandClass.getModifiers()) && !Modifier.isInterface(commandClass.getModifiers())) {
                    try {
                        commandClass.asSubclass(PSubCommand.class); // check if it's not a sub command
                        return;
                    } catch (ClassCastException ignored) {}
                    commandClass.getDeclaredConstructor(); // check if it has an empty constructor

                    this.registerCommand(commandClass);
                }
            } catch (NoSuchMethodException ignored) {}
        });
    }

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

    public void lookForCommandMap() {
        this.commandMap = (CommandMap) ReflectionUtils.getFieldValue(this.plugin.getServer().getClass(), "commandMap",
                Bukkit.getServer());
    }

    public Map<Class<?>, PArgReader<?>> getArgReaderRegistry() {
        return argReaderRegistry;
    }

    public Map<Class<?>, PSentenceReader<?>> getSentenceReaderRegistry() {
        return sentenceReaderRegistry;
    }
}
