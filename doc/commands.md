# Commands

In this topic, you will how learn to create commands, from simple one to complex one with sub commands.

## Basic Example 

First, let's see a basic [example](../src/test/java/fr/pipilib/example/commands/ExampleCommand.java) :

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
Let's see another [example](../src/test/java/fr/pipilib/example/commands/MessageCommand.java) :

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

There is not much to say other than the new type `Player`, which required the specified player to be online.

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
