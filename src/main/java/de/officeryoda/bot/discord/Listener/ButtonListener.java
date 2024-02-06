package de.officeryoda.bot.discord.Listener;

import de.officeryoda.bot.discord.CantinaBand;
import de.officeryoda.bot.discord.Commands.Executor.MusicQueue;
import de.officeryoda.bot.discord.Miscellaneous.ActionRows;
import de.officeryoda.bot.discord.Music.MusicController;
import de.officeryoda.bot.discord.Music.MusicMaster;
import de.officeryoda.bot.discord.Music.Queue;
import de.officeryoda.bot.discord.exceptions.ShouldNotGetHereException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class ButtonListener extends ListenerAdapter {

    private final static int QUEUE_FAR_JUMP_AMOUNT = 10;
    private final MusicMaster master;
    private final MusicQueue.CmdQueue cmdQueue;

    public ButtonListener() {
        master = CantinaBand.INSTANCE.getMusicMaster();
        cmdQueue = new MusicQueue.CmdQueue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(event.getComponentId().startsWith("queue")) onQueueButton(event);
        else if(event.getComponentId().startsWith("player")) onPlayerButton(event);
    }

    private void onQueueButton(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();
        MusicController controller = master.getController(guild.getIdLong());

        MessageEmbed msgEmbed = event.getMessage().getEmbeds().get(0);
        MessageEmbed.Footer footer = msgEmbed.getFooter();

        int crntPage = 1;

        if(footer.getIconUrl() == null) {
            // When a queue exists, the footer doesn't contain a profile picture
            crntPage = Character.getNumericValue(footer.getText().charAt(5)); // Extract page number ('2') from ("page 2/3")
        } else if(controller.getQueue().getQueueLength() == 0) {
            event.reply("This button won't do anything, so don't embarrass yourself and stop trying.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        int maxPage = MusicQueue.CmdQueue.maxQueuePages(controller.getQueue().getQueueLength());

        if(crntPage < 0)
            throw new ShouldNotGetHereException();

        int targetPage = crntPage;
        switch(event.getComponentId()) {
            case "queueFarPrevious" -> targetPage -= QUEUE_FAR_JUMP_AMOUNT;
            case "queuePrevious" -> targetPage -= 1;
            case "queueNext" -> targetPage += 1;
            case "queueFarNext" -> targetPage += QUEUE_FAR_JUMP_AMOUNT;
            case "queueShuffle" -> controller.getQueue().shuffle();
        }


        int clampedPage = clamp(targetPage, 1, maxPage);
        event.editMessageEmbeds(cmdQueue.getQueuePageEmbed(controller, clampedPage)).queue();

        ActionRow navRow = ActionRow.of(ActionRows.queueNavigationRow());
        ActionRow secRow = ActionRow.of(ActionRows.queueSecondaryRow());
        event.getMessage().editMessageComponents(navRow, secRow).queue();
    }

    private void onPlayerButton(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();
        MusicController controller = master.getController(guild.getIdLong());
        Queue queue = controller.getQueue();

        switch(event.getComponentId()) {
            case "playerPreviousTrack" -> queue.previous();
            case "playerTogglePlay" -> {
                queue.setPlaying(!queue.isPlaying());
                controller.sendOrUpdatePlayEmbed();
            }
            case "playerNextTrack" -> queue.next(true);
        }

        // sending or editing a message will be handled in 'sendOrUpdatePlayEmbed()'
        event.deferEdit().queue();
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
