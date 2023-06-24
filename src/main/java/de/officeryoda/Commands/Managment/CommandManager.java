package de.officeryoda.Commands.Managment;

import de.officeryoda.Commands.Executer.MusicBasic;
import de.officeryoda.Commands.Executer.MusicMisc;
import de.officeryoda.Commands.Executer.CmdPing;
import de.officeryoda.Commands.Executer.MusicQueue;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
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
        OptionData urlArg = new OptionData(OptionType.STRING, "url", "The URL of the song", true);
        OptionData volumeArg = new OptionData(OptionType.INTEGER, "volume", "The volume you want to set the bot to");
        OptionData looping = new OptionData(OptionType.BOOLEAN, "looping", "The state of looping");
        OptionData queue = new OptionData(OptionType.STRING, "operation", "Queue operation arg").addChoice("shuffle", "shuffle").addChoice("clear", "clear");

        List<Command> commands = new ArrayList<>();
        commands.add(new Command(Commands.slash("ping", "Pings the Bot"), new CmdPing()));
        commands.add(new Command(Commands.slash("play", "Plays a song from Youtube/SoundCloud(Coming soon)").addOptions(urlArg), new MusicBasic.CmdPlay()));
        commands.add(new Command(Commands.slash("stop", "Stops the bot and clears the queue"), new MusicBasic.CmdStop()));
        commands.add(new Command(Commands.slash("volume", "Sets the Volume of the bot").addOptions(volumeArg), new MusicMisc.CmdVolume()));
        commands.add(new Command(Commands.slash("pause", "Pauses the bot"), new MusicMisc.CmdPause()));
        commands.add(new Command(Commands.slash("resume", "Resumes the bot"), new MusicMisc.CmdResume()));
        commands.add(new Command(Commands.slash("continue", "Resumes the bot"), new MusicMisc.CmdResume()));
        commands.add(new Command(Commands.slash("loop", "Loops the currently playing bot").addOptions(looping), new MusicMisc.CmdLoop()));
        commands.add(new Command(Commands.slash("toggleloop", "Toggles the loop state"), new MusicMisc.CmdToggleLoop()));
        commands.add(new Command(Commands.slash("queue", "Shows the queue or lets you clear/shuffle it").addOptions(queue), new MusicQueue.CmdQueue()));
        commands.add(new Command(Commands.slash("shuffle", "Shuffles the queue"), new MusicQueue.CmdShuffle()));
        commands.add(new Command(Commands.slash("skip", "Skips the current song"), new MusicQueue.CmdSkip()));

        for(Command cmd : commands) {
            jda.upsertCommand(cmd.cmdData.setGuildOnly(true)).queue();
            commandExecuter.put(cmd.cmdData.getName(), cmd.commandExecuter);
        }
    }

    public void executeCommand(String command, SlashCommandInteractionEvent event) {
        commandExecuter.getOrDefault(command, e -> e.reply("This Command has not been implemented yet.").setEphemeral(true).queue()).executeCommand(event);
    }

    private record Command(CommandData cmdData, CommandExecuter commandExecuter) {}
}
