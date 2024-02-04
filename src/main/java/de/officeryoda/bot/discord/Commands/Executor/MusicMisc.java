package de.officeryoda.bot.discord.Commands.Executor;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.officeryoda.bot.discord.CantinaBand;
import de.officeryoda.bot.discord.Commands.Managment.CommandExecutor;
import de.officeryoda.bot.discord.Music.MusicController;
import de.officeryoda.bot.discord.Music.MusicMaster;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class MusicMisc {

    public static class CmdVolume implements CommandExecutor {

        private final MusicMaster master;

        public CmdVolume() {
            master = CantinaBand.INSTANCE.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            Guild guild = event.getGuild();
            if(event.getMember().getVoiceState().getChannel() != guild.getAudioManager().getConnectedChannel()) {
                event.reply("You must be in our voice channel to use that!").queue();
                return;
            }

            MusicController controller = master.getController(guild.getIdLong());
            OptionMapping messageOption = event.getOption("volume");
            if(messageOption == null) {
                event.reply("The volume is `" + controller.getVolume() + "`.").queue();
            } else {
                int volume = messageOption.getAsInt();
                    controller.setVolume(volume);
                    event.reply("Set the volume to `" + volume + "`.").queue();
            }
        }
    }

    public static class CmdPause implements CommandExecutor {

        private final MusicMaster master;

        public CmdPause() {
            master = CantinaBand.INSTANCE.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            Guild guild = event.getGuild();

            if(event.getMember().getVoiceState().getChannel() != guild.getAudioManager().getConnectedChannel()) {
                event.reply("You must be in our voice channel to use that!").queue();
                return;
            }

            MusicController controller = master.getController(guild.getIdLong());
            AudioPlayer player = controller.getPlayer();

            if(player.isPaused()) {
                event.reply("The band is already taking a rest.").queue();
            } else {
                player.setPaused(true);
                event.reply("Paused playing.").queue();
            }
        }
    }

    public static class CmdResume implements CommandExecutor {

        private final MusicMaster master;

        public CmdResume() {
            master = CantinaBand.INSTANCE.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            Guild guild = event.getGuild();
            if(event.getMember().getVoiceState().getChannel() != guild.getAudioManager().getConnectedChannel()) {
                event.reply("You must be in our voice channel to use that!").queue();
                return;
            }

            MusicController controller = master.getController(guild.getIdLong());
            AudioPlayer player = controller.getPlayer();

            if(player.isPaused()) {
                player.setPaused(false);
                event.reply("Resumed playing.").queue();
            } else {
                event.reply("The band is already playing.").queue();
            }
        }
    }

    public static class CmdLoop implements CommandExecutor {

        private final MusicMaster master;

        public CmdLoop() {
            master = CantinaBand.INSTANCE.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            Guild guild = event.getGuild();
            if(event.getMember().getVoiceState().getChannel() != guild.getAudioManager().getConnectedChannel()) {
                event.reply("You must be in our voice channel to use that!").queue();
                return;
            }

            MusicController controller = master.getController(guild.getIdLong());

            OptionMapping messageOption = event.getOption("looping");

            if(messageOption == null) {
                event.reply("The band is currently "
                        + (controller.isLooping() ? "" : "not ") +
                        "playing den selben Song nochmal.").queue();
            } else {
                boolean looping = messageOption.getAsBoolean();
                controller.setLooping(looping);
                event.reply("The band is playing den selben Song nochmal.").queue();
            }
        }
    }

    public static class CmdToggleLoop implements CommandExecutor {

        private final MusicMaster master;

        public CmdToggleLoop() {
            master = CantinaBand.INSTANCE.getMusicMaster();
        }

        @Override
        public void executeCommand(SlashCommandInteractionEvent event) {
            Guild guild = event.getGuild();
            if(event.getMember().getVoiceState().getChannel() != guild.getAudioManager().getConnectedChannel()) {
                event.reply("You must be in our voice channel to use that!").queue();
                return;
            }

            MusicController controller = master.getController(guild.getIdLong());

            controller.setLooping(!controller.isLooping());

            event.reply("The band is "
                    + (controller.isLooping() ? "" : "not") +
                    " playing den selben Song nochmal.").queue();
        }
    }
}
