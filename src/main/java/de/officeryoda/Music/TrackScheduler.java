package de.officeryoda.Music;

import java.util.HashMap;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import de.officeryoda.CantinaBand;
import net.dv8tion.jda.api.entities.Guild;

public class TrackScheduler extends AudioEventAdapter {

    private CantinaBand cantinaBand;
    private MusicMaster master;
    private HashMap<Guild, String> lastUri;

    public TrackScheduler(MusicMaster master) {
        this.cantinaBand = CantinaBand.INSTANCE;
        this.master = master;
        lastUri = new HashMap<>();
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        //		Main.getMainGui().updateConsole();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        //		Main.getMainGui().updateConsole();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        Long guildId = master.getGuildIdByPlayerHash(player.hashCode());
        Guild guild = cantinaBand.getGuildById(guildId);
        MusicController controller = master.getController(guildId);
        Queue queue = controller.getQueue();
        queue.setPlaying(true);

        setLastUri(guild, track.getInfo().uri);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(!endReason.mayStartNext) return;

        Long guildId = master.getGuildIdByPlayerHash(player.hashCode());
        Guild guild = cantinaBand.getGuildById(guildId);
        MusicController controller = master.getController(guildId);
        Queue queue = controller.getQueue();

//        if(controller.isLooping()) {
//            String lastUri = getLastUri(guild);
//            master.getPlayerManager().loadItem(lastUri, new AudioLoadResult(controller, lastUri));
//            return;
//        }

        if(queue.next())
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

    public String getLastUri(Guild guild) {
        return lastUri.getOrDefault(guild, "https://www.youtube.com/watch?v=dQw4w9WgXcQ&ab_channel=RickAstley");
    }

    public void setLastUri(Guild guild, String value) {
        lastUri.put(guild, value);
    }
}
