package de.officeryoda.bot.discord.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;

@Getter
public class MusicController {

    private final MusicMaster master;
    private final Guild guild;
    private final AudioPlayer player;
    private final Queue queue;
    @Setter
    private boolean looping;

    public MusicController(MusicMaster master, Guild guild) {
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
}
