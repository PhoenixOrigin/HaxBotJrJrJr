package net.Phoenix.utilities.commands;

import net.Phoenix.utilities.Utilities;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SlashCommandHandler extends ListenerAdapter {

    public static void registerCommands (JDA jda) {
        Reflections reflections = new Reflections("net.Phoenix");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(BridgeCommand.class);
        for (Class<?> c : annotated) {
            for (Method method : c.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(BridgeCommand.invoke.class)) continue;
                BridgeCommand commandAnnotation = method.getDeclaringClass().getAnnotation(BridgeCommand.class);
                SlashCommandData commandData = Commands.slash(commandAnnotation.name(), commandAnnotation.description());
                for (BridgeCommand.CommandOption commandOption : commandAnnotation.options()) {
                    commandData.addOptions(new OptionData(commandOption.type(), commandOption.name(), commandOption.description(), commandOption.required()));
                }
                for (BridgeCommand.SubCommand subCommand : commandAnnotation.subcommands()) {
                    SubcommandData data = new SubcommandData(subCommand.name(), subCommand.description());

                    for (BridgeCommand.CommandOption commandOption : subCommand.options()) {
                        data.addOptions(new OptionData(commandOption.type(), commandOption.name(), commandOption.description(), commandOption.required()));
                    }

                    commandData.addSubcommands(data);
                }
                jda.upsertCommand(commandData).queue();
                jda.addEventListener(new BridgeCommandListener(method, commandAnnotation));
            }

            for (Method method : c.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(BridgeCommand.SubCommand.class)) continue;
                List<Method> subCommands = new ArrayList<>();
                for (Method m : c.getDeclaredMethods()) {
                    if(m.isAnnotationPresent(BridgeCommand.SubCommand.class)) subCommands.add(m);
                }

                BridgeCommand commandAnnotation = method.getDeclaringClass().getAnnotation(BridgeCommand.class);

                SlashCommandData commandData = Commands.slash(commandAnnotation.name(), commandAnnotation.description());
                for (BridgeCommand.CommandOption commandOption : commandAnnotation.options()) {
                    commandData.addOptions(new OptionData(commandOption.type(), commandOption.name(), commandOption.description(), commandOption.required()));
                }
                for (BridgeCommand.SubCommand subCommand : commandAnnotation.subcommands()) {
                    SubcommandData data = new SubcommandData(subCommand.name(), subCommand.description());

                    for (BridgeCommand.CommandOption commandOption : subCommand.options()) {
                        data.addOptions(new OptionData(commandOption.type(), commandOption.name(), commandOption.description(), commandOption.required()));
                    }

                    commandData.addSubcommands(data);
                }

                jda.upsertCommand(commandData).queue();
                jda.addEventListener(new SubBridgeCommandListener(commandAnnotation, subCommands));
            }
        }
    }

    public static class BridgeCommandListener extends ListenerAdapter {

        Method command;
        BridgeCommand annotation;

        public BridgeCommandListener(Method command, BridgeCommand annotation) {
            this.command = command;
            this.annotation = annotation;
        }

        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            if (!event.getName().equals(annotation.name())) return;
            BridgeCommand.CommandOption[] options = annotation.options();
            if (options.length == 0) {
                try {
                    command.invoke(command.getDeclaringClass(), List.of(event.getInteraction()).toArray());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            } else {
                List<Parameter> parameters = Arrays.stream(command.getParameters()).toList();
                List<Object> args = new ArrayList<>();
                args.add(event.getInteraction());
                for (OptionMapping option : event.getOptions()) {
                    for (Parameter parameter : parameters) {
                        if (parameter.isAnnotationPresent(BridgeCommand.OptionValue.class) && parameter.getAnnotation(BridgeCommand.OptionValue.class).name().equals(option.getName())) {
                            args.add(parameters.indexOf(parameter), Utilities.getOptionValue(option));
                            break;
                        }
                    }
                }
                try {
                    command.invoke(command.getDeclaringClass(), args.toArray());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static class SubBridgeCommandListener extends ListenerAdapter {
        BridgeCommand annotation;
        List<Method> subCommands;

        public SubBridgeCommandListener(BridgeCommand annotation, List<Method> subCommands) {
            this.annotation = annotation;
            this.subCommands = subCommands;
        }

        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            if (!event.getName().equals(annotation.name())) return;
            for (Method subCommand : subCommands) {
                if (!subCommand.isAnnotationPresent(BridgeCommand.SubCommand.class) || !subCommand.getAnnotation(BridgeCommand.SubCommand.class).name().equals(event.getSubcommandName())) {
                    return;
                }

                BridgeCommand.CommandOption[] options = null;
                for(BridgeCommand.SubCommand subcommand : subCommand.getDeclaringClass().getAnnotation(BridgeCommand.class).subcommands()) {
                    if(subcommand.name().equals(event.getSubcommandName())) options = subcommand.options();
                }
                if (options.length == 0) {
                    try {
                        subCommand.invoke(subCommand.getDeclaringClass(), List.of(event.getInteraction()).toArray());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    List<Parameter> parameters = Arrays.stream(subCommand.getParameters()).toList();
                    List<Object> args = new ArrayList<>();
                    args.add(event.getInteraction());
                    for (OptionMapping option : event.getOptions()) {
                        for (Parameter parameter : parameters) {
                            if (parameter.isAnnotationPresent(BridgeCommand.OptionValue.class) && parameter.getAnnotation(BridgeCommand.OptionValue.class).name().equals(option.getName())) {
                                args.add(parameters.indexOf(parameter), Utilities.getOptionValue(option));
                                break;
                            }
                        }
                    }
                    try {
                        subCommand.invoke(subCommand.getDeclaringClass(), args.toArray());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
    }
}