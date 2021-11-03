package fr.pixeldeecran.pixellib.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells {@link PCommandRegistry} that we need to register this command. This is required when using
 * {@link PCommandRegistry#registerAllCommandsIn(String)}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PCommandExist {

}
