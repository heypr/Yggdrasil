package dev.heypr.yggdrasil.events;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.misc.discord.command.CommandManager;
import dev.heypr.yggdrasil.misc.discord.command.impl.LinkCommand;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class PlayerChatListener implements Listener {
    private final Yggdrasil plugin;

    public PlayerChatListener(final Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent e) {
        final Player player = e.getPlayer();
        final String message = e.getMessage();
        final LinkCommand linkCommand = CommandManager.getCommand("link");

        if (!linkCommand.getCodeMap().keySet().contains(message))
            return;

        final LinkCommand.DiscordId discordId = linkCommand.getCodeMap().get(message);

        final ConfigurationSection section = plugin.getConfig().getConfigurationSection("discord.linked");
        ConfigurationSection userSection = null;

        if (!section.contains(discordId.id()))
            userSection = section.createSection(discordId.id());

        userSection.set("uuid", player.getUniqueId().toString());
        userSection.set("name", player.getName());

        plugin.saveConfig();

        e.setCancelled(true);
        player.sendMessage(ChatColor.GREEN + String.format("You have successfully linked your current Minecraft account with '%s' discord account.", discordId.name()));

        linkCommand.getCodeMap().remove(message);
    }
}