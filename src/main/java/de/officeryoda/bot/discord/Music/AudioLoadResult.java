package de.officeryoda.bot.discord.Music;

import java.awt.Color;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.officeryoda.bot.discord.CantinaBand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class AudioLoadResult implements AudioLoadResultHandler {

    private final CantinaBand cantinaBand;
    private final MusicMaster master;
    private final MusicController controller;
    private final String sUrl;

    public AudioLoadResult(MusicController controller, String sUrl) {
        this.cantinaBand = CantinaBand.INSTANCE;
        this.master = controller.getMaster();
        this.controller = controller;
        this.sUrl = sUrl;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        Queue queue = controller.getQueue();
        queue.addTrackToQueue(track, false);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        Queue queue = controller.getQueue();
        if(sUrl.startsWith("ytsearch: ")) {
            queue.addTrackToQueue(playlist.getTracks().get(0), false);
            return;
        }

        playlist.getTracks().forEach(track -> queue.addTrackToQueue(track, true));

        EmbedBuilder embed = new EmbedBuilder().setColor(CantinaBand.EMBED_COLOR)
                .setTitle("Playlist `" + playlist.getName() + "` added to queue")
                .setDescription("Added ``" + playlist.getTracks().size() + "`` tracks to queue; now ``" + queue.getQueueLength() + "``.")
                .setFooter(cantinaBand.getEmbedFooterTime(), cantinaBand.getProfilePictureUrl());

        MessageChannelUnion channel = controller.getQueue().getCmdChannel();
        channel.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void noMatches() {
        controller.getQueue().getCmdChannel().sendMessage("No Video or Song found.").queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        controller.getQueue().getCmdChannel().sendMessage(
                "Loading has failed." +
                "\nSeverity: `" + exception.severity + "`.").queue();
        exception.printStackTrace();
    }
}
