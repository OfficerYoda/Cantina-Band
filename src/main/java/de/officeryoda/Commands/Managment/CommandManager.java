package de.officeryoda.Commands.Managment;

import de.officeryoda.Commands.Executer.CmdMusic;
import de.officeryoda.Commands.Executer.CmdPing;
import de.officeryoda.Commands.Executer.CmdPlay;
import de.officeryoda.Commands.Executer.CmdVolume;
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
        OptionData volume_volumeArg = new OptionData(OptionType.INTEGER, "volume", "The volume you want to set the bot to");

        List<Command> commands = new ArrayList<>();
        commands.add(new Command(Commands.slash("ping", "Pings the Bot"), new CmdPing()));
        commands.add(new Command(Commands.slash("music", "Music test"), new CmdMusic()));
        commands.add(new Command(Commands.slash("play", "Plays a song from Youtube/SoundCloud(Coming soon)").addOptions(play_urlArg), new CmdPlay()));
        commands.add(new Command(Commands.slash("volume", "Sets the Volume  of the bot").addOptions(volume_volumeArg), new CmdVolume()));

        for(Command cmd : commands) {
            jda.upsertCommand(cmd.cmdData.setGuildOnly(true)).queue();
            commandExecuter.put(cmd.cmdData.getName(), cmd.commandExecuter);
        }
    }

    public void executeCommand(String command, SlashCommandInteractionEvent event) {
        commandExecuter.getOrDefault(command, e -> e.reply("This Command has not been implemented yet.").setEphemeral(true).queue()).executeCommand(event);
    }

    private record Command(CommandData cmdData, CommandExecuter commandExecuter) {
    }
}
