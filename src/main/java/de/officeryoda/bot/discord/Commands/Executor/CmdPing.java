package de.officeryoda.bot.discord.Commands.Executor;

import de.officeryoda.bot.discord.Commands.Managment.CommandExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CmdPing implements CommandExecutor {

    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        event.reply("Pong!").setEphemeral(true).queue();
    }
}
