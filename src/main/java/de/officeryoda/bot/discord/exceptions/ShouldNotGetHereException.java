package de.officeryoda.bot.discord.exceptions;

public class ShouldNotGetHereException extends RuntimeException {
    public ShouldNotGetHereException() {
        super("This code path should not be reached.");
    }

    public ShouldNotGetHereException(String message) {
        super(message);
    }
}
