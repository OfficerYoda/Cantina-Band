package de.officeryoda.Commands.Executer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import de.officeryoda.CantinaBand;
import de.officeryoda.Commands.Managment.CommandExecuter;
import de.officeryoda.Music.AudioLoadResult;
import de.officeryoda.Music.MusicController;
import de.officeryoda.Music.MusicMaster;
import de.officeryoda.Music.Queue;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.File;

public class MusicBasic {

    public static class CmdPlay implements CommandExecuter {

        private final MusicMaster master;

        public CmdPlay() {
            this.master = CantinaBand.INSTANCE.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            Guild guild = event.getGuild();
            GuildVoiceState state;
            AudioChannelUnion vc;

            assert event.getMember() != null;
            if((state = event.getMember().getVoiceState()) == null || (vc = state.getChannel()) == null) {
                event.reply("You must be in a voice channel to use that!").setEphemeral(true).queue();
                return;
            }

            assert guild != null;
            MusicController controller = master.getController(guild.getIdLong());
            Queue queue = controller.getQueue();
            AudioPlayerManager playerManager = master.getPlayerManager();
            AudioManager manager = guild.getAudioManager();

            if(queue.getQueueLength() != 0) {
                assert guild.getSelfMember().getVoiceState() != null;
                if(guild.getSelfMember().getVoiceState().getChannel() != event.getMember().getVoiceState().getChannel()) { // check if bot channel is same as sender channel
                    event.reply("I'm not in your voice channel").setEphemeral(true).queue();
                    return;
                }
            }

            // failure is always a Throwable
            event.reply("Loading").queue(
                    (result) -> result.deleteOriginal().queue(),
                    (failure) -> failure.printStackTrace());

            queue.setCmdChannel(event.getChannel());
            manager.openAudioConnection(vc);

            // get url arg
            OptionMapping messageOption = event.getOption("song-name");
            assert messageOption != null;
            String url = messageOption.getAsString();

            if(!url.startsWith("http")) {
                url = "ytsearch: " + url;
            }

            playerManager.loadItem(url, new AudioLoadResult(controller, url));
        }
    }

    public static class CmdStop implements CommandExecuter {

        private final MusicMaster master;

        public CmdStop() {
            this.master = CantinaBand.INSTANCE.getMusicMaster();
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
            AudioPlayer player = controller.getPlayer();

            event.reply("Trying to stop playing.").queue(msg -> {
                if(player.getPlayingTrack() == null) {
                    msg.editOriginal("Nothing is playing.").queue();
                    return;
                }

                player.stopTrack();
                guild.getAudioManager().closeAudioConnection();
                controller.getQueue().clear();
                msg.editOriginal("Stopped playing and cleared the queue.").queue();
            });
        }
    }
}
