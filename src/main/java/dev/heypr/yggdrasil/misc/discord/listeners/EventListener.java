package dev.heypr.yggdrasil.misc.discord.listeners;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.misc.ColorManager;
import dev.heypr.yggdrasil.misc.discord.command.CommandManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public final class EventListener extends ListenerAdapter {
    public static Guild guild;

    private void createRole(final Guild guild, final ColorManager.Colors colors) {
        final String name = colors.name().toLowerCase();
        final boolean exists = guild.getRolesByName(name, false).stream().anyMatch(role -> role.getColor().equals(colors.getColor()));

        if (!exists) {
            guild.createRole()
                    .setColor(colors.getColor())
                    .setName(name)
                    .queue();
        }
    }

    @Override
    public void onGuildReady(final GuildReadyEvent e) {
        final Guild guild = e.getGuild();
        final long id = guild.getIdLong();
        ConfigurationSection section = Yggdrasil.plugin.getConfig().getConfigurationSection("discord.guilds");

        EventListener.guild = guild;

        boolean registered = false;

        if (!section.contains(String.valueOf(id)))
            section = section.createSection(String.valueOf(id));
        else {
            section = section.getConfigurationSection(String.valueOf(id));
            registered = section.contains("registered") && section.getBoolean("registered");
        }

        if (!registered) {
            this.createSlashCommands(e.getGuild());
            section.set("registered", true);
            Yggdrasil.plugin.saveConfig();
        }

        for (final ColorManager.Colors colors : ColorManager.Colors.values())
            this.createRole(guild, colors);
    }

    private void createSlashCommands(final Guild guild) {
        final List<CommandData> data = new ArrayList<>();
        CommandManager.getCommands().forEach(command -> data.add(command.getData()));

        guild.updateCommands().addCommands(data).queue(commands -> Yggdrasil.plugin.getLogger().info(String.format("Created discord slash commands for guild %s.", guild.getId())));
    }

    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent e) {
        this.handleCommand(e);
    }

    private void handleCommand(final SlashCommandInteractionEvent e) {
        final String command = e.getName();
        final List<OptionMapping> args = e.getOptions();

        CommandManager.handleCommand(e, command, args);
    }
}