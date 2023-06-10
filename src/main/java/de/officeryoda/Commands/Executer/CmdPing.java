package de.officeryoda.Commands.Executer;

import de.officeryoda.Commands.Managment.CommandExecuter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CmdPing implements CommandExecuter {

    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        event.reply("Pong!").setEphemeral(true).queue();
    }
}
