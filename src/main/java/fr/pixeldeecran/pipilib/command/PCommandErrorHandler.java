package fr.pixeldeecran.pipilib.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PCommandErrorHandler {

    private final Map<String, Function<PCommandContext, String>> reasons;

    private boolean doesPrintException;

    public PCommandErrorHandler() {
        this.reasons = new HashMap<>();
        this.doesPrintException = true;
    }

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

    public void setDoesPrintException(boolean doesPrintException) {
        this.doesPrintException = doesPrintException;
    }

    public boolean doesPrintException() {
        return doesPrintException;
    }
}
