# Commands

In this topic, you will how learn to create commands, from simple one to complex one with sub commands.
You will also learn how it works internally in order to more understand what you can do, and how you can do it.

## Basic Example 

First, let's see a basic [example](../core/src/test/java/fr/pipilib/example/commands/ExampleCommand.java) :

```java
@PCommandInfo( // Describe the example command
    name = "example", // The name of the command
    aliases = "examples", // The aliases of the command
    description = "Simple Example Command", // The description of the command
    usage = "<number> [color]", // The usage of the command, <> for required args, and [] for optional args
    permission = "fr.pipilib.commands.example" // The permission of the command
)
public class ExampleCommand extends PCommand { // extends PCommand, this tells that this is a command and not a subcommand

    @Override
    public void execute(CommandSender sender) { // When the command is executed
        
        // We read the first arg, which is required and is an int arg if the arg couldn't be read, 
        // The code will stop here and will send a message to the player describing the error
        int number = this.readRequiredArg(0, int.class);
        
        // We also read the second arg, which is optional, if the command sender didn't specify the arg
        // Or there was an error while parsing the arg, the default value will be returned
        char color = this.readOptionalArg(1, 'e');
        
        // We can now send the message to the player and use the information provided by the command sender
        sender.sendMessage(String.format("§%1$sYou enter the number %2$d!", color, number));
    }
}
```

As you can see, in this command, we just return the number entered by the sender, and allows him to specify 
the color of the message.

## More Advanced Example

> What if I want to create a command with sub commands?

As you will see, it's very simple, and the library does most of the work while staying flexible.  
Let's see another [example](../core/src/test/java/fr/pipilib/example/commands/MessageCommand.java) :

```java
@PCommandInfo(
    name = "message",
    aliases = {"msg"},
    description = "A Message command",
    permission = "fr.pipilib.commands.message",
    subCommands = {
        HelpCommand.class,
        BroadcastCommand.class,
        SendCommand.class
    }
)
public class MessageCommand extends PCommand {
    
}
```

In this example, we want a command with 3 sub commands :  

 - **/message help :** Display the available commands  
 - **/message broadcast \<message> :** Broadcast a message   
 - **/message send \<player> \<message> :** Send a message to a specific player

So back to the above code, as you can see, the field `subCommands` let you specify the classes of the subcommands.  
Moreover, you can see that the execute method hasn't been override. This is because the library automatically
handle sub commands, but you will see that you can still modify the behaviours of the sub command handler.

In the code, the sub commands are organized as a tree :

 - MessageCommand
   - HelpCommand
   - BroadcastCommand
   - OtherCommand
       - OtherSubCommand
       - AnotherSubCommand
   - SendCommand
 
So that even sub commands can have their own sub commands!

Now let's see how we can go about a help command :

```java
@PCommandInfo(
    name = "help",
    description = "Display the available commands",
    permission = "fr.pipilib.commands.message.help"
    // We don't need to specify the usage because there are no arguments used
)
public class HelpCommand extends PSubCommand<MessageCommand> { // We specify the parent command

    @Override
    public void execute(CommandSender sender) {
        sender.sendMessage("§6<---------- /broadcast help ---------->");
        sender.sendMessage(String.format(
            "§6%1$s : §e%2$s", // String to format
            this.getParent().getFullUsage(), // The full usage of the command (name included), here, it will be /broadcast <message>
            this.getParent().getDescription() // The description of the command
        ));

        // PCommand#getAllSubCommands() returns a list with all sub commands, and even sub commands of sub commands, etc...
        // In fact, it's just a recursive function
        this.getParent().getAllSubCommands().forEach(subCommand -> {
            // We iterate through all sub commands and send a message with the full usage and the description of the sub command
            sender.sendMessage(String.format("§6%1$s : §e%2$s", subCommand.getFullUsage(), subCommand.getDescription()));
        });
    }
}
```

In game, the result looks like this : **TODO**

As you can see, the function `PCommand#getFullUsage()` returns you an already formatted usage String ready to be used in your command!  
You can also notice the fact that the arguments are indexed locally, so that in the `HelpCommand`,
the first argument is the second argument of `MessageCommand`.

Now, let's see how we go about reading a group of arguments :

```java
@PCommandInfo(
    name = "broadcast",
    aliases = "bc",
    description = "Broadcast a message",
    usage = "<message>", // Specify the usage
    permission = "fr.pipilib.commands.message.broadcast"
)
public class BroadcastCommand extends PSubCommand<MessageCommand> {

    @Override
    public void execute(CommandSender sender) {
        // Sentence are groups of args
        // They can or cannot have specific length
        // Here, we read a String, starting at the first argument (included)
        String message = this.readRequiredSentence(0, String.class);

        // Broadcast the message (with colors!)
        Bukkit.broadcastMessage(message.replace('&', '§'));
    }
}
```

Notice the fact that we specify the usage locally.  
You can also see the whole list of sentence type here **TODO**

Finally, let's see a more complex one which combines a lot of things that we learnt :

```java
@PCommandInfo(
    name = "send",
    description = "Send a message to a specific player",
    usage = "<player> <message>",
    permission = "fr.pipilib.commands.message.send"
)
public class SendCommand extends PSubCommand<MessageCommand> {

    @Override
    public void execute(CommandSender sender) {
        // Here, we read the first argument which returns an online player
        // If the player is offline, the code will stop here
        Player player = this.readRequiredArg(0, Player.class);

        // Unlike BroadcastCommand, here the sentence start at the argument 1
        String message = this.readRequiredSentence(1, String.class);

        // We send the message to the player
        player.sendMessage("§6" + sender.getName() + " : §e" + message.replace('&', '§'));
    }
}
```

There is not much to say other than the new type `Player`, which requires the specified player to be online.

## Modify the behaviours of the sub commands

Let's go back to the example from above, and see how we go about modifying the behaviours of the sub commands.  

When we don't specify a first argument or an invalid subcommand, we would like to display the help menu.

```java
@PCommandInfo(
    name = "message",
    aliases = {"msg"},
    description = "A Message command",
    permission = "fr.pipilib.commands.message",
    subCommands = {
        HelpCommand.class,
        BroadcastCommand.class,
        SendCommand.class
    },
    autoManagingSubCommands = false // Disable the auto sub command managing
)
public class MessageCommand extends PCommand {

    @Override
    public void execute(CommandSender sender) {

        // Manually execute the sub command manager
        // This will return true if a sub command was executed
        // Note that if this returns true, the sub command have already been executed
        boolean result = this.executeSubCommands(sender);

        if (!result) { // Check if a sub command was executed

            // No sub commands were executed, we can display the help message
            // (here, we force the command sender to execute a command for simplicity)
            Bukkit.getServer().dispatchCommand(sender, "message help");
        }
    }
}
```

The field `autoManagingSubCommands` is not required to be disabled in this case, it still helps to improve performances,
and to keep control of what's going on.

## Dealing with errors

One of the main problem that we can encounter with commands, and which causes the code to be very long
is the error checking. So, to avoid this problem, we tried to simplify the error management while keeping it
flexible.

To do so, we added a `PCommandErrorHandler` and a `PCommandContext`. And at every step, we update the context,
so that when the error occurs, we can have enough information to send a precise error. We can know
in which command, for what reason, at which index, with which arguments, and at which state.

Here are all the information we can have access to : (from [PCommandContext](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandContext.java))

```java
public class PCommandContext {
    
    private PCommand currentCommand;
    private CommandSender currentSender;
    private String[] currentArgs;
    private int currentIndex;
    private boolean isReadingSentence;
    private String currentError;
}
```

Now, let's dive into the code of the [PCommandErrorHandler](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandErrorHandler.java)
to understand how it works and how we can use it in order to be efficient.

First, we have two fields, which are self describing :

```java
    private final Map<String, Function<PCommandContext, String>> reasons;
    private boolean doesPrintException; // default true
```

Next, we have two methods which helps us register error message :

```java
    /**
     * Use String format, args :
     * 1 : current index (int)
     * 2 : current arg (String)
     * 3 : current command name (String)
     * 4 : current command usage (String)
     *
     * @param reasonName The name of the reason
     * @param message The message which will be formatted
     */
    public void registerReasonMessage(String reasonName, String message) {
        this.registerReasonMessage(reasonName, context -> String.format(
            message,
            context.getCurrentIndex(),
            context.getCurrentIndex() < context.getCurrentArgs().length ? context.getCurrentArgs()[context.getCurrentIndex()] : "",
            context.getCurrentCommand().getFullName(),
            context.getCurrentCommand().getFullUsage()
        ));
    }

    public void registerReasonMessage(String reasonName, Function<PCommandContext, String> message) {
        this.reasons.put(reasonName, message);
    }
```

As you can see, not only we can register a custom message depending on the reason,
but we can also create highly precise message, and even do others actions than sending messages.  

When an error is detected, we can catch this error, and send a message describing the error. 

```java
    public void whenError(PCommandContext context) {
        if (this.reasons.containsKey(context.getCurrentError())) {
            context.getCurrentSender().sendMessage(this.reasons.get(context.getCurrentError()).apply(context));
        }
    }
    
    public void whenError(PCommandContext context, Exception exception) {
        this.whenError(context);
    
        if (doesPrintException) {
            exception.printStackTrace();
        }
    }
```

Notice the fact that there is two methods, one when there is an exception, and another when there isn't.
The one with the exception is called when an exception other than [PCommandException](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandException.java).

Here you can see the `registerDefaults()` method which as the name says, register the default reason message. 

```java
    public void registerDefaults() {
        this.registerReasonMessage("EXCEPTION", "§cException happened while reading the arg \"%2$s\" at index %1$d of the command \"%3$s\".");
        this.registerReasonMessage("CRITICAL", "§cCritical error happened at arg \"%2$s\" of index %1$d with the command \"%3$s\".");

        this.registerReasonMessage("ARG_NULL", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is null!");
        this.registerReasonMessage("EMPTY_STRING", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is an empty String!");

        this.registerReasonMessage("BOOLEAN_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Boolean!");
        this.registerReasonMessage("BYTE_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Byte!");
        this.registerReasonMessage("SHORT_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Short!");
        this.registerReasonMessage("INTEGER_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Integer!");
        this.registerReasonMessage("LONG_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Long!");
        this.registerReasonMessage("FLOAT_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Float!");
        this.registerReasonMessage("DOUBLE_NON_VALID_FORMAT", "§cThe arg \"%2$s\" at index %1$d in the command \"%3$s\" is not a valid Double!");

        this.registerReasonMessage("NOT_ONLINE_PLAYER", "§cThe player \"%2$s\" is not connected!");

        this.registerReasonMessage("MUST_BE_PLAYER", "§cYou need to be a player to be able to do that!");
        this.registerReasonMessage("WRONG_USAGE", "§cWrong usage : \"%4$s\"");
    }
```


> But how do we catch these errors and how do we identify them?

To understand, we first need to visit [PCommandContainer](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandContainer.java)
and check this part :

```java
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        try {
            this.command.internallyExecute(sender, args);
            return true;
        } catch (PCommandException e) {
            this.command.getErrorHandler().whenError(this.command.getContext().setCurrentError(e.getMessage()));
        } catch (Exception e) {
            this.command.getErrorHandler().whenError(this.command.getContext().setCurrentError("EXCEPTION"), e);
        }

        return false;
    }
```

As you can see, we first try to execute the command. (Note that `internallyExecute` is as the name suggests
a method which does some works to simplify your life)  

After, we check for two different types of exception :

- The [PCommandException](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandException.java)
  which is catch for a non-critical exception and is mainly caused by the user who executed the command.  
- The Exception, which means that there were an error in your command implementation (in most cases).

To understand where does this [PCommandException](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandException.java)
comes from, we need to see some parts of the code of [PCommand](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommand.java) :

```java
    @SuppressWarnings("unchecked")
    public <T> T readOptionalArg(int index, T defaultValue) {
        return (T) this.readArg(index, defaultValue.getClass(), defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T readRequiredArg(int index, Class<T> typeClass) {
        return (T) this.readArg(index, typeClass, null);
    }

    public Object readArg(int index, Class<?> typeClass, Object defaultValue) {
        // Update context
        this.context.setCurrentIndex(index);
        this.context.setCurrentArgs(this.currentArgs);
        this.context.setReadingSentence(false);

        // Check if there are enough arguments
        if (index >= this.currentArgs.length) {
            if (defaultValue != null) { // Is optional arg ?
                return defaultValue;
            } else {
                throw new PCommandException("WRONG_USAGE");
            }
        }

        // Try to read the value
        String arg = this.currentArgs[index];
        PArgReader<?> argReader = this.commandRegistry.getArgReader(typeClass);
        Object value = argReader.read(arg);

        if (value != null) { // Have we been able to read the value ?
            return value;
        } else if (defaultValue != null) { // Is optional arg ?
            return defaultValue;
        } else {
            throw new PCommandException(argReader.errorCause(arg));
        }
    }
```

As you can see above, the two functions `readOptionalArg` and `readRequiredArg` are just overloading
the `readArg` function.  

Notice the function `errorCause` at `throw new PCommandException(argReader.errorCause(arg));`?  
To understand what it means, we first need to understand how we can parse the arguments.

To parse an argument, we have an interface called [PArgReader](../core/src/main/java/fr/pixeldeecran/pipilib/command/arg/PArgReader.java) :

```java
public interface PArgReader<T> {

    T read(String arg);

    String errorCause(String arg);

    String getDisplayName();
}
```

You can see three functions, the first one which parse the arg, the second one 
which returns the cause of the error of the arg, and finally the third one which returns the display name
(actually not used, but it can still be useful information in the future).

Let's see a simple implementation of the [PArgReader](../core/src/main/java/fr/pixeldeecran/pipilib/command/arg/PArgReader.java) :

```java
public class CharPAR implements PArgReader<Character> {

    @Override
    public Character read(String arg) {
        return arg.length() > 0 ? arg.charAt(0) : null;
    }

    @Override
    public String errorCause(String arg) {
        return "EMPTY_STRING";
    }

    @Override
    public String getDisplayName() {
        return "Character";
    }
}
```

In the `read` function, you can see that we return `null` when the argument length is `0`,
so that when we try to parse the argument, we can understand that there were an error if an empty string
is used as the argument.

You can also see in the `errorCause` function the name of the error. Note the fact that this method is only
called if the `read` function returns `null`.

These principles are also applied with [PSentenceReader](../core/src/main/java/fr/pixeldeecran/pipilib/command/sentence/PSentenceReader.java).

> Now that I understood how the library handles errors, how can I customize his behaviours?

To do so, you can create your own [PCommandErrorHandler](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandErrorHandler.java)
in order to register your custom messages!

```java
public class ExampleCommand extends PCommand {

    public ExampleCommand() {
        PCommandErrorHandler errorHandler = new PCommandErrorHandler();
        errorHandler.registerDefaults();
        errorHandler.registerReasonMessage("WRONG_USAGE", "§cHey, check that if you didn't understand how to use this command : \"%4$s\"");
        this.setErrorHandler(errorHandler);
    }
}
```

Here, we just create our own [PCommandErrorHandler](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandErrorHandler.java), 
register the defaults messages, and register our custom one for finally replace the old error handler (although we could simply
get the current [PCommandErrorHandler](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandErrorHandler.java), 
and register our own message).

You can also create your own class, which extends of [PCommandErrorHandler](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandErrorHandler.java)
and modify the default behaviours and to have a more reusable error handler!

> And what if I want to register my own [PArgReader](../core/src/main/java/fr/pixeldeecran/pipilib/command/arg/PArgReader.java) 
> or [PSentenceReader](../core/src/main/java/fr/pixeldeecran/pipilib/command/sentence/PSentenceReader.java)?

First, you need to create an implementation of the [PArgReader](../core/src/main/java/fr/pixeldeecran/pipilib/command/arg/PArgReader.java) 
or of the [PSentenceReader](../core/src/main/java/fr/pixeldeecran/pipilib/command/sentence/PSentenceReader.java).

Then, you need to get the instance of your [PCommandRegistry](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandRegistry.java)
(usually accessible via `this.getCommandRegistry()` in your main class).

With it, you can register it :

```java
    @Override
    public void onEnable() {
        // Register our custom types
        this.getCommandRegistry().registerArgReader(YourType.class, new YourTypePAR()); // For PArgReader
        this.getCommandRegistry().registerSentenceReader(YourType.class, new YourTypePSR()); // For PSentenceReader
        
        // Register our commands
        this.getCommandRegistry().registerAllCommandsIn(ExampleCommand.class.getPackage().getName());
    }
```

(PAR stands for PArgReader and PSR stands for PSentenceReader)

See this example where we create a simple Material reader :

```java
public class MaterialPAR implements PArgReader<Material> {

    @Override
    public Material read(String arg) {
        try {
            return Material.matchMaterial(arg);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String errorCause(String arg) {
        return "UNKNOWN_MATERIAL";
    }

    @Override
    public String getDisplayName() {
        return "Material";
    }
}
```

Then we just have to register it :

```java
    @Override
    public void onEnable() {
        // Register our custom types
        this.getCommandRegistry().registerArgReader(Material.class, new MaterialPAR());

        // Register our commands
        this.getCommandRegistry().registerAllCommandsIn(ExampleCommand.class.getPackage().getName());
    }
```

And it's now ready to be used :

```java
    Material material = this.readRequiredArg(0, Material.class);
```

## Want to learn more?

Go check this [example](../core/src/test/java/fr/pipilib/example/commands/EcoCommand.java) which is a simple example of an
economy command, but which shows the flexibility of the library!  

Check also these classes to learn more about what you can do, and how it works internally :

 - [PCommandInfo](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandInfo.java)
 - [PCommand](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommand.java)
 - [PSubCommand](../core/src/main/java/fr/pixeldeecran/pipilib/command/PSubCommand.java)
 - [PCommandRegistry](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandRegistry.java)
 - [PCommandErrorHandler](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandErrorHandler.java)
 - [PCommandContext](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandContext.java)
 - [PCommandContainer](../core/src/main/java/fr/pixeldeecran/pipilib/command/PCommandContainer.java)
