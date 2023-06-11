package de.officeryoda.Commands.Managment;

import de.officeryoda.Commands.Executer.CmdMusic;
import de.officeryoda.Commands.Executer.CmdPing;
import de.officeryoda.Commands.Executer.CmdPlay;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {

    public static CommandManager INSTANCE;

    private JDA jda;

    private Map<String, CommandExecuter> commandExecuter;

    public CommandManager(JDA jda) {
        if(INSTANCE == null)
            INSTANCE = this;
        else
            return;

        this.jda = jda;
        commandExecuter = new HashMap<>();
    }

    public void registerCommands() {
        OptionData play_urlArg = new OptionData(OptionType.STRING, "url", "The URL of the song", true);

        List<Command> commands = new ArrayList<>();
        commands.add(new Command(Commands.slash("ping", "Pings the Bot").setGuildOnly(true), new CmdPing()));
        commands.add(new Command(Commands.slash("music", "Music test").setGuildOnly(true), new CmdMusic()));
        commands.add(new Command(Commands.slash("play", "Plays a song from Youtube/SoundCloud(Comming soon)").addOptions(play_urlArg),new CmdPlay()));

        for(Command cmd : commands) {
            jda.upsertCommand(cmd.cmdData).queue();
            commandExecuter.put(cmd.cmdData.getName(), cmd.commandExecuter);
        }
    }

    public void executeCommand(String command, SlashCommandInteractionEvent event) {
        commandExecuter.getOrDefault(command, e -> e.reply("This Command has not been implemented yet.").setEphemeral(true).queue()).executeCommand(event);
    }

    private record Command(CommandData cmdData, CommandExecuter commandExecuter) {}
}
