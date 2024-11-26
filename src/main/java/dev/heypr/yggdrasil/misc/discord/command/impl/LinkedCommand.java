package dev.heypr.yggdrasil.misc.discord.command.impl;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.misc.discord.command.ISimpleCommand;
import dev.heypr.yggdrasil.misc.discord.command.Response;
import dev.heypr.yggdrasil.misc.discord.command.ResponseType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bukkit.configuration.ConfigurationSection;

public final class LinkedCommand implements ISimpleCommand {
    @Override
    public String name() {
        return "linked";
    }

    @Override
    public String description() {
        return "Tells you which account you are linked to.";
    }

    @Override
    public Response execute(final SlashCommandInteractionEvent e) {
        final String discordId = e.getUser().getId();
        final ConfigurationSection section = Yggdrasil.plugin.getConfig().getConfigurationSection("discord.linked");

        if (!section.contains(discordId))
            return Response.ResponseBuilder.response("Your account does not appear to be linked!").setType(ResponseType.ERROR).build();

        final ConfigurationSection userSection = section.getConfigurationSection(discordId);

        return Response.ResponseBuilder.response(String.format("You are linked to the account `%s` with uuid of `%s`.", userSection.get("name"), userSection.get("uuid"))).build();
    }
}