package dev.heypr.yggdrasil.commands;

import dev.heypr.yggdrasil.Yggdrasil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public final class SetDiscordTokenCommand implements CommandExecutor {
    private final Yggdrasil plugin;

    public SetDiscordTokenCommand(final Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/setdiscordtoken <token>");
            return true;
        }

        final String token = args[0];
        final ConfigurationSection section = plugin.getConfig().getConfigurationSection("discord");

        section.set("token", token);
        plugin.saveConfig();

        sender.sendMessage(ChatColor.GREEN + "Successfully set the discord token.");

        if (plugin.getBot() == null) {
            sender.sendMessage(ChatColor.GOLD + "Attempting to load bot...");
            plugin.loadBot();
        }

        return true;
    }
}