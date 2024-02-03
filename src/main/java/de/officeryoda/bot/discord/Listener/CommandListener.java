package de.officeryoda.bot.discord.Listener;

import de.officeryoda.bot.discord.Commands.Managment.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {

    private final CommandManager cmdManager;

    public CommandListener() {
        cmdManager = CommandManager.INSTANCE;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        cmdManager.executeCommand(event.getName(), event);
    }
}
