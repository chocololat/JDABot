package org.virep.jdabot.commands.music;


import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.virep.jdabot.language.Language;
import org.virep.jdabot.music.AudioManagerController;
import org.virep.jdabot.music.GuildAudioManager;
import org.virep.jdabot.slashcommandhandler.Command;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ShuffleCommand implements Command {
    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), "Shuffles the queue")
                .setDescriptionLocalization(DiscordLocale.FRENCH, "Mélange la file actuelle.")
                .setGuildOnly(true);
    }

    @Override
    public boolean isDev() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();

        GuildAudioManager manager = AudioManagerController.getGuildAudioManager(guild);

        assert guild != null;

        final GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = Objects.requireNonNull(event.getMember()).getVoiceState();

        assert memberVoiceState != null;
        assert selfVoiceState != null;

        if (memberVoiceState.getChannel() == null) {
            event.reply(Language.getString("MUSIC_NOVOICECHANNEL", guild)).setEphemeral(true).queue();
            return;
        }

        if (!selfVoiceState.inAudioChannel()) {
            event.reply(Language.getString("MUSIC_NOMUSIC", guild)).setEphemeral(true).queue();
            return;
        }

        if (Objects.requireNonNull(selfVoiceState.getChannel()).getIdLong() != memberVoiceState.getChannel().getIdLong()) {
            event.reply(Language.getString("MUSIC_NOTSAMEVC", guild)).setEphemeral(true).queue();
            return;
        }

        Collections.shuffle((List<?>) manager.getScheduler().queue);

        event.reply(Language.getString("SHUFFLE_SHUFFLED", guild)).queue();
    }
}