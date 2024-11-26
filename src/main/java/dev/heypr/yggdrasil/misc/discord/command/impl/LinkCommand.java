package dev.heypr.yggdrasil.misc.discord.command.impl;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.misc.discord.command.ISimpleCommand;
import dev.heypr.yggdrasil.misc.discord.command.Response;
import dev.heypr.yggdrasil.misc.discord.command.ResponseType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class LinkCommand implements ISimpleCommand {
    public record DiscordId(String id, String name) {
    }

    private final Map<String, DiscordId> codeMap = new HashMap<>(); // code -> discord id

    public static String generateString(final int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    @Override
    public String name() {
        return "link";
    }

    @Override
    public String description() {
        return "Generate random code used to link ingame account. Type code in chat to link.";
    }

    private String getCode(final String discordId) {
        for (final Map.Entry<String, DiscordId> entry : this.codeMap.entrySet()) {
            if (entry.getValue().id().equalsIgnoreCase(discordId))
                return entry.getKey();
        }

        return null;
    }

    @Override
    public Response execute(final SlashCommandInteractionEvent e) {
        final String discordId = e.getUser().getId();
        final ConfigurationSection section = Yggdrasil.plugin.getConfig().getConfigurationSection("discord.linked");

        if (section.contains(discordId))
            return Response.ResponseBuilder.response("Your account appears to already be linked!").setType(ResponseType.ERROR).build();

        String code;
        final DiscordId id = new DiscordId(discordId, e.getUser().getName());

        if (this.codeMap.containsValue(id))
            code = this.getCode(discordId);
        else {
            code = generateString(32);
            this.codeMap.put(code, id);
        }

        return Response.ResponseBuilder.response(String.format("Your code is `%s`. Type it in chat on the server to link your account.", code)).build();
    }

    public Map<String, DiscordId> getCodeMap() {
        return this.codeMap;
    }

    public ConfigurationSection getUserSection(final UUID uuid) {
        final ConfigurationSection section = Yggdrasil.plugin.getConfig().getConfigurationSection("discord.linked");

        for (final String discordId : section.getKeys(false)) {
            final ConfigurationSection userSection = section.getConfigurationSection(discordId);

            if (!userSection.getString("uuid").equals(uuid.toString()))
                continue;

            return userSection;
        }

        return null;
    }
}