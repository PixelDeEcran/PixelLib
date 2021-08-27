package fr.pixeldeecran.pipilib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents the command information.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PCommandInfo {

    /**
     * @return The name of the command
     */
    String name();

    /**
     * @return The aliases of the command
     */
    String[] aliases() default {};

    /**
     * @return The main usage of the command
     */
    String usage() default "";

    /**
     * @return The sub-command's usage
     */
    String subCommandUsage() default "";

    /**
     * @return The description of the command
     */
    String description() default "";

    /**
     * If the permission is empty, this will be considered as a command usable by everyone.
     *
     * @return The permission of the command
     */
    String permission() default "";

    /**
     * @return The classes of the sub-commands
     */
    Class<? extends PSubCommand<?>>[] subCommands() default {};

    /**
     * @return The index at which the sub-command start
     */
    int subCommandIndex() default 0;

    /**
     * @return Do we need to print exception?
     */
    boolean doesPrintException() default true;

    /**
     * @return Do we need to auto-manage the sub-commands?
     */
    boolean autoManagingSubCommands() default true;

    /**
     * @return Do we need to auto-check the permission?
     */
    boolean autoCheckPermission() default true;
}
