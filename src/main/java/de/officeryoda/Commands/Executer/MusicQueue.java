package de.officeryoda.Commands.Executer;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.officeryoda.CantinaBand;
import de.officeryoda.Commands.Managment.CommandExecuter;
import de.officeryoda.Miscellaneous.ActionRows;
import de.officeryoda.Music.MusicController;
import de.officeryoda.Music.MusicMaster;
import de.officeryoda.Music.Queue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class MusicQueue {

    public static class CmdQueue implements CommandExecuter {

        private final CantinaBand cantinaBand;
        private final MusicMaster master;

        public CmdQueue() {
            this.cantinaBand = CantinaBand.INSTANCE;
            this.master = cantinaBand.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            Guild guild = event.getGuild();
            assert guild != null;
            MusicController controller = master.getController(guild.getIdLong());
            Queue queue = controller.getQueue();

            OptionMapping arg1 = event.getOption("operation");
            if(arg1 != null) {
                switch(arg1.getAsString()) {
                    case "clear" -> {
                        queue.clear();
                        event.reply("Queue cleared.").queue();
                    }
//                    case "page"-- > sendQueuePageFromArg(event, controller); // same as default
                    case "shuffle" -> new CmdShuffle().executeCommand(event);
                    default -> {
                        sendQueuePageFromArg(event, controller);
                    }
                }
            } else {
                sendQueuePageFromArg(event, controller);
            }
        }

        private void sendQueuePageFromArg(SlashCommandInteractionEvent event, MusicController controller) {
            OptionMapping pageArg = event.getOption("operation");
            int page = 0;
            if(pageArg != null) {
                page = Integer.parseInt(pageArg.getAsString());
            }
            sendQueuePage(event, controller, page);
        }

        private void sendQueuePage(SlashCommandInteractionEvent event, MusicController controller, int page) {
            List<Button> navRow = ActionRows.queueNavigationRow(page, pagesToMaxPages(controller.getQueue().getQueueLength()));
            List<Button> secRow = ActionRows.queueSecondaryRow();
            MessageEmbed embed = getQueuePageEmbed(controller, page);
            event.replyEmbeds(embed).addActionRow(navRow).addActionRow(secRow).queue();
        }

        public MessageEmbed getQueuePageEmbed(MusicController controller, int page) {

            List<AudioTrack> queue = controller.getQueue().getQueueList();
            EmbedBuilder embed = new EmbedBuilder();
            StringBuilder builder = new StringBuilder();
            embed.setFooter(cantinaBand.getEmbedFooterTime(), cantinaBand.getProfilePictureUrl());

            embed.setTitle(":notes: **Current Queue | ** " + (queue.size() == 1 ? queue.size() + " song" : queue.size() + " songs"));
            //currently playing
            embed.addField(":arrow_forward: Currently playing",
                    (controller.getPlayer().getPlayingTrack() != null) ? controller.getPlayer().getPlayingTrack().getInfo().title : "*No Song playing*", false);

            if(queue.size() > 0) {
                int queueSize = queue.size();
                int maxPage = pagesToMaxPages(queueSize);
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


        private int pagesToMaxPages(int pages) {
            return (int) Math.ceil(pages / 10f);
        }
    }

    public static class CmdShuffle implements CommandExecuter {

        private final MusicMaster master;

        public CmdShuffle() {
            master = CantinaBand.INSTANCE.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            Guild guild = event.getGuild();
            GuildVoiceState state;
            AudioChannelUnion vc;
            if(event.getMember().getVoiceState().getChannel() != guild.getAudioManager().getConnectedChannel()) {
                event.reply("You must be in our voice channel to use that!").queue();
                return;
            }

            MusicController controller = master.getController(guild.getIdLong());
            controller.getQueue().shuffle();

            event.reply("Shuffled the queue.").queue();
        }
    }

    public static class CmdSkip implements CommandExecuter {

        private final MusicMaster master;

        public CmdSkip() {
            master = CantinaBand.INSTANCE.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            System.out.println("SKIP");
            Guild guild = event.getGuild();
            GuildVoiceState state;
            AudioChannelUnion vc;
            if(event.getMember().getVoiceState().getChannel() != guild.getAudioManager().getConnectedChannel()) {
                event.reply("You must be in our voice channel to use that!").queue();
                return;
            }

            MusicController controller = master.getController(guild.getIdLong());

            if(controller.getQueue().getQueueLength() > 0)
                event.reply("Skipping the current song.").queue(msg -> {
                    controller.getQueue().next();
                    msg.editOriginal("Skipped the current song").queue();
                });
            else
                event.reply("There are no songs to skip to").queue();
        }
    }
}
