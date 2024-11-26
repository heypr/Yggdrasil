package dev.heypr.yggdrasil.misc.discord.command;

import java.awt.*;

public enum ResponseType {
    SUCCESS("Success", Colors.GREEN), // For when the command is run and everything goes as it should
    NO_PERMISSION("No permission", Colors.RED), // For when the user does not have permission to run specified command
    BANNED("Banned", Colors.DARKEST_RED), // For when the user is banned and the command does not allow banned users to run it
    ERROR("Error", Colors.DARK_RED), // For when there is some kind of error or something wrong with the users input
    NOT_FOUND("Not found", Colors.YELLOW); // For when a value could not be found

    private final String title;
    private final Color color;

    ResponseType(final String title, final Color color) {
        this.title = title;
        this.color = color;
    }

    public String getTitle() {
        return this.title;
    }

    public Color getColor() {
        return this.color;
    }
}

class Colors {
    public static final Color GREEN = Color.GREEN;
    public static final Color RED = Color.RED;
    public static final Color DARK_RED = new Color(145, 0, 0);
    public static final Color DARKEST_RED = new Color(69, 0, 0);
    public static final Color YELLOW = Color.YELLOW;
}