package org.virep.jdabot.commands.general;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.virep.jdabot.slashcommandhandler.Command;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MemeCommand implements Command {
    @Override
    public String getName() {
        return "meme";
    }

    @Override
    public CommandData getCommandData() {
        return new CommandDataImpl(getName(), "Returns a random meme from Reddit.")
                .addOption(OptionType.STRING, "subreddit", "The subreddit you want to see memes from.");
    }

    @Override
    public boolean isDev() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String[] subreddits = {"dankmemes", "memes", "crappyoffbrands", "MemeEconomy", "me_irl"};

        OptionMapping subredditOption = event.getOption("subreddit");

        String subreddit = subredditOption != null ? subredditOption.getAsString() : subreddits[(int) Math.floor(Math.random() * subreddits.length)];

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        try {
            Request request = new Request.Builder()
                    .url("https://www.reddit.com/r/" + subreddit+ "/top.json?sort=top&t=day&limit=500")
                    .build();

            Response res = client.newCall(request).execute();

            assert res.body() != null;

            JSONObject jsonObject = new JSONObject(res.body().string());

            JSONArray childrens = jsonObject.getJSONObject("data").getJSONArray("children");
            JSONObject children = childrens.getJSONObject((int) Math.floor(Math.random() * childrens.length())).getJSONObject("data");

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle(children.getString("title"), "https://reddit.com" + children.getString("permalink"))
                    .setImage(children.getString("url"))
                    .setColor(0x9590EE)
                    .setAuthor(event.getUser().getAsTag(), null, event.getUser().getAvatarUrl())
                    .setFooter("\uD83D\uDC4D " + children.getInt("ups") + " | \uD83D\uDC4E " + children.getInt("downs") + " | r/" + children.getString("subreddit"))
                    .build();


            event.replyEmbeds(embed).queue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}