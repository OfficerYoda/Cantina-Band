package de.officeryoda.Commands.Executer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import de.officeryoda.CantinaBand;
import de.officeryoda.Commands.Managment.CommandExecuter;
import de.officeryoda.Music.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;

public class CmdPlay implements CommandExecuter {

    private MusicMaster master;

    public CmdPlay() {
        this.master = CantinaBand.INSTANCE.getMusicMaster();
    }

    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
//        event.reply()
        OptionMapping messageOption = event.getOption("url");
        assert messageOption != null;
        String url = messageOption.getAsString();

        testPlay(event, url);
//        Guild guild = event.getGuild();
//        // This will get the first voice channel with the name "music"
//        // matching by voiceChannel.getName().equalsIgnoreCase("music")
//        VoiceChannel channel = guild.getVoiceChannelsByName("music", true).get(0);
//        AudioManager manager = guild.getAudioManager();
//
//        // MySendHandler should be your AudioSendHandler implementation
//        MusicController controller = master.getController(event.getGuild().getIdLong());
//        manager.setSendingHandler(new AudioPlayerSendHandler(controller.getPlayer()));
//        // Here we finally connect to the target voice channel
//        // and it will automatically start pulling the audio from the MySendHandler instance
//        manager.openAudioConnection(channel);
//        master.getPlayerManager().loadItem(url, new AudioLoadResult(controller, url));
    }

    private void testPlay(SlashCommandInteractionEvent event, String url) {
        Guild guild = event.getGuild();
        GuildVoiceState state;
        AudioChannelUnion vc;

        if((state = event.getMember().getVoiceState()) == null || (vc = state.getChannel()) == null) {
            event.reply("You must be in a voice channel to use that!").setEphemeral(true).queue();
            return;
        }


        assert guild != null;
        MusicController controller = master.getController(guild.getIdLong());
        Queue queue = controller.getQueue();
        AudioPlayerManager apm = master.getPlayerManager();
        AudioManager manager = guild.getAudioManager();

        if(queue.getLength() != 0)
            if(guild.getSelfMember().getVoiceState().getChannel() != event.getMember().getVoiceState().getChannel())
                event.reply("I'm not in your voiceChannel").setEphemeral(true).queue();

        queue.setCmdChannel(event.getChannel());
        manager.openAudioConnection(vc);

//        String url = builder.toString().trim();
        if(!url.startsWith("http")) {
            url = "ytsearch: " + url;
        }
        apm.loadItem(url, new AudioLoadResult(controller, url));
    }
}
