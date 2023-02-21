package net.Phoenix.utilities.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BridgeCommand {
    String name(); // Name of the command
    String description(); // Description of the command
    CommandOption[] options() default {}; // Options for the command
    SubCommand[] subcommands() default {}; // Subcommands for the command

    // Command option annotation
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface CommandOption {
        OptionType type(); // Type of the option
        String name(); // Name of the option
        String description(); // Description of the option
        boolean required() default false; // Whether the option is required
        String[] choices() default {}; // Possible choices for the option
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface invoke {

    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface OptionValue {
        String name(); // Name of the option
    }

    // Subcommand annotation
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface SubCommand {
        String name(); // Name of the subcommand
        String description() default ""; // Description of the subcommand
        CommandOption[] options() default {}; // Options for the subcommand
    }
}
