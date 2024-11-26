package dev.heypr.yggdrasil.misc.discord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public interface ISimpleCommand {
    String name();
    String description();
    Response execute(final SlashCommandInteractionEvent e);

    default CommandData getData() {
        return Commands.slash(this.name(), this.description());
    }
}