package de.officeryoda.Commands.Executer;

import de.officeryoda.CantinaBand;
import de.officeryoda.Commands.Managment.CommandExecuter;
import de.officeryoda.Music.MusicController;
import de.officeryoda.Music.MusicMaster;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class CmdVolume implements CommandExecuter {

    private MusicMaster master;

    public CmdVolume() {
        master = CantinaBand.INSTANCE.getMusicMaster();
    }

    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        MusicController controller = master.getController(event.getGuild().getIdLong());
        OptionMapping messageOption = event.getOption("volume");
        if(messageOption == null) {
            event.reply("The volume is " + controller.getVolume()).queue();
        } else {
            int volume = messageOption.getAsInt();
            controller.setVolume(volume);
            event.reply("Set the volume to " + volume).queue();
        }
    }
}
