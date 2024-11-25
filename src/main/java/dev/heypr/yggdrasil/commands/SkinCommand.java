package dev.heypr.yggdrasil.commands;

import dev.heypr.yggdrasil.Yggdrasil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class SkinCommand implements CommandExecutor {
    private final Yggdrasil plugin;

    public SkinCommand(Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/skin <file_path>");
            return true;
        }

        final String filePath = String.join(" ", args);
        final File skinFile = new File(filePath);

        if (!skinFile.exists()) {
            sender.sendMessage(ChatColor.RED + "That file does not exist.");
            return true;
        }

        plugin.skinManager.skin(player, skinFile, exception -> {
            if (exception == null)
                sender.sendMessage(ChatColor.GREEN + String.format("Successfully changed your skin to the file '%s'.", skinFile.getPath()));
            else
                sender.sendMessage(ChatColor.RED + String.format("Error updating skin -> %s", exception.getMessage()));
        });

        return true;
    }
}
