package de.officeryoda.Commands.Executer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.officeryoda.CantinaBand;
import de.officeryoda.Commands.Managment.CommandExecuter;
import de.officeryoda.Music.AudioPlayerSendHandler;
import de.officeryoda.Music.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class CmdMusic implements CommandExecuter {
    @Override // from https://github.com/sedmelluq/LavaPlayer#lavaplayer---audio-player-library-for-discord
    public void executeCommand(SlashCommandInteractionEvent event) {
//        Guild guild = event.getGuild();
//        // This will get the first voice channel with the name "music"
//        // matching by voiceChannel.getName().equalsIgnoreCase("music")
//        VoiceChannel channel = guild.getVoiceChannelsByName("music", true).get(0);
//        AudioManager manager = guild.getAudioManager();
//
//        // MySendHandler should be your AudioSendHandler implementation
//        manager.setSendingHandler(new AudioPlayerSendHandler(player));
//        // Here we finally connect to the target voice channel
//        // and it will automatically start pulling the audio from the MySendHandler instance
//        manager.openAudioConnection(channel);
    }
}
