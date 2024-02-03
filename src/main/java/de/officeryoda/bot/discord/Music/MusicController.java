package de.officeryoda.bot.discord.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;

public class MusicController {

    private MusicMaster master;

    private Guild guild;
    private AudioPlayer player;
    private Queue queue;
    private boolean isLooping;

    public MusicController(MusicMaster master, Guild guild) {
        this.master = master;
        this.guild = guild;
        this.player = master.getPlayerManager().createPlayer();
        this.queue = new Queue(this);
        this.isLooping = false;

        this.guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
        this.player.addListener(new TrackScheduler(this.master));
    }

    public MusicMaster getMaster() {
        return master;
    }

    public Guild getGuild() {
        return guild;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public void setVolume(int volume) {
        this.player.setVolume(volume);
    }

    public int getVolume() {
        return this.player.getVolume();
    }

    public Queue getQueue() {
        return queue;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }
}
