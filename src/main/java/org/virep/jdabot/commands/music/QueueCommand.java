package org.virep.jdabot.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.virep.jdabot.music.AudioManagerController;
import org.virep.jdabot.music.GuildAudioManager;
import org.virep.jdabot.music.TrackScheduler;
import org.virep.jdabot.slashcommandhandler.Command;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueCommand implements Command {
    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), "Returns the current music queue.")
                .setDescriptionLocalization(DiscordLocale.FRENCH, "Renvoie la file de musique actuelle.")
                .setGuildOnly(true);
    }

    @Override
    public boolean isDev() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        GuildAudioManager guildAudioManager = AudioManagerController.getGuildAudioManager(event.getGuild());
        TrackScheduler trackScheduler = guildAudioManager.getScheduler();

        if (trackScheduler.queue.size() > 0) event.replyEmbeds(paginate(event))
                .addActionRow(Button.secondary("queueFull", Emoji.fromUnicode("\uD83D\uDCC3")))
                .queue();
        else event.reply("Nothing is in the queue right now !\n\nIf you want to add songs, use the `/play` command.\nIf you want to check the currently playing music (if any), use the `/nowplaying` command.").setEphemeral(true).queue();
    }

    public static MessageEmbed paginate(GenericInteractionCreateEvent event) {
        GuildAudioManager guildAudioManager = AudioManagerController.getGuildAudioManager(event.getGuild());
        TrackScheduler trackScheduler = guildAudioManager.getScheduler();

        StringBuilder queueBuilder = new StringBuilder();
        AtomicInteger counter = new AtomicInteger();

        guildAudioManager.getScheduler().queue.stream().limit(20).forEach(audioTrack -> {
            counter.getAndIncrement();
            queueBuilder
                    .append("\n")
                    .append("[")
                    .append(counter.get())
                    .append("] ")
                    .append(audioTrack.getInfo().getTitle())
                    .append(" - ")
                    .append(audioTrack.getInfo().getAuthor());
        });

        assert event.getGuild() != null;
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTimestamp(Instant.now())
                .setAuthor("Queue for " + event.getGuild().getName(), event.getGuild().getIconUrl())
                .setDescription(queueBuilder.toString())
                .setFooter("The queue has " + trackScheduler.queue.size() + " total tracks.");

        return embed.build();
    }
}