package dev.heypr.yggdrasil.commands;

import dev.heypr.yggdrasil.Yggdrasil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LivesCommand implements CommandExecutor {

    private final Yggdrasil plugin;

    public LivesCommand(Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage("You have " + plugin.getPlayerData().get(((Player) sender).getUniqueId()).getLives() + " lives.");
        }
        else if (args.length == 1) {
            Player target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("Player not found");
                return true;
            }
            sender.sendMessage(target.getName() + " has " + plugin.getPlayerData().get(target.getUniqueId()).getLives() + " lives.");
            return true;
        }
        else {
            sender.sendMessage("Usage: /lives [player]");
        }

        return true;
    }
}
