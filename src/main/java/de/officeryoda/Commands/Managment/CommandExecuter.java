package de.officeryoda.Commands.Managment;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface CommandExecuter {

    public void executeCommand(SlashCommandInteractionEvent event);

}
