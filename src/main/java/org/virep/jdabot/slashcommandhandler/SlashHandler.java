package org.virep.jdabot.slashcommandhandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//TODO: For production stage, add developerOnly commands through Guild#updateCommands()
public class SlashHandler {
    private final JDA jda;
    private final Map<String, SlashCommand> slashCommandMap = new HashMap<>();

    public SlashHandler(JDA jda) {
        this.jda = jda;
    }

    // IntelliJ thinks CommandListUpdateAction#addCommands() result is ignored
    // though it is queued line 38

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void addCommands() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Guild guild = jda.getGuildById("418433461817180180");
        assert guild != null;

        CommandListUpdateAction globalUpdate = jda.updateCommands();

        Reflections reflectionsCommands = new Reflections("org.virep.jdabot.commands");
        Set<Class<? extends SlashCommand>> commandClasses = reflectionsCommands.getSubTypesOf(SlashCommand.class);

        for (Class<? extends SlashCommand> commandClass : commandClasses) {
            if (Modifier.isAbstract(commandClass.getModifiers())) {
                continue;
            }

            SlashCommand command = commandClass.getConstructor().newInstance();

            if (command.hasOptions(command.options) && !command.hasSubcommandData(command.subcommandData)) globalUpdate.addCommands(Commands.slash(command.getName(), command.getDescription()).addOptions(command.getOptions()));
            else if (!command.hasOptions(command.options) && command.hasSubcommandData(command.subcommandData)) globalUpdate.addCommands(Commands.slash(command.getName(), command.getDescription()).addSubcommands(command.getSubcommandData()));
            else if (!command.hasOptions(command.options) && !command.hasSubcommandData(command.subcommandData)) globalUpdate.addCommands(Commands.slash(command.getName(), command.getDescription()));

            slashCommandMap.put(command.getName(), command);
        }


        CommandListUpdateAction update = guild.updateCommands();

        Reflections reflectionsInDev = new Reflections("org.virep.jdabot.devcommands");
        Set<Class<? extends SlashCommand>> commandInDevClasses = reflectionsInDev.getSubTypesOf(SlashCommand.class);
        for (Class<? extends SlashCommand> commandInDevClass : commandInDevClasses) {
            if (Modifier.isAbstract(commandInDevClass.getModifiers())) {
                continue;
            }

            SlashCommand command = commandInDevClass.getConstructor().newInstance();

            if (command.hasOptions(command.options) && !command.hasSubcommandData(command.subcommandData)) update.addCommands(Commands.slash(command.getName(), command.getDescription()).addOptions(command.getOptions()));
            else if (!command.hasOptions(command.options) && command.hasSubcommandData(command.subcommandData)) update.addCommands(Commands.slash(command.getName(), command.getDescription()).addSubcommands(command.getSubcommandData()));
            else if (!command.hasOptions(command.options) && !command.hasSubcommandData(command.subcommandData)) update.addCommands(Commands.slash(command.getName(), command.getDescription()));

            slashCommandMap.put(command.getName(), command);
        }

        globalUpdate.queue();
        update.queue();
    }

    public Map<String, SlashCommand> getSlashCommandMap() {
        return this.slashCommandMap;
    }
}
