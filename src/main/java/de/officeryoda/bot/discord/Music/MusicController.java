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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
public class MusicController {

    private final CantinaBand cantinaBand;
    private final MusicMaster master;
    private final Guild guild;
    private final AudioPlayer player;
    private final Queue queue;
    /**
     * Number of recent messages to consider when checking for the presence of the interacted message.
     * If not found within the last 'recentMessageThreshold', a new message is sent.
     */
    private final int recentMessageThreshold = 5;
    @Setter
    private boolean looping;
    @Getter
    @Setter
    private MessageChannelUnion cmdChannel;
    private Message playEmbedMsg;

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

    public void sendOrUpdatePlayEmbed() {
        AudioTrack track = queue.getCurrentTrack();
        EmbedBuilder embed = getPlayEmbed(track);

        if(playEmbedMsg != null && !newPlayEmbedNeeded()) {
            // edit existing message
            InputStream file = getThumbnail(track);
            if(file != null) {
                // with thumbnail
                embed.setImage("attachment://thumbnail.png");
                playEmbedMsg.editMessageAttachments(FileUpload.fromData(file, "thumbnail.png"))
                        .setEmbeds(embed.build())
                        .setActionRow(ActionRows.playerRow(queue.isPlaying()))
                        .queue();
            } else {
                // without thumbnail
                playEmbedMsg.editMessageEmbeds(embed.build())
                        .setActionRow(ActionRows.playerRow(queue.isPlaying()))
                        .queue();
            }
        } else {
            // send new play embed
            sendNewPlayEmbed(track);
        }
    }

    private void sendNewPlayEmbed(AudioTrack track) {
        EmbedBuilder embed = getPlayEmbed(track);

        InputStream file = getThumbnail(track);
        if(file != null) {
            // with thumbnail
            embed.setImage("attachment://thumbnail.png");
            cmdChannel.sendFiles(FileUpload.fromData(file, "thumbnail.png"))
                    .setEmbeds(embed.build())
                    .addActionRow(ActionRows.playerRow(queue.isPlaying()))
                    .queue(msg -> playEmbedMsg = msg);
        } else {
            // without thumbnail
            cmdChannel.sendMessageEmbeds(embed.build())
                    .addActionRow(ActionRows.playerRow(queue.isPlaying()))
                    .queue(msg -> playEmbedMsg = msg);
        }
    }

    private EmbedBuilder getPlayEmbed(AudioTrack track) {
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

    private InputStream getThumbnail(AudioTrack track) {
        String videoID = track.getInfo().uri.replaceFirst("https://(www\\.)?youtube\\.com/watch\\?v=", "");

        InputStream file;
        try {
            file = new URL("https://img.youtube.com/vi/" + videoID + "/hqdefault.jpg").openStream();
        } catch(IOException e) {
            return null;
        }
        return file;
    }

    private boolean newPlayEmbedNeeded() {
        long interactedId = playEmbedMsg.getIdLong();
        CompletableFuture<Boolean> futureResult = new CompletableFuture<>();

        cmdChannel.getHistory().retrievePast(recentMessageThreshold)
                .map(messages -> messages.stream()
                        .map(Message::getIdLong)
                        .collect(Collectors.toSet()))
                .queue(ids -> futureResult.complete(ids.contains(interactedId)));

        // wait for async call to complete
        return !futureResult.join();
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

    public int getVolume() {
        return this.player.getVolume();
    }

    public void setVolume(int volume) {
        this.player.setVolume(volume);
    }
}
