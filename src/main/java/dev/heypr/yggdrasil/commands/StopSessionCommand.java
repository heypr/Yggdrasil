package dev.heypr.yggdrasil.commands;

import dev.heypr.yggdrasil.Yggdrasil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.temporal.ChronoUnit;
import java.util.Date;

public class StopSessionCommand implements CommandExecutor {

    private final Yggdrasil plugin;

    public StopSessionCommand(Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!plugin.isSessionRunning) {
            sender.sendMessage("Session is not running.");
            return true;
        }

        plugin.getDeadPlayers().forEach(player -> {
            player.ban("Banned for dying.", new Date().toInstant().plus(30, ChronoUnit.DAYS),null, true);
            plugin.getDeadPlayers().remove(player);
        });

        plugin.getPlayerData().forEach((uuid, playerData) -> {
            if (playerData.isBoogeyman()) {
                playerData.setBoogeyman(false);
            }
            Player player = plugin.getServer().getPlayer(uuid);

//            NamespacedKey livesKey = new NamespacedKey(plugin, "lives");
//
//            player.getPersistentDataContainer().set(livesKey, PersistentDataType.INTEGER, plugin.getPlayerData().get(player.getUniqueId()).getLives());
        });

        plugin.isSessionRunning = false;
        return true;
    }
}
