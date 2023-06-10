package de.officeryoda;

import de.officeryoda.Commands.Managment.CommandManager;
import de.officeryoda.Listener.CommandListener;
import de.officeryoda.Listener.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class CantinaBand extends ListenerAdapter {

    public static long startTime;
    private JDA jda;

    public CantinaBand() {
        //runs on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            }
        });

        // Build bot
        JDABuilder builder = JDABuilder.createDefault(TOKEN.TOKEN, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS);
        jda = builder.build();

        // register Commands
        CommandManager cmdManager = new CommandManager(jda);
        cmdManager.registerCommands();

        // register Listeners
        jda.addEventListener(new CommandListener());
        jda.addEventListener(new ReadyListener());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equalsIgnoreCase("!ping")) {
            event.getChannel().sendMessage("Pong!").queue();
        }
    }


    public static void main(String[] args) {
        startTime = System.currentTimeMillis();
        new CantinaBand();
    }

    private void shutdown() {
        jda.shutdown();
        System.out.println("shoutdown");
    }
}