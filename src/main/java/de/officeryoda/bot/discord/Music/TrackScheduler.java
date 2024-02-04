package de.officeryoda.bot.discord.Music;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import de.officeryoda.bot.discord.CantinaBand;

public class TrackScheduler extends AudioEventAdapter {

    private final CantinaBand cantinaBand;
    private final MusicMaster master;

    public TrackScheduler(MusicMaster master) {
        this.cantinaBand = CantinaBand.INSTANCE;
        this.master = master;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        Long guildId = master.getGuildIdByPlayerHash(player.hashCode());
        MusicController controller = master.getController(guildId);
        Queue queue = controller.getQueue();
        queue.setPlaying(true);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(!endReason.mayStartNext) return;

        Long guildId = master.getGuildIdByPlayerHash(player.hashCode());
        MusicController controller = master.getController(guildId);
        Queue queue = controller.getQueue();

        if(queue.next()) // still has songs in the queue
            return;

        queue.setPlaying(false);
        player.stopTrack();

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }
}
