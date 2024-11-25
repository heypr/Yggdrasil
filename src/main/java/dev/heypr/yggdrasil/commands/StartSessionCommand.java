package dev.heypr.yggdrasil.commands;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.data.PlayerData;
import dev.heypr.yggdrasil.misc.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StartSessionCommand implements CommandExecutor {

    private final Yggdrasil plugin;

    public StartSessionCommand(Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (plugin.isSessionRunning) {
            sender.sendMessage("Game is already running.");
            return true;
        }

        Map<UUID, PlayerData> playerData = plugin.getPlayerData();

        playerData.clear();

        List<Player> players = new ArrayList<>(plugin.getServer().getOnlinePlayers());
        if (players.isEmpty()) return true;

        int numBoogeymen = plugin.randomNumber(1, 3);

        for (int i = 0; i < numBoogeymen && i < players.size() - 1; i++) {
            Player boogeyman = players.get(i);

            playerData.put(boogeyman.getUniqueId(), new PlayerData(boogeyman.getUniqueId(), plugin.randomNumber(2, 6)));
            playerData.get(boogeyman.getUniqueId()).setBoogeyman(true);

            boogeyman.sendTitle(ChatColor.GRAY + "You will have...", "", 10, 20, 10);

            new BukkitRunnable() {
                int e = 5;

                @Override
                public void run() {
                    if (e > 0) {
                        int lives = plugin.randomNumber(2, 6);
                        ChatColor color = Colors.getColor(lives);

                        boogeyman.sendTitle(color + "" + lives,
                                "",
                                10, 20, 10);
                        e--;
                    }
                    else {
                        int lives = plugin.getPlayerData().get(boogeyman.getUniqueId()).getLives();
                        ChatColor color = Colors.getColor(lives);

                        boogeyman.sendTitle(color + "" + lives + " lives",
                                "",
                                10, 20, 10);

                        Component component = Component.text(" (" + plugin.getPlayerData().get(boogeyman.getUniqueId()).getLives() + " lives)").decoration(TextDecoration.ITALIC, false).color(TextColor.color(128, 128, 128));
                        boogeyman.playerListName(boogeyman.name().append(component));

                        switch (plugin.getPlayerData().get(boogeyman.getUniqueId()).getLives()) {
                            case 5, 6:
                                boogeyman.playerListName(boogeyman.name().color(TextColor.color(0, 170, 0)).append(component));
                                break;
                            case 3, 4:
                                boogeyman.playerListName(boogeyman.name().color(TextColor.color(85, 255, 85)).append(component));
                                break;
                            case 2:
                                boogeyman.playerListName(boogeyman.name().color(TextColor.color(255, 255, 85)).append(component));
                                break;
                        }
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 40, 5);

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
                }, 260L);
        }

        players.forEach(player -> {
            playerData.putIfAbsent(player.getUniqueId(), new PlayerData(player.getUniqueId(), plugin.randomNumber(2, 6)));
            if (!playerData.get(player.getUniqueId()).isBoogeyman()) {
                player.sendTitle(ChatColor.GRAY + "You will have...", "", 10, 20, 10);
                new BukkitRunnable() {

                    int e = 5;

                    @Override
                    public void run() {
                        if (e > 0) {
                            int lives = plugin.randomNumber(2, 6);
                            ChatColor color = Colors.getColor(lives);

                            player.sendTitle(color + "" + lives,
                                    "",
                                    10, 20, 10);
                            e--;
                        }
                        else {
                            int lives = plugin.getPlayerData().get(player.getUniqueId()).getLives();
                            ChatColor color = Colors.getColor(lives);

                            player.sendTitle(color + "" + lives + " lives",
                                    "",
                                    10, 20, 10);

                            Component component = Component.text(" (" + plugin.getPlayerData().get(player.getUniqueId()).getLives() + " lives)").decoration(TextDecoration.ITALIC, false).color(TextColor.color(128, 128, 128));
                            player.playerListName(player.name().append(component));

                            switch (plugin.getPlayerData().get(player.getUniqueId()).getLives()) {
                                case 5, 6:
                                    player.playerListName(player.name().color(TextColor.color(0, 170, 0)).append(component));
                                    break;
                                case 3, 4:
                                    player.playerListName(player.name().color(TextColor.color(85, 255, 85)).append(component));
                                    break;
                                case 2:
                                    player.playerListName(player.name().color(TextColor.color(255, 255, 85)).append(component));
                                    break;
                            }
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 40, 5);

                plugin.getScheduler().runTaskLater(plugin, () -> {
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
                }, 260L);
            }
        });

        plugin.isSessionRunning = true;
        return true;
    }
}
