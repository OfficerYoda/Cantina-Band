package de.officeryoda;

import de.officeryoda.Commands.Managment.CommandManager;
import de.officeryoda.Listener.ButtonListener;
import de.officeryoda.Listener.CommandListener;
import de.officeryoda.Listener.ReadyListener;
import de.officeryoda.Music.MusicMaster;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class CantinaBand {

    public static CantinaBand INSTANCE;

    private final JDA jda;
    private final MusicMaster musicMaster;
    private static long startTime;

    private final HashMap<Long, Guild> guilds;

    public CantinaBand() {
        INSTANCE = this;

        //runs on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        // Build bot
        JDABuilder builder = JDABuilder.createDefault(TOKEN.TOKEN);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES);
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

    public JDA getJda() {
        return jda;
    }

    public MusicMaster getMusicMaster() {
        return musicMaster;
    }

    public static long getStartTime() {
        return startTime;
    }

    public void addGuild(Guild guild) {
        guilds.put(guild.getIdLong(), guild);
    }

    public Guild getGuildById(long guildId) {
        return guilds.get(guildId);
    }

    public static void main(String[] args) {
        startTime = System.currentTimeMillis();
        new CantinaBand();
    }
}