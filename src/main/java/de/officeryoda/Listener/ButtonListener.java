package de.officeryoda.Listener;

import de.officeryoda.CantinaBand;
import de.officeryoda.Commands.Executer.MusicQueue;
import de.officeryoda.Miscellaneous.ActionRows;
import de.officeryoda.Music.MusicController;
import de.officeryoda.Music.MusicMaster;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class ButtonListener extends ListenerAdapter {

    private final int FAR_JUMP_AMOUNT = 10;

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
        assert guild != null;
        MusicController controller = master.getController(guild.getIdLong());

        MessageEmbed msgEmbed = event.getMessage().getEmbeds().get(0);
        MessageEmbed.Footer footer = msgEmbed.getFooter();
        assert footer != null;
        String[] pageInfo = footer.getText().split("/");

        int crntPage, maxPage;

        if(footer.getIconUrl() == null) { // when a queue exist the footer doesn't contain a profile picture
            crntPage = Integer.parseInt(pageInfo[0].substring(5));
            maxPage = (int) Math.ceil(controller.getQueue().getLength() / 10f);
        } else {
            event.reply("This button won't do anything so don't embarrass yourself and stop trying.").setEphemeral(true).queue();
            return;
        }

        int targetPage = crntPage;
        switch(event.getComponentId()) {
            case "queueFarPrevious" -> targetPage -= FAR_JUMP_AMOUNT;
            case "queuePrevious" -> targetPage -= 1;
            case "queueNext" -> targetPage += 1;
            case "queueFarNext" -> targetPage += FAR_JUMP_AMOUNT;
            case "queueShuffle" -> controller.getQueue().shuffle();
        }

        int clampedPage = clamp(targetPage, 1, maxPage);
        event.editMessageEmbeds(cmdQueue.getQueuePageEmbed(controller, clampedPage)).queue();

        ActionRow actionRow = ActionRow.of(ActionRows.QueueRow(clampedPage, maxPage));
        event.getMessage().editMessageComponents(actionRow).queue();
    }

    private void onPlayerButton(ButtonInteractionEvent event) {

    }

    public int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
