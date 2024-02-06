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
     * Represents the index of the currently playing track in the queue.
     */
    private int playPosition;

    public Queue(MusicController controller) {
        this.controller = controller;
        this.player = controller.getPlayer();
        this.trackList = new ArrayList<>();
        this.playPosition = -1;
    }

    public boolean next(boolean forceSkip) {
        if(!hasNext()) return false;

        AudioTrack track;
        if(!(controller.isLooping() && !forceSkip)) {
            playPosition++;
        }
        track = trackList.get(playPosition);

        if(track == null) return false;

        if(!controller.isLooping() || forceSkip)
            controller.sendOrUpdatePlayEmbed();

        // Can't play the same instance of a track twice: .clone to get multiple instances during queue navigation
        player.playTrack(track.makeClone());

        return true;
    }

    public boolean next() {
        return next(false);
    }

    public void previous() {
        if(!hasPrevious()) return;

        playPosition--;
        AudioTrack track = getCurrentTrack();

        if(track == null) return;

        controller.sendOrUpdatePlayEmbed();

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
        playPosition = -1;
    }

    public boolean isPlaying() {
        return !player.isPaused();
    }

    public void setPlaying(boolean playing) {
        this.player.setPaused(!playing);
    }

    public int getQueueLength() {
        return trackList.size() - (playPosition + 1);
    }

    public List<AudioTrack> getQueueList() {
        if(getQueueLength() == 0) return new ArrayList<>();
        return trackList.subList(playPosition + 1, trackList.size() - 1);
    }

    public boolean hasNext() {
        return getQueueLength() != 0;
    }

    public boolean hasPrevious() {
        return playPosition > 0;
    }

    public AudioTrack getCurrentTrack() {
        return trackList.get(playPosition);
    }
}
