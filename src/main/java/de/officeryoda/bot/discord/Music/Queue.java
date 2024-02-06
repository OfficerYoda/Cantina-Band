package de.officeryoda.bot.discord.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.officeryoda.bot.discord.CantinaBand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Queue {

    private final MusicController controller;
    private final AudioPlayer player;
    private List<AudioTrack> trackList;
    /**
     * Represents the position of the next track in the queue to be played after the current track is finished.
     */
    private int queuePosition;
    private AudioTrack lastLoopingTrack;

    public Queue(MusicController controller) {
        this.controller = controller;
        this.player = controller.getPlayer();
        this.trackList = new ArrayList<>();
    }

    public boolean next(boolean forceSkip) {
        if(hasNext()) return false;

        AudioTrack track;
        if(controller.isLooping() && !forceSkip) {
            track = trackList.get(Math.max(queuePosition - 1, 0));
            lastLoopingTrack = track;
        } else {
            track = trackList.get(queuePosition);
            queuePosition++;
            if(track == lastLoopingTrack)
                return next();
        }

        if(track == null) return false;

        if(!controller.isLooping() || forceSkip)
            controller.sendPlayEmbed(track);

        // Can't play the same instance of a track twice: .clone to get multiple instances during queue navigation
        player.playTrack(track.makeClone());

        return true;
    }

    public boolean next() {
        return next(false);
    }

    public void previous() {
        if(!hasPrevious()) return;

        queuePosition--;
        AudioTrack track = trackList.get(queuePosition - 1); // queue position is where the index of the next song

        if(track == null) return;

        controller.sendPlayEmbed(track);

        player.playTrack(track.makeClone());
    }

    public void addTrackToQueue(AudioTrack track, boolean isPlaylist) {
        this.trackList.add(track);

        if(player.getPlayingTrack() == null) {
            next();
        } else if(!isPlaylist) {
            controller.getCmdChannel().sendMessageEmbeds(CantinaBand.messageAsEmbed(":notes: Added **" + track.getInfo().title + "** to queue.")).queue();
        }
    }

    public void shuffle() {
        Collections.shuffle(trackList);
    }

    public void clear() {
        trackList = new ArrayList<>();
        queuePosition = 0;
    }

    public boolean isPlaying() {
        return !player.isPaused();
    }

    public void setPlaying(boolean playing) {
        this.player.setPaused(!playing);
    }

    public int getQueueLength() {
        return trackList.size() - queuePosition;
    }

    public List<AudioTrack> getQueueList() {
        if(getQueueLength() == 0) return new ArrayList<>();
        return trackList.subList(queuePosition, trackList.size() - 1);
    }

    public boolean hasNext() {
        return getQueueLength() == 0;
    }

    public boolean hasPrevious() {
        return queuePosition > 1;
    }

    public AudioTrack getCurrentTrack() {
        return trackList.get(queuePosition - 1);
    }
}
