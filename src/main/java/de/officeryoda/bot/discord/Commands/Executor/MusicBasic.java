package de.officeryoda.bot.discord.Commands.Executor;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import de.officeryoda.bot.discord.CantinaBand;
import de.officeryoda.bot.discord.Commands.Managment.CommandExecutor;
import de.officeryoda.bot.discord.Music.AudioLoadResult;
import de.officeryoda.bot.discord.Music.MusicController;
import de.officeryoda.bot.discord.Music.MusicMaster;
import de.officeryoda.bot.discord.Music.Queue;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicBasic {

    public static class CmdPlay implements CommandExecutor {

        private final MusicMaster master;

        public CmdPlay() {
            this.master = CantinaBand.INSTANCE.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            Guild guild = event.getGuild();
            GuildVoiceState state;
            AudioChannelUnion vc;

            if((state = event.getMember().getVoiceState()) == null || (vc = state.getChannel()) == null) {
                event.reply("You must be in a voice channel to use that!").setEphemeral(true).queue();
                return;
            }

            MusicController controller = master.getController(guild.getIdLong());
            Queue queue = controller.getQueue();
            AudioPlayerManager playerManager = master.getPlayerManager();
            AudioManager manager = guild.getAudioManager();

            if(queue.getQueueLength() != 0 && MusicMisc.differentVoiceChannel(event)) return;

            // failure is always a Throwable
            event.reply("Loading").queue(
                    (result) -> result.deleteOriginal().queue(),
                    (failure) -> failure.printStackTrace());

            controller.setCmdChannel(event.getChannel());
            manager.openAudioConnection(vc);

            // get url arg
            OptionMapping messageOption = event.getOption("song-name");
            String url = messageOption.getAsString();

            if(!url.startsWith("http")) {
                url = "ytsearch: " + url;
            }

            playerManager.loadItem(url, new AudioLoadResult(controller, url));
        }
    }

    public static class CmdStop implements CommandExecutor {

        private final MusicMaster master;

        public CmdStop() {
            this.master = CantinaBand.INSTANCE.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            if(MusicMisc.differentVoiceChannel(event)) return;

            Guild guild = event.getGuild();
            MusicController controller = master.getController(guild.getIdLong());
            AudioPlayer player = controller.getPlayer();

            controller.setCmdChannel(event.getChannel());

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
