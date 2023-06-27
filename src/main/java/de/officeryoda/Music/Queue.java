package de.officeryoda.Music;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.officeryoda.CantinaBand;
import de.officeryoda.Miscellaneous.ActionRows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;

public class Queue {

    private final CantinaBand cantinaBand;

    private List<AudioTrack> trackList;
    private int queuePosition = 0;
    private AudioTrack lastLoopingTrack;

    private final MusicController controller;
    private boolean playing;
    private MessageChannelUnion cmdChannel;

    public Queue(MusicController controller) {
        this.cantinaBand = CantinaBand.INSTANCE;
        this.controller = controller;
        this.trackList = new ArrayList<>();
    }

    public boolean next() {
        if(hasNext()) return false;

        AudioTrack track;
        if(controller.isLooping()) {
            track = trackList.get(Math.max(queuePosition - 1, 0)); // to loop currently playing song (queue Position is where the queue starts)
            lastLoopingTrack = track;
        } else {
            track = trackList.get(queuePosition++);
            if(track == lastLoopingTrack)
                return next();
        }

        if(track == null) return false;

        if(!isPlaying())
            sendPlayEmbed(track);

        this.controller.getPlayer().playTrack(track);

        return true;
    }

    public void previous() {
        if(!hasPrevious()) return;

        queuePosition--; // queue position is where the queue starts
        AudioTrack track = trackList.get(queuePosition - 1);
        ;

        if(track == null) return;

        if(!isPlaying())
            sendPlayEmbed(track);

        this.controller.getPlayer().playTrack(track);
    }

    public void addTrackToQueue(AudioTrack track, boolean isPlaylist) {
        this.trackList.add(track);

        if(controller.getPlayer().getPlayingTrack() == null)
            next();
        else if(!isPlaylist)
            cmdChannel.sendMessage(":notes: Added **" + track.getInfo().title + "** to queue.").queue();
        ;
    }

    public void shuffle() {
        Collections.shuffle(trackList);
    }

    public void clear() {
        trackList = new ArrayList<>();
        queuePosition = 0;
    }

    private String songLengthToTime(long length) {
        String time = "";

        long seconds = length / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;
        hours %= 60;

        //Hours
        if(hours > 0)
            time += hours + ":";
        //Minutes
        if(minutes < 10 && hours > 0)
            time += "0" + minutes + ":";
        else
            time += minutes + ":";
        //Seconds
        if(seconds < 10)
            time += "0" + seconds;
        else
            time += seconds + "";

        return time;
    }

    public List<AudioTrack> getQueueList() {
        return trackList.subList(queuePosition, trackList.size() - 1);
    }

    public int getQueueLength() {
        return trackList.size() - queuePosition;
    }

    private void sendPlayEmbed(AudioTrack track) {
        if(controller.getPlayer().getPlayingTrack() == null) {
            AudioTrackInfo info = track.getInfo();
            String url = info.uri;
            EmbedBuilder embed = getPlayEmbed(track);

            if(url.startsWith("https://www.youtube.com/watch?v=")) {
                InputStream file = getThumbnail(track);
                if(file != null) {
                    embed.setImage("attachment://thumbnail.png");
                    cmdChannel.sendFiles(FileUpload.fromData(file, "thumbnail.png")).setEmbeds(embed.build()).addActionRow(ActionRows.playerRow(true)).queue(); // not playing yet but as soon as it joins
                }
            }
            cmdChannel.sendMessageEmbeds(embed.build()).addActionRow(ActionRows.playerRow(true)).queue(); // not playing yet but as soon as it joins
        }
    }

    public EmbedBuilder getPlayEmbed(AudioTrack track) {
        AudioTrackInfo info = track.getInfo();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.decode("#00e640"));
        embed.setTitle(":notes: playing: **" + info.title + "**", info.uri);

        String time = songLengthToTime(info.length);

        String url = info.uri;
        embed.addField(info.author, "[" + info.title + "](" + url + ")", false);
        embed.addField("Length: ", info.isStream ? ":red_circle: STREAM" : time, true);
        embed.setFooter(cantinaBand.getEmbedFooterTime(), cantinaBand.getProfilePictureUrl());

        return embed;
    }

    public InputStream getThumbnail(AudioTrack track) {
        String videoID = track.getInfo().uri.replace("https://www.youtube.com/watch?v=", "");

        InputStream file;
        try {
            file = new URL("https://img.youtube.com/vi/" + videoID + "/hqdefault.jpg").openStream();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean hasNext() {
        return getQueueLength() == 0;
    }

    public boolean hasPrevious() {
        return queuePosition > 0;
    }

    public void setPlaying(boolean playing) {
        this.controller.getPlayer().setPaused(!playing);
        this.playing = playing;
    }

    public AudioTrack getCurrentTrack() {
        return trackList.get(queuePosition);
    }

    public void setCmdChannel(MessageChannelUnion cmdChannel) {
        this.cmdChannel = cmdChannel;
    }

    public MessageChannelUnion getCmdChannel() {
        return cmdChannel;
    }
}
