package dev.heypr.yggdrasil.commands.impl;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RandomizeBoogeymanCommand implements CommandExecutor {

    private final Yggdrasil plugin;

    public RandomizeBoogeymanCommand(Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<Player> players = new ArrayList<>(plugin.getServer().getOnlinePlayers());
        Map<UUID, PlayerData> playerData = plugin.getPlayerData();

        if (players.isEmpty()) return true;

        for (final PlayerData data : playerData.values())
            data.setBoogeyman(false);

        int numBoogeymen = plugin.randomNumber(1, 3);
        List<Player> potentialBoogyMen = plugin.getBoogieManPool();

        for (int i = 0; i < numBoogeymen && i < potentialBoogyMen.size() - 1; i++) {
            Player boogeyman = potentialBoogyMen.get(i);
            final int lives = PlayerData.retrieveLivesOrDefault(boogeyman.getUniqueId(), plugin.randomNumber(2, 6));
            playerData.putIfAbsent(boogeyman.getUniqueId(), new PlayerData(boogeyman.getUniqueId(), lives));
            playerData.get(boogeyman.getUniqueId()).setBoogeyman(true);

            plugin.getScheduler().runTaskLater(plugin, () -> {
                boogeyman.sendTitle(ChatColor.GREEN + "3", "", 10, 20, 10);
                plugin.getScheduler().runTaskLater(plugin, () -> {
                    boogeyman.sendTitle(ChatColor.YELLOW + "2", "", 10, 20, 10);
                    plugin.getScheduler().runTaskLater(plugin, () -> {
                        boogeyman.sendTitle(ChatColor.RED + "1", "", 10, 20, 10);
                        plugin.getScheduler().runTaskLater(plugin, () -> {
                            boogeyman.sendTitle(ChatColor.YELLOW + "You are...", "", 10, 70, 20);
                            plugin.getScheduler().runTaskLater(plugin, () -> {
                                boogeyman.sendTitle(ChatColor.RED + "THE BOOGEYMAN!", "", 10, 70, 20);
                            }, 60L);
                        }, 20L);
                    }, 20L);
                }, 20L);
            }, 40L);
        }

        players.forEach(player -> {
            if (!playerData.get(player.getUniqueId()).isBoogeyman()) {
                plugin.getScheduler().runTaskLater(plugin, () -> {
                    final int lives = PlayerData.retrieveLivesOrDefault(player.getUniqueId(), plugin.randomNumber(2, 6));
                    playerData.putIfAbsent(player.getUniqueId(), new PlayerData(player.getUniqueId(), lives));
                    player.sendTitle(ChatColor.GREEN + "3", "", 10, 20, 10);
                    plugin.getScheduler().runTaskLater(plugin, () -> {
                        player.sendTitle(ChatColor.YELLOW + "2", "", 10, 20, 10);
                        plugin.getScheduler().runTaskLater(plugin, () -> {
                            player.sendTitle(ChatColor.RED + "1", "", 10, 20, 10);
                            plugin.getScheduler().runTaskLater(plugin, () -> {
                                player.sendTitle(ChatColor.YELLOW + "You are...", "", 10, 70, 20);
                                plugin.getScheduler().runTaskLater(plugin, () -> {
                                    player.sendTitle(ChatColor.GREEN + "NOT THE BOOGEYMAN!", "", 10, 70, 20);
                                }, 60L);
                            }, 20L);
                        }, 20L);
                    }, 20L);
                }, 40L);
            }
        });

        return true;
    }
}