package de.officeryoda.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import de.officeryoda.CantinaBand;

import java.util.HashMap;
import java.util.Map;

public class MusicMaster {


    private final CantinaBand cantinaBand;
    private final AudioPlayerManager playerManager;
    private final Map<Long, MusicController> controller;

    public MusicMaster() {
        playerManager = new DefaultAudioPlayerManager();
        playerManager.setFrameBufferDuration(10);
        AudioSourceManagers.registerRemoteSources(playerManager);

        cantinaBand = CantinaBand.INSTANCE;
        controller = new HashMap<>();
    }

    public MusicController getController(long guildId) {
        MusicController mc = null;

        if(controller.containsKey(guildId)) {
            mc = this.controller.get(guildId);
        } else {
            mc = new MusicController(this, cantinaBand.getGuildById(guildId));

            this.controller.put(guildId, mc);
        }

        return mc;
    }

    public Long getGuildIdByPlayerHash(int hash) {
        for(MusicController controller : this.controller.values())
            if(controller.getPlayer().hashCode() == hash)
                return controller.getGuild().getIdLong();

        return -1L;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }
}
