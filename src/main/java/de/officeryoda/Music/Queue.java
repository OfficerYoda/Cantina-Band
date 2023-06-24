package de.officeryoda.Music;

import java.awt.Color;
import java.io.File;
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

import javax.swing.*;

public class Queue {

    private CantinaBand cantinaBand;

    private List<AudioTrack> queueList;
    private MusicController controller;
    private boolean playing;
    private MessageChannelUnion cmdChannel;

    public Queue(MusicController controller) {
        this.cantinaBand = CantinaBand.INSTANCE;
        this.controller = controller;
        this.queueList = new ArrayList<>();
    }

    public boolean next() {
        if(this.queueList.size() == 0) return false;
        AudioTrack track;
        if(controller.isLooping())
            track = queueList.get(0);
        else
            track = queueList.remove(0);

        if(track == null) return false;

        if(!isPlaying())
            sendPlayEmbed(track);

        this.controller.getPlayer().playTrack(track);

        return true;
    }

    public void addTrackToQueue(AudioTrack track, boolean isPlaylist) {
        this.queueList.add(track);

        if(controller.getPlayer().getPlayingTrack() == null)
            next();
        else if(!isPlaylist)
            cmdChannel.sendMessage(":notes: Added **" + track.getInfo().title + "** to queue.").queue();
        ;
    }

    public MusicController getController() {
        return controller;
    }

    public void setController(MusicController controller) {
        this.controller = controller;
    }

    public List<AudioTrack> getQueueList() {
        return queueList;
    }

    public void setQueueList(List<AudioTrack> queueList) {
        this.queueList = queueList;
    }

    public int getLength() {
        return this.queueList.size();
    }

    public void shuffle() {
        Collections.shuffle(queueList);
    }

    private void sendPlayEmbed(AudioTrack track) {
        if(controller.getPlayer().getPlayingTrack() == null) {
            AudioTrackInfo info = track.getInfo();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.decode("#00e640"));
            embed.setTitle(":notes: playing: **" + info.title + "**", info.uri);

            String time = songLengthToTime(info.length);

            String url = info.uri;
            embed.addField(info.author, "[" + info.title + "](" + url + ")", false);
            embed.addField("Length: ", info.isStream ? ":red_circle: STREAM" : time, true);
            embed.setFooter(cantinaBand.getEmbedFooterTime(), cantinaBand.getProfilePictureUrl());

            if(url.startsWith("https://www.youtube.com/watch?v=")) {
                String videoID = url.replace("https://www.youtube.com/watch?v=", "");

                InputStream file;
                try {
                    file = new URL("https://img.youtube.com/vi/" + videoID + "/hqdefault.jpg").openStream();
                    embed.setImage("attachment://thumbnail.png");

                    cmdChannel.sendFiles(FileUpload.fromData(file, "thumbnail.png")).setEmbeds(embed.build()).addActionRow(ActionRows.PlayerRow(true)).queue(); // not playing yet but as soon as it joins
                } catch(IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                cmdChannel.sendMessageEmbeds(embed.build()).addActionRow(ActionRows.PlayerRow(true)).queue(); // not playing yet but as soon as it joins
            }
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void setCmdChannel(MessageChannelUnion cmdChannel) {
        this.cmdChannel = cmdChannel;
    }

    public MessageChannelUnion getCmdChannel() {
        return cmdChannel;
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
}
