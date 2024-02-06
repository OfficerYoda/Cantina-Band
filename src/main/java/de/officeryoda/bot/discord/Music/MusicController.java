package de.officeryoda.bot.discord.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.officeryoda.bot.discord.CantinaBand;
import de.officeryoda.bot.discord.Miscellaneous.ActionRows;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Getter
public class MusicController {

    private final CantinaBand cantinaBand;
    private final MusicMaster master;
    private final Guild guild;
    private final AudioPlayer player;
    private final Queue queue;
    @Setter
    private boolean looping;
    @Getter
    @Setter
    private MessageChannelUnion cmdChannel;

    public MusicController(MusicMaster master, Guild guild) {
        this.cantinaBand = CantinaBand.INSTANCE;
        this.master = master;
        this.guild = guild;
        this.player = master.getPlayerManager().createPlayer();
        this.queue = new Queue(this);
        this.looping = false;

        this.guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
        this.player.addListener(new TrackScheduler(this.master));
    }

    public int getVolume() {
        return this.player.getVolume();
    }

    public void setVolume(int volume) {
        this.player.setVolume(volume);
    }

    public void sendPlayEmbed(AudioTrack track) {
        if(player.getPlayingTrack() == null) {
            EmbedBuilder embed = getPlayEmbed(track);

            if(track.getInfo().uri.matches("https://(www\\.)?youtube\\.com/watch\\?v=.+")) {
                InputStream file = getThumbnail(track);
                if(file != null) {
                    embed.setImage("attachment://thumbnail.png");
                    cmdChannel.sendFiles(FileUpload.fromData(file, "thumbnail.png")).setEmbeds(embed.build()).addActionRow(ActionRows.playerRow(true)).queue(); // not playing yet but as soon as it joins
                }
            } else {
                cmdChannel.sendMessageEmbeds(embed.build()).addActionRow(ActionRows.playerRow(true)).queue(); // not playing yet but as soon as it joins
            }
        }
    }

    public EmbedBuilder getPlayEmbed(AudioTrack track) {
        AudioTrackInfo info = track.getInfo();
        String time = songLengthToTimeString(info.length);
        String url = info.uri;

        return new EmbedBuilder()
                .setColor(CantinaBand.EMBED_COLOR)
                .setTitle(":notes: playing: **" + info.title + "**", info.uri)
                .addField(info.author, "[" + info.title + "](" + url + ")", false)
                .addField("Length: ", info.isStream ? ":red_circle: STREAM" : time, true)
                .setFooter(cantinaBand.getEmbedFooterTime(), cantinaBand.getProfilePictureUrl());
    }

    public InputStream getThumbnail(AudioTrack track) {
        String videoID = track.getInfo().uri.replaceFirst("https://(www\\.)?youtube\\.com/watch\\?v=", "");

        InputStream file;
        try {
            file = new URL("https://img.youtube.com/vi/" + videoID + "/hqdefault.jpg").openStream();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private String songLengthToTimeString(long length) {
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
