package org.virep.jdabot.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import org.virep.jdabot.language.Language;
import org.virep.jdabot.music.AudioLoadHandler;
import org.virep.jdabot.music.AudioManagerController;
import org.virep.jdabot.music.GuildAudioManager;
import org.virep.jdabot.slashcommandhandler.Command;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class PlayCommand implements Command {

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getName(), "Play music on any voice channel.")
                .setDescriptionLocalization(DiscordLocale.FRENCH, "Joue de la musique sur n'importe quel salon vocal.")
                .setGuildOnly(true)
                .addSubcommands(
                        new SubcommandData("search", "Search for any songs!")
                                .setDescriptionLocalization(DiscordLocale.FRENCH, "Cherche pour n'importe quel musique !")
                                .addOptions(
                                        new OptionData(OptionType.STRING, "search", "The song or artist to search.", true)
                                                .setDescriptionLocalization(DiscordLocale.FRENCH, "La musique ou artiste a rechercher")
                                ),
                        new SubcommandData("soundcloud", "Play SoundCloud songs!")
                                .setDescriptionLocalization(DiscordLocale.FRENCH, "Joue de la musique à partir de SoundCloud !")
                                .addOptions(
                                        new OptionData(OptionType.STRING, "url", "SoundCloud Track or Playlist URL")
                                                .setDescriptionLocalization(DiscordLocale.FRENCH, "Lien de musique ou playlist SoundCloud"),
                                        new OptionData(OptionType.STRING, "search", "Search string")
                                                .setDescriptionLocalization(DiscordLocale.FRENCH, "La musique ou artiste a rechercher.")
                                ),
                        new SubcommandData("url", "Play URLs from random sources.")
                                .setDescriptionLocalization(DiscordLocale.FRENCH, "Lire de la musique à partir d'un lien")
                                .addOptions(
                                        new OptionData(OptionType.STRING, "url", "URL string", true)
                                                .setDescriptionLocalization(DiscordLocale.FRENCH, "Lien à jouer")
                                ),
                        new SubcommandData("file", "Play the audio files you attach !")
                                .setDescriptionLocalization(DiscordLocale.FRENCH, "Joue les fichiers attachés a la commande.")
                                .addOptions(
                                        new OptionData(OptionType.ATTACHMENT, "file", "Audio file", true)
                                                .setDescriptionLocalization(DiscordLocale.FRENCH, "Fichier audio à jouer")
                                )
                );
    }

    @Override
    public boolean isDev() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        GuildAudioManager manager = AudioManagerController.getGuildAudioManager(event.getGuild());

        Guild guild = event.getGuild();

        assert guild != null;
        final GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();
        final GuildVoiceState memberVoiceState = Objects.requireNonNull(event.getMember()).getVoiceState();

        assert memberVoiceState != null;
        assert selfVoiceState != null;

        OptionMapping urlOption = event.getOption("url");
        OptionMapping searchOption = event.getOption("search");
        OptionMapping fileOption = event.getOption("file");

        String result = "";

        if (urlOption == null && searchOption == null && fileOption == null) {
            event.getHook().editOriginal(Language.getString("PLAY_NOOPTION", guild)).queue();
            return;
        }

        if (urlOption != null && searchOption != null) {
            event.getHook().editOriginal(Language.getString("PLAY_ONEOPTION", guild)).queue();
            return;
        }

        if (event.getSubcommandName().equals("search")) {
            result = "dsearch:" + searchOption.getAsString();
        } else {
            if (urlOption != null) {
                try {
                    new URL(urlOption.getAsString());
                    result = urlOption.getAsString();
                } catch (MalformedURLException e) {
                    event.getHook().editOriginal(Language.getString("PLAY_NOTVALIDURL", guild)).queue();
                    return;
                }
            }

            if (searchOption != null) {
                assert event.getSubcommandName() != null;

                result = "scsearch:" + searchOption.getAsString();
            }

            if (fileOption != null) {
                Attachment attachment = fileOption.getAsAttachment();

                result = attachment.getProxyUrl();
            }
        }

        if (memberVoiceState.getChannel() == null) {
            event.getHook().editOriginal(Language.getString("MUSIC_NOVOICECHANNEL", guild)).queue();
            return;
        }

        if (!selfVoiceState.inAudioChannel()) {
            manager.openConnection((VoiceChannel) memberVoiceState.getChannel());
        } else if (Objects.requireNonNull(selfVoiceState.getChannel()).getIdLong() != memberVoiceState.getChannel().getIdLong()) {
            event.getHook().editOriginal(Language.getString("MUSIC_NOTSAMEVC", guild)).queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.setSelfDeafened(true);

        AudioLoadHandler.loadAndPlay(manager, result, event);
    }
}