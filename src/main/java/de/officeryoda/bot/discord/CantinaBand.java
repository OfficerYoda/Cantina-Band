package de.officeryoda.bot.discord;

import de.officeryoda.bot.discord.Commands.Managment.CommandManager;
import de.officeryoda.bot.discord.Listener.ButtonListener;
import de.officeryoda.bot.discord.Listener.CommandListener;
import de.officeryoda.bot.discord.Listener.ReadyListener;
import de.officeryoda.bot.discord.Music.MusicMaster;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class CantinaBand {

    public final static Color EMBED_COLOR = Color.DARK_GRAY;
    public static CantinaBand INSTANCE;
    @Getter
    private static long startTime;
    @Getter
    private final JDA jda;
    @Getter
    private final MusicMaster musicMaster;
    private final HashMap<Long, Guild> guilds;

    public CantinaBand() {
        INSTANCE = this;

        //runs on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        // Build bot
        JDABuilder builder = JDABuilder.createDefault(TOKEN.TOKEN);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES);
        jda = builder.build();

        // music
        musicMaster = new MusicMaster();

        // register Commands
        CommandManager cmdManager = new CommandManager(jda);
        cmdManager.registerCommands();

        // register Listeners
        jda.addEventListener(new CommandListener());
        jda.addEventListener(new ReadyListener());
        jda.addEventListener(new ButtonListener());

        // other
        guilds = new HashMap<>();
    }

    public static void main(String[] args) {
        startTime = System.currentTimeMillis();
        new CantinaBand();
    }

    public static MessageEmbed messageAsEmbed(String message, Color color) {
        return new EmbedBuilder()
                .setColor(color)
                .setTitle(message)
                .build();
    }

    public static MessageEmbed messageAsEmbed(String message) {
        return messageAsEmbed(message, EMBED_COLOR);
    }

    private void shutdown() {
        if(jda != null)
            jda.shutdown();
        System.out.println("shutdown");
    }

    public String getEmbedFooterTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        return dtf.format(now);
    }

    public String getProfilePictureUrl() {
        return jda.getSelfUser().getAvatarUrl();
    }

    public void addGuild(Guild guild) {
        guilds.put(guild.getIdLong(), guild);
    }

    public Guild getGuildById(long guildId) {
        return guilds.get(guildId);
    }
}