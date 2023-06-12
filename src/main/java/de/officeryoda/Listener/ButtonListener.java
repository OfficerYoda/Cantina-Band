package de.officeryoda.Listener;

import de.officeryoda.CantinaBand;
import de.officeryoda.Commands.Executer.MusicQueue;
import de.officeryoda.Music.MusicController;
import de.officeryoda.Music.MusicMaster;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Objects;

public class ButtonListener extends ListenerAdapter {

    private final int farJumpAmount = 10;

    private final MusicMaster master;
    private MusicQueue.CmdQueue cmdQueue;

    public ButtonListener() {
        master = CantinaBand.INSTANCE.getMusicMaster();
        cmdQueue = new MusicQueue.CmdQueue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();
        assert guild != null;
        MusicController controller = master.getController(guild.getIdLong());

        MessageEmbed msgEmbed = event.getMessage().getEmbeds().get(0);
        MessageEmbed.Footer footer = msgEmbed.getFooter();
        String[] pageInfo = footer.getText().split("/");

        int crntPage, maxPage;

        if(footer.getIconUrl() == null) { // when a queue exist the footer doesn't contain a profile picture
            crntPage = Integer.parseInt(pageInfo[0].substring(5));
            maxPage = Integer.parseInt(pageInfo[1]);
        } else {
            event.reply("This button won't do anything so don't embarrass yourself and stop trying.").setEphemeral(true).queue();
            return;
        }

        int targetPage = crntPage;
        switch(event.getComponentId()) {
            case "queueFarPrevious" -> targetPage -= farJumpAmount;
            case "queuePrevious" -> targetPage -= 1;
            case "queueNext" -> targetPage += 1;
            case "queueFarNext" -> targetPage += farJumpAmount;
        }

        int clampedPage = clamp(targetPage, 1, maxPage);
        event.editMessageEmbeds(cmdQueue.getQueuePageEmbed(controller, clampedPage)).queue();


        // find a way to get this to work
        // event.editButton only edits the interacted button ("I think)
//        ActionRow actionRow = event.getMessage().getActionRows().get(0);
//        for(int i = 0; i < 4; i++) {
//            if(clampedPage == 1 && i < 2)
//                event.editButton(actionRow.getButtons().get(i).asDisabled()).queue();
//            else
//                event.editButton(actionRow.getButtons().get(i).asEnabled()).queue();
//
//            if(clampedPage == maxPage && i > 2)
//                event.editButton(actionRow.getButtons().get(i).asDisabled()).queue();
//            else
//                event.editButton(actionRow.getButtons().get(i).asEnabled()).queue();
//        }
    }

    public int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
