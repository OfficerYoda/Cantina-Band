package de.officeryoda.bot.discord.Miscellaneous;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;

public class ActionRows {

    public static List<Button> queueNavigationRow() {
        Button b1 = Button.primary("queueFarPrevious", Emoji.fromUnicode("⏪"));
        Button b2 = Button.primary("queuePrevious", Emoji.fromUnicode("◀️"));
        Button b3 = Button.primary("queueNext", Emoji.fromUnicode("▶️"));
        Button b4 = Button.primary("queueFarNext", Emoji.fromUnicode("⏩"));

        List<Button> buttons = new ArrayList<>();
        buttons.add(b1);
        buttons.add(b2);
        buttons.add(b3);
        buttons.add(b4);

        return buttons;
    }

    public static List<Button> queueSecondaryRow() {
        Button b5 = Button.primary("queueShuffle", Emoji.fromUnicode("\uD83D\uDD00"));
//        Button b6 = Button.primary("queueRefresh", Emoji.fromUnicode("\uD83D\uDD04"));

        List<Button> buttons = new ArrayList<>();
        buttons.add(b5);
//        buttons.add(b6);

        return buttons;
    }

    public static List<Button> playerRow(boolean isPlaying) {
        Button b1 = Button.primary("playerPreviousTrack", Emoji.fromUnicode("⏮"));
        Button b2 = Button.primary("playerTogglePlay", Emoji.fromUnicode(isPlaying ? "⏸" : "▶"));
        Button b3 = Button.primary("playerNextTrack", Emoji.fromUnicode("⏭"));

        List<Button> buttons = new ArrayList<>();
        buttons.add(b1);
        buttons.add(b2);
        buttons.add(b3);

        return buttons;
    }
}
