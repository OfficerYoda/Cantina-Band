package de.officeryoda.Listener;

import de.officeryoda.CantinaBand;
import de.officeryoda.Commands.Executer.MusicQueue;
import de.officeryoda.Music.MusicController;
import de.officeryoda.Music.MusicMaster;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        Guild guild = event.getGuild();
        assert guild != null;
        MusicController controller = master.getController(guild.getIdLong());

        MessageEmbed msgEmbed = event.getMessage().getEmbeds().get(0);
        MessageEmbed.Footer footer = msgEmbed.getFooter();
        String[] pageInfo = footer.getText().split("/");

        int crntPage, maxPage;

        if(footer.getIconUrl() == null) { // when a queue exist the footer doesn't contain a profile picture
            crntPage = Integer.parseInt(pageInfo[0].substring(5));
            maxPage = (int) Math.ceil(controller.getQueue().getLength() / 10f);
            ;
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
        }

        int clampedPage = clamp(targetPage, 1, maxPage);
        event.editMessageEmbeds(cmdQueue.getQueuePageEmbed(controller, clampedPage)).queue();

        Button b1 = Button.primary("queueFarPrevious", Emoji.fromUnicode("⏪"));
        Button b2 = Button.primary("queuePrevious", Emoji.fromUnicode("◀️"));
        Button b3 = Button.primary("queueNext", Emoji.fromUnicode("▶️"));
        Button b4 = Button.primary("queueFarNext", Emoji.fromUnicode("⏩"));

        if(clampedPage == 1) {
            b1 = b1.asDisabled();
            b2 = b2.asDisabled();
        }
        if(clampedPage == maxPage) {
            b3 = b3.asDisabled();
            b4 = b4.asDisabled();
        }

        ActionRow actionRow = ActionRow.of(b1, b2, b3, b4);
        event.getMessage().editMessageComponents(actionRow).queue();
    }

    public int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
