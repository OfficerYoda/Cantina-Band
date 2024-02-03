package de.officeryoda.bot.discord.Commands.Managment;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface CommandExecutor {

    void executeCommand(SlashCommandInteractionEvent event);

}
