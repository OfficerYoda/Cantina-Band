package de.officeryoda.Commands.Managment;

import de.officeryoda.Commands.Executer.CmdPing;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

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
        List<Command> commands = new ArrayList<>();
        commands.add(new Command("ping", "Pings the Bot", new CmdPing()));

        for(Command cmd : commands) {
            jda.upsertCommand(cmd.command, cmd.description).setGuildOnly(true).queue();
            commandExecuter.put(cmd.command, cmd.commandExecuter);
        }
    }

    public void executeCommand(String command, SlashCommandInteractionEvent event) {
        commandExecuter.getOrDefault(command, e -> e.reply("This Command has not been implemented yet.").setEphemeral(true).queue()).executeCommand(event);
    }

    private record Command(String command, String description, CommandExecuter commandExecuter) {}
}
