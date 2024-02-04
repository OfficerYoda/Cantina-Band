package de.officeryoda.bot.discord.Listener;

import de.officeryoda.bot.discord.CantinaBand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        CantinaBand cantinaBand = CantinaBand.INSTANCE;
        List<Guild> guilds = event.getJDA().getGuilds();
        for(Guild guild : guilds) {
            //send bot start confirmation
            if(guild.getId().equals(/*DC Bot test server id: */"918946466142228501")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("**Bot booted successfully**")
                        .setColor(Color.LIGHT_GRAY)
                        .setDescription(":man: **Host:** " + System.getProperty("user.home").replace("C:\\Users\\", "")
                                + "\n:hourglass: **Time:** " + (System.currentTimeMillis() - CantinaBand.getStartTime()) + " ms")
                        .setFooter(dtf.format(now), guild.getSelfMember().getUser().getAvatarUrl());

                // send in "Discord Bot test #logs"
                guild.getTextChannelById(923608705470189638L).sendMessageEmbeds(embed.build()).queue();
                embed.clear();
            }
            cantinaBand.addGuild(guild);
        }
        System.out.println("Cantina Band is ready to play.");
    }
}
