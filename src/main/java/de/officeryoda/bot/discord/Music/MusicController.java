package de.officeryoda.bot.discord.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import lombok.Getter;

public class MusicController {

    @Getter
    private MusicMaster master;

    @Getter
    private Guild guild;
    @Getter
    private AudioPlayer player;
    @Getter
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

    public void setVolume(int volume) {
        this.player.setVolume(volume);
    }

    public int getVolume() {
        return this.player.getVolume();
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }
}
