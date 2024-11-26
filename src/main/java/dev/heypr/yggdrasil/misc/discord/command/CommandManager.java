package dev.heypr.yggdrasil.misc.discord.command;

import dev.heypr.yggdrasil.misc.discord.BotUtils;
import dev.heypr.yggdrasil.misc.discord.command.impl.LinkCommand;
import dev.heypr.yggdrasil.misc.discord.command.impl.LinkedCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.ArrayList;
import java.util.List;

public final class CommandManager {
    private static final List<ISimpleCommand> commands = new ArrayList<>();

    public static void registerCommands() {
        registerCommand(new LinkCommand());
        registerCommand(new LinkedCommand());
    }

    public static void registerCommand(final ISimpleCommand command) {
        if (!commands.contains(command))
            commands.add(command);
    }

    public static List<ISimpleCommand> getCommands() {
        return commands;
    }

    public static <T extends ISimpleCommand> T getCommand(final String name) {
        for (final ISimpleCommand cmd : getCommands()) {
            if (!cmd.name().equalsIgnoreCase(name))
                continue;

            return (T) cmd;
        }

        return null;
    }

    public static void handleCommand(final SlashCommandInteractionEvent e, final String commandName, final List<OptionMapping> args) {
        try {
            final ISimpleCommand cmd = getCommand(commandName);

            if (cmd == null)
                return;

            final Response response = cmd.execute(e);

            if (response == null)
                return;

            CommandManager.handleResponse(e, response);
        } catch (final Exception exception) {
            if (!e.isAcknowledged()) {
                e.deferReply(true).queue();
                e.getHook().sendMessageEmbeds(BotUtils.getEmbed(ResponseType.ERROR, String.format("There was an error. %s", String.format("Debug: `%s: %s`", exception.getClass().getName(), exception.getMessage())))).setEphemeral(true).queue();
            }

            exception.printStackTrace();
        }
    }

    public static void handleResponse(final SlashCommandInteractionEvent e, final Response response) {
        if (!e.isAcknowledged())
            e.deferReply(response.isEphemeral()).queue();

        final boolean nonEmbed = response.isNonEmbed();
        final List<FileUpload> uploads = response.getUploads();
        final List<Button> buttons = response.getButtons();

        final WebhookMessageCreateAction<Message> action;
        if (nonEmbed)
            action = e.getHook().sendMessage(response.getResponse());
        else
            action = e.getHook().sendMessageEmbeds(BotUtils.getEmbed(response.getType(), response.getResponse()));

        action.setEphemeral(response.isEphemeral());

        if (!uploads.isEmpty())
            action.addFiles(uploads);

        if (!buttons.isEmpty())
            action.setActionRow(buttons);

        action.queue();
    }
}