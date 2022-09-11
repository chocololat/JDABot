package org.virep.jdabot;

import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.virep.jdabot.database.DatabaseConnector;
import org.virep.jdabot.listeners.EventListener;
import org.virep.jdabot.listeners.LogsListener;
import org.virep.jdabot.external.Notifier;
import org.virep.jdabot.slashcommandhandler.SlashHandler;
import org.virep.jdabot.listeners.SlashListener;
import org.virep.jdabot.utils.Config;
import org.virep.jdabot.utils.DatabaseUtils;

import java.net.URI;
import java.sql.Connection;
public class Main {
    static Main instance;

    Notifier notifier;
    public static JDA PublicJDA = null;
    public static final Connection connectionDB = DatabaseConnector.openConnection();
    public static void main(String[] args) throws Exception {
        instance = new Main();
        instance.notifier = new Notifier();

        JDA api = JDABuilder
                .createDefault(Config.get("TOKEN"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(lavalink, new EventListener(), new LogsListener())
                .setBulkDeleteSplittingEnabled(false)
                .setVoiceDispatchInterceptor(lavalink.getVoiceInterceptor())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.VOICE_STATE).build().awaitReady();

        SlashHandler slashHandler = new SlashHandler(api);

        api.addEventListener(new SlashListener(slashHandler));

        slashHandler.addCommands();

        instance.notifier.registerTwitterUser(DatabaseUtils.getAllTwitterNames());

        lavalink.setAutoReconnect(true);
        lavalink.addNode(URI.create(Config.get("LAVALINKURI")), Config.get("LAVALINKPWD"));

        PublicJDA = api;
    }

    public static final JdaLavalink lavalink = new JdaLavalink(
            "816407992505073725",
            1,
            integer -> PublicJDA
    );

    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }

        return instance;
    }

    public Notifier getNotifier() {
        return notifier;
    }
}