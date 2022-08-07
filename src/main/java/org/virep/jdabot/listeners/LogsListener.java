package org.virep.jdabot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNSFWEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateSlowmodeEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateTopicEvent;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.virep.jdabot.utils.DatabaseUtils.getLogChannelID;
import static org.virep.jdabot.utils.DatabaseUtils.isEnabled;
import static org.virep.jdabot.utils.Utils.secondsToSeperatedTime;

public class LogsListener extends ListenerAdapter {
    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        ChannelType type = event.getChannelType();

        if (type == ChannelType.PRIVATE | type == ChannelType.UNKNOWN) return;

        if (isEnabled("channelCreate", event.getGuild().getId())) {
            MessageEmbed embed = new EmbedBuilder()
                    .setDescription("**" + Type.valueOf(type.toString()).type + " channel created: " + event.getChannel().getAsMention() + "**")
                    .setColor(3066993)
                    .setFooter("Channel ID: " + event.getChannel().getId())
                    .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                    .setTimestamp(Instant.now())
                    .build();


            String logChannelID = getLogChannelID(event.getGuild().getId());

            assert logChannelID != null;
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

            if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {
        ChannelType type = event.getChannelType();

        if (type == ChannelType.PRIVATE | type == ChannelType.UNKNOWN) return;

        if (isEnabled("channelDelete", event.getGuild().getId())) {
            MessageEmbed embed = new EmbedBuilder()
                    .setDescription("**" + Type.valueOf(type.toString()).type + " channel delete: " + event.getChannel().getName() + "**")
                    .setColor(3066993)
                    .setFooter("Channel ID: " + event.getChannel().getId())
                    .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                    .setTimestamp(Instant.now())
                    .build();


            String logChannelID = getLogChannelID(event.getGuild().getId());

            assert logChannelID != null;
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

            if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public void onChannelUpdateNSFW(ChannelUpdateNSFWEvent event) {
        if (isEnabled("channelNSFWUpdate", event.getGuild().getId())) {
            boolean oldValue = event.getOldValue();
            boolean newValue = event.getNewValue();

            MessageEmbed embed = new EmbedBuilder()
                    .setDescription("**" + Type.valueOf(event.getChannel().getType().toString()).type + " channel NSFW status changed: " + event.getChannel().getAsMention() + "**")
                    .addField("Old:", (oldValue ? "__**Enabled**__" : "__**Disabled**__"), true)
                    .addField("New:", (newValue ? "__**Enabled**__" : "__**Disabled**__"), true)
                    .setColor(3066993)
                    .setFooter("Channel ID: " + event.getChannel().getId())
                    .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                    .setTimestamp(Instant.now())
                    .build();

            String logChannelID = getLogChannelID(event.getGuild().getId());

            assert logChannelID != null;
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

            if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public void onChannelUpdateTopic(ChannelUpdateTopicEvent event) {
        if (isEnabled("channelTopicUpdate", event.getGuild().getId())) {
            String oldValue = event.getOldValue();
            String newValue = event.getNewValue();

            MessageEmbed embed = new EmbedBuilder()
                    .setDescription("**" + Type.valueOf(event.getChannel().getType().toString()).type + " channel topic changed: " + event.getChannel().getAsMention() + "**")
                    .addField("Old:", (oldValue != null ? oldValue : "No Topic Specified"), true)
                    .addField("New:", (newValue != null ? newValue : "No Topic Specified"), true)
                    .setColor(3066993)
                    .setFooter("Channel ID: " + event.getChannel().getId())
                    .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                    .setTimestamp(Instant.now())
                    .build();

            String logChannelID = getLogChannelID(event.getGuild().getId());

            assert logChannelID != null;
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

            if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public void onChannelUpdateName(ChannelUpdateNameEvent event) {
        if (isEnabled("channelNameUpdate", event.getGuild().getId())) {
            String oldName = event.getOldValue();
            String newName = event.getNewValue();

            MessageEmbed embed = new EmbedBuilder()
                    .setDescription("**" + Type.valueOf(event.getChannel().getType().toString()).type + " channel name changed: " + event.getChannel().getAsMention() + "**")
                    .addField("Old:", "**" + oldName + "**", true)
                    .addField("New:", "**" + newName + "**", true)
                    .setColor(3066993)
                    .setFooter("Channel ID: " + event.getChannel().getId())
                    .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                    .setTimestamp(Instant.now())
                    .build();

            String logChannelID = getLogChannelID(event.getGuild().getId());

            assert logChannelID != null;
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

            if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public void onChannelUpdateSlowmode(ChannelUpdateSlowmodeEvent event) {
        if (isEnabled("channelSlowmodeUpdate", event.getGuild().getId())) {
            int oldValue = event.getOldValue();
            int newValue = event.getNewValue();

            MessageEmbed embed = new EmbedBuilder()
                    .setDescription("**" + Type.valueOf(event.getChannel().getType().toString()).type + " channel slowmode changed: " + event.getChannel().getAsMention() + "**")
                    .addField("Old:", "**" + secondsToSeperatedTime(oldValue) + "**", true)
                    .addField("New:", "**" + secondsToSeperatedTime(newValue) + "**", true)
                    .setColor(3066993)
                    .setFooter("Channel ID: " + event.getChannel().getId())
                    .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                    .setTimestamp(Instant.now())
                    .build();

            String logChannelID = getLogChannelID(event.getGuild().getId());

            assert logChannelID != null;
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

            if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public void onEmojiAdded(EmojiAddedEvent event) {
        RichCustomEmoji emoji = event.getEmoji();
        String emojiFormatted = emoji.getFormatted();

        if (isEnabled("emojiAdded", event.getGuild().getId())) {
            MessageEmbed embed = new EmbedBuilder()
                    .setDescription("**Emoji: " + emojiFormatted + " (" + emoji.getName() + ") has been created**")
                    .setColor(3066993)
                    .setFooter("Emoji ID: " + emoji.getId())
                    .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                    .setTimestamp(Instant.now())
                    .build();

            String logChannelID = getLogChannelID(event.getGuild().getId());

            assert logChannelID != null;
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

            if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public void onEmojiRemoved(EmojiRemovedEvent event) {
        RichCustomEmoji emoji = event.getEmoji();

        if (isEnabled("emojiRemoved", event.getGuild().getId())) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(3066993)
                    .setFooter("Emoji ID: " + emoji.getId())
                    .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                    .setTimestamp(Instant.now())
                    .build();

            String logChannelID = getLogChannelID(event.getGuild().getId());

            assert logChannelID != null;
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

            if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public void onEmojiUpdateName(EmojiUpdateNameEvent event) {
        RichCustomEmoji emoji = event.getEmoji();
        String emojiFormatted = event.getEmoji().getFormatted();

        String oldName = event.getOldName();
        String newName = event.getNewName();

        if (isEnabled("emojiUpdateName", event.getGuild().getId())) {
            MessageEmbed embed = new EmbedBuilder()
                    .setDescription("**Emoji: " + emojiFormatted + " (" + emoji.getName() + ") name has been updated**")
                    .addField("Old:", "**" + oldName + "**", true)
                    .addField("New:", "**" + newName + "**", true)
                    .setColor(3066993)
                    .setFooter("Emoji ID: " + emoji.getId())
                    .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                    .setTimestamp(Instant.now())
                    .build();

            String logChannelID = getLogChannelID(event.getGuild().getId());

            assert logChannelID != null;
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

            if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        if (isEnabled("guildBan", event.getGuild().getId())) {
            User bannedUser = event.getUser();

            AuditLogPaginationAction auditLogs = event.getGuild().retrieveAuditLogs();

            auditLogs.type(ActionType.BAN);
            auditLogs.limit(1);

            auditLogs.queue((entries) -> {
                String moderator = entries.isEmpty() ? "N/A" : entries.get(0).getUser().getAsTag();
                String reason = entries.isEmpty() ? "N/A" : entries.get(0).getReason();

                MessageEmbed embed = new EmbedBuilder()
                        .setDescription("User: " + bannedUser.getAsTag())
                        .setColor(15158332)
                        .setAuthor("User banned:", null, bannedUser.getAvatarUrl())
                        .setThumbnail(bannedUser.getAvatarUrl())
                        .addField("Reason:", reason != null ? reason : "N/A", true)
                        .addField("Banned by:", moderator, true)
                        .setTimestamp(Instant.now())
                        .setFooter("ID: " + bannedUser.getId())
                        .build();

                String logChannelID = getLogChannelID(event.getGuild().getId());

                assert logChannelID != null;
                TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

                if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
            });
        }
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        if (isEnabled("guildUnban", event.getGuild().getId())) {
            User unbannedUser = event.getUser();

            AuditLogPaginationAction auditLogs = event.getGuild().retrieveAuditLogs();

            auditLogs.type(ActionType.UNBAN);
            auditLogs.limit(1);

            auditLogs.queue((entries) -> {
                String moderator = entries.isEmpty() ? "N/A" : entries.get(0).getUser().getAsTag();

                MessageEmbed embed = new EmbedBuilder()
                        .setDescription("User: " + unbannedUser.getAsTag())
                        .setColor(15158332)
                        .setAuthor("User unbanned:", null, unbannedUser.getAvatarUrl())
                        .setThumbnail(unbannedUser.getAvatarUrl())
                        .addField("Unbanned by:", moderator, true)
                        .setTimestamp(Instant.now())
                        .setFooter("ID: " + unbannedUser.getId())
                        .build();

                String logChannelID = getLogChannelID(event.getGuild().getId());

                assert logChannelID != null;
                TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

                if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
            });
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (isEnabled("guildMemberJoin", event.getGuild().getId())) {
            User member = event.getUser();
            MessageEmbed embed = new EmbedBuilder()
                    .setDescription(member.getAsMention() + "\nMember count: " + event.getGuild().getMemberCount())
                    .setColor(3066993)
                    .setFooter("ID: " + member.getId())
                    .setThumbnail(member.getAvatarUrl())
                    .setAuthor("Member joined:", null, member.getAvatarUrl())
                    .setTimestamp(Instant.now())
                    .build();

            String logChannelID = getLogChannelID(event.getGuild().getId());

            assert logChannelID != null;
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

            if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        if (isEnabled("guildMemberRemove", event.getGuild().getId())) {
            User member = event.getUser();
            MessageEmbed embed = new EmbedBuilder()
                    .setDescription(member.getAsMention() + "\nMember count: " + event.getGuild().getMemberCount())
                    .setColor(15158332)
                    .setFooter("ID: " + member.getId())
                    .setThumbnail(member.getAvatarUrl())
                    .setAuthor("Member left:", null, member.getAvatarUrl())
                    .setTimestamp(Instant.now())
                    .build();

            String logChannelID = getLogChannelID(event.getGuild().getId());

            assert logChannelID != null;
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

            if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        System.out.println("ll");
        if (isEnabled("guildMemberRoleAdd", event.getGuild().getId())) {
            List<Role> roles = event.getRoles();
            StringBuilder addedRoles = new StringBuilder();
            User member = event.getUser();

            if (!roles.isEmpty()) {
                for (Role role : roles) {
                    addedRoles.append(role.getName()).append(", ");
                }

                String roleString = addedRoles.toString();

                MessageEmbed embed = new EmbedBuilder()
                        .setDescription("**" + member.getAsTag() + " roles added**")
                        .setFooter("ID: " + member.getId())
                        .setAuthor(member.getAsTag(), null, member.getAvatarUrl())
                        .addField("Added roles", roleString.substring(0, roleString.length() - 2), true)
                        .setTimestamp(Instant.now())
                        .build();

                String logChannelID = getLogChannelID(event.getGuild().getId());

                assert logChannelID != null;
                TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

                if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
            }
        }
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if (isEnabled("guildMemberRoleRemove", event.getGuild().getId())) {
            List<Role> roles = event.getRoles();
            StringBuilder removedRoles = new StringBuilder();
            User member = event.getUser();

            if (!roles.isEmpty()) {
                for (Role role : roles) {
                    removedRoles.append(role.getName()).append(", ");
                }

                String roleString = removedRoles.toString();

                MessageEmbed embed = new EmbedBuilder()
                        .setDescription("**" + member.getAsTag() + " roles removed**")
                        .setFooter("ID: " + member.getId())
                        .setAuthor(member.getAsTag(), null, member.getAvatarUrl())
                        .addField("Removed roles", roleString.substring(0, roleString.length() - 2), true)
                        .setTimestamp(Instant.now())
                        .build();

                String logChannelID = getLogChannelID(event.getGuild().getId());

                assert logChannelID != null;
                TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

                if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
            }
        }
    }

    @Override
    public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
        if (isEnabled("guildMemberUpdateNickname", event.getGuild().getId())) {
            Member member = event.getMember();

            String oldNickname = event.getOldNickname();
            String newNickname = event.getNewNickname();

            MessageEmbed embed = new EmbedBuilder()
                    .setDescription("**" + member.getUser().getAsTag() + " nickname changed**")
                    .setFooter("ID: " + member.getId())
                    .setAuthor(member.getUser().getAsTag(), null, member.getUser().getAvatarUrl())
                    .addField("Old:", oldNickname != null ? oldNickname : "None", true)
                    .addField("New:", newNickname != null ? newNickname : "None", true)
                    .setTimestamp(Instant.now())
                    .build();

            String logChannelID = getLogChannelID(event.getGuild().getId());

            assert logChannelID != null;
            TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

            if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public void onGuildMemberUpdateTimeOut(GuildMemberUpdateTimeOutEvent event) {
        if (isEnabled("guildMemberUpdateTimeOut", event.getGuild().getId())) {
            AuditLogPaginationAction auditLogs = event.getGuild().retrieveAuditLogs();

            auditLogs.type(ActionType.MEMBER_UPDATE);
            auditLogs.limit(1);

            auditLogs.queue((entries) -> {
                String moderator = entries.isEmpty() ? "N/A" : entries.get(0).getUser().getAsTag();
                String reason = entries.isEmpty() ? "N/A" : entries.get(0).getReason();

                User member = event.getUser();
                OffsetDateTime oldTimeout = event.getOldTimeOutEnd();
                OffsetDateTime newTimeout = event.getNewTimeOutEnd();

                MessageEmbed embed = new EmbedBuilder()
                        .setDescription("**" + member.getAsTag() + " timeout changed**")
                        .addField("Old Timeout:", oldTimeout != null ? "Until <t:" + oldTimeout.toEpochSecond() + ":F>" : "None", true)
                        .addField("New Timeout:", newTimeout != null ? "Until <t:" + newTimeout.toEpochSecond() + ":F>" : "None", true)
                        .addField("Reason:", reason != null ? reason : "N/A", false)
                        .addField("Timed out by:", moderator, true)
                        .setColor(3066993)
                        .setFooter("ID: " + member.getId())
                        .setAuthor(member.getAsTag(), null, member.getAvatarUrl())
                        .setTimestamp(Instant.now())
                        .build();

                String logChannelID = getLogChannelID(event.getGuild().getId());

                assert logChannelID != null;
                TextChannel logChannel = event.getGuild().getTextChannelById(logChannelID);

                if (logChannel != null) logChannel.sendMessageEmbeds(embed).queue();
            });
        }
    }

    enum Type {
        CATEGORY("Category"),
        GUILD_NEWS_THREAD("News Thread"),
        GUILD_PRIVATE_THREAD("Private Thread"),
        GUILD_PUBLIC_THREAD("Public Thread"),
        NEWS("News"),
        STAGE("Stage"),
        TEXT("Text"),
        VOICE("Voice");

        public final String type;

        Type(String type) {
            this.type = type;
        }
    }
}
