package dev.heypr.yggdrasil.commands;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.data.PlayerData;
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

public class AddPlayerCommand implements CommandExecutor {

    private final Yggdrasil plugin;

    public AddPlayerCommand(Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 1) {
            sender.sendMessage("Usage: /addplayer <player>");
            return true;
        }

        Player target = sender.getServer().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        if (plugin.getPlayerData().containsKey(target.getUniqueId())) {
            sender.sendMessage("Player already added.");
            return true;
        }

        plugin.getPlayerData().putIfAbsent(target.getUniqueId(), new PlayerData(target.getUniqueId(), plugin.randomNumber(2, 6)));

        target.sendTitle(ChatColor.GRAY + "You will have...", "", 10, 20, 10);
        new BukkitRunnable() {

            int e = 5;

            @Override
            public void run() {
                if (e > 0) {
                    int lives = plugin.randomNumber(2, 6);
                    switch (lives) {
                        case 2:
                            target.sendTitle(ChatColor.YELLOW + "" + lives,
                                    "",
                                    10, 20, 10);
                            break;
                        case 4, 3:
                            target.sendTitle(ChatColor.GREEN + "" + lives,
                                    "",
                                    10, 20, 10);
                            break;
                        case 6, 5:
                            target.sendTitle(ChatColor.DARK_GREEN + "" + lives,
                                    "",
                                    10, 20, 10);
                            break;
                    }
                    e--;
                }
                else {
                    int lives = plugin.getPlayerData().get(target.getUniqueId()).getLives();
                    switch (lives) {
                        case 2:
                            target.sendTitle(ChatColor.YELLOW + "" + lives + " lives",
                                    "",
                                    10, 20, 10);
                            break;
                        case 4, 3:
                            target.sendTitle(ChatColor.GREEN + "" + lives + " lives",
                                    "",
                                    10, 20, 10);
                            break;
                        case 6, 5:
                            target.sendTitle(ChatColor.DARK_GREEN + "" + lives + " lives",
                                    "",
                                    10, 20, 10);
                            break;
                    }
                    Component component = Component.text(" (" + plugin.getPlayerData().get(target.getUniqueId()).getLives() + " lives)").decoration(TextDecoration.ITALIC, false).color(TextColor.color(128, 128, 128));

                    target.playerListName(target.name().append(component));
                    switch (plugin.getPlayerData().get(target.getUniqueId()).getLives()) {
                        case 5, 6:
                            target.playerListName(target.name().color(TextColor.color(0, 170, 0)).append(component));
                            break;
                        case 3, 4:
                            target.playerListName(target.name().color(TextColor.color(85, 255, 85)).append(component));
                            break;
                        case 2:
                            target.playerListName(target.name().color(TextColor.color(255, 255, 85)).append(component));
                            break;
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 5);

        sender.sendMessage("Player " + target.getName() + " added.");
        return true;
    }
}
