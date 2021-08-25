package fr.pixeldeecran.pipilib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PCommandInfo {

    String name();

    String[] aliases() default {};

    String subCommandUsage() default "";

    String usage() default "";

    String description() default "";

    String permission() default "";

    Class<? extends PSubCommand<?>>[] subCommands() default {};

    int subCommandIndex() default 0;

    boolean doesPrintException() default true;
}
