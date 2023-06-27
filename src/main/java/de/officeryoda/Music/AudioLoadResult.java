package de.officeryoda.Music;

import java.awt.Color;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.officeryoda.CantinaBand;
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

        int added = 0;
        for(AudioTrack track : playlist.getTracks()) {
            queue.addTrackToQueue(track, true);
            added++;
        }

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.RED)
                .setTitle("Playlist added to queue | " + playlist.getName())
                .setDescription("Added ``" + added + "`` tracks to queue, now ``" + queue.getQueueLength() + "``.")
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
        exception.printStackTrace();
    }
}
