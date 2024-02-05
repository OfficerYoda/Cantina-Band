package de.officeryoda.bot.discord.Commands.Executor;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.officeryoda.bot.discord.CantinaBand;
import de.officeryoda.bot.discord.Commands.Managment.CommandExecutor;
import de.officeryoda.bot.discord.Miscellaneous.ActionRows;
import de.officeryoda.bot.discord.Music.MusicController;
import de.officeryoda.bot.discord.Music.MusicMaster;
import de.officeryoda.bot.discord.Music.Queue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class MusicQueue {

    public static class CmdQueue implements CommandExecutor {

        private final CantinaBand cantinaBand;
        private final MusicMaster master;

        public CmdQueue() {
            this.cantinaBand = CantinaBand.INSTANCE;
            this.master = cantinaBand.getMusicMaster();
        }

        public static int maxQueuePages(int queueSize) {
            final double maxTracksPerPage = 10.0;
            return (int) Math.ceil(queueSize / maxTracksPerPage);
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            Guild guild = event.getGuild();
            MusicController controller = master.getController(guild.getIdLong());
            Queue queue = controller.getQueue();

            OptionMapping arg1 = event.getOption("operation");
            if(arg1 != null) {
                switch(arg1.getAsString()) {
                    case "clear" -> {
                        queue.clear();
                        event.reply("Queue cleared.").queue();
                    }
                    case "shuffle" -> new CmdShuffle().executeCommand(event);
                    default -> sendQueuePageFromArg(event, controller);
                }
            } else {
                sendQueuePageFromArg(event, controller);
            }
        }

        private void sendQueuePageFromArg(SlashCommandInteractionEvent event, MusicController controller) {
            OptionMapping pageArg = event.getOption("operation");
            int page = pageArg != null ? Integer.parseInt(pageArg.getAsString()) : 0;

            sendQueuePage(event, controller, page);
        }

        public void sendQueuePage(SlashCommandInteractionEvent event, MusicController controller, int page) {
            List<Button> navRow = ActionRows.queueNavigationRow();
            List<Button> secRow = ActionRows.queueSecondaryRow();
            MessageEmbed embed = getQueuePageEmbed(controller, page);
            event.replyEmbeds(embed).addActionRow(navRow).addActionRow(secRow).queue();
        }

        public MessageEmbed getQueuePageEmbed(MusicController controller, int page) {

            List<AudioTrack> queue = controller.getQueue().getQueueList();
            StringBuilder builder = new StringBuilder();
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(CantinaBand.EMBED_COLOR)
                    .setFooter(cantinaBand.getEmbedFooterTime(), cantinaBand.getProfilePictureUrl())
                    .setTitle(":notes: **Current Queue | ** " + (queue.size() == 1 ? queue.size() + " song" : queue.size() + " songs"))
                    .addField(":arrow_forward: Currently playing",
                            (controller.getPlayer().getPlayingTrack() != null) ? controller.getPlayer().getPlayingTrack().getInfo().title : "*No Song playing*", false);

            if(!queue.isEmpty()) {
                int queueSize = queue.size();
                int maxPage = maxQueuePages(queueSize);
                if(page <= 0) page = 1;
                if(page > queueSize) page = maxPage;

                //get the part of the queue needed
                int startValue = (page - 1) * 10;
                int endValue = startValue + 10;
                //get start value right
                if(startValue > queueSize)
                    startValue = queueSize - 10;
                if(startValue < 0)
                    startValue = 0;
                //get end value right
                if(endValue > queueSize)
                    endValue = queueSize;

                //build the queue
                for(int i = startValue; i < endValue; i++) {
                    builder.append("**[").append(i + 1).append("]** ").append(queue.get(i).getInfo().title).append("\r\n");
                }
                embed.setFooter("page " + page + "/" + maxPage);
            } else {
                builder.append("*Queue is empty*");
            }

            embed.addField("**QUEUE**", builder.toString(), false);

            return embed.build();
        }
    }

    public static class CmdShuffle implements CommandExecutor {

        private final MusicMaster master;

        public CmdShuffle() {
            master = CantinaBand.INSTANCE.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            if(MusicMisc.differentVoiceChannel(event)) return;

            MusicController controller = master.getController(event.getGuild().getIdLong());
            controller.getQueue().shuffle();

            event.reply("Shuffled the queue.").queue();
        }
    }

    public static class CmdSkip implements CommandExecutor {

        private final MusicMaster master;

        public CmdSkip() {
            master = CantinaBand.INSTANCE.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            if(MusicMisc.differentVoiceChannel(event)) return;

            MusicController controller = master.getController(event.getGuild().getIdLong());

            if (controller.getQueue().getQueueLength() > 0) {
                event.reply("Skipping the current song.").queue(msg -> {
                    controller.getQueue().next(true);
                    msg.editOriginal("Skipped the current song.").queue();
                });
            } else {
                event.reply("There are no songs to skip to.").queue();
            }
        }
    }
}
