package dev.heypr.yggdrasil.commands;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.data.PlayerData;
import dev.heypr.yggdrasil.misc.ColorManager;
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

        final int lives = PlayerData.retrieveLivesOrDefault(target.getUniqueId(), plugin.randomNumber(2, 6));
        plugin.getPlayerData().putIfAbsent(target.getUniqueId(), new PlayerData(target.getUniqueId(), lives));

        target.sendTitle(ChatColor.GRAY + "You will have...", "", 10, 20, 10);
        new BukkitRunnable() {

            int e = 5;

            @Override
            public void run() {
                if (e > 0) {
                    int lives = plugin.randomNumber(2, 6);
                    ChatColor color = ColorManager.getColor(lives);

                    target.sendTitle(color + "" + lives + " lives",
                            "",
                            10, 20, 10);
                    e--;
                }
                else {
                    int lives = plugin.getPlayerData().get(target.getUniqueId()).getLives();
                    ChatColor color = ColorManager.getColor(lives);

                    target.sendTitle(color + "" + lives + " lives",
                            "",
                            10, 20, 10);

                    ColorManager.setTabListName(plugin, target, plugin.getPlayerData().get(target.getUniqueId()).getLives());
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 5);

        sender.sendMessage("Player " + target.getName() + " added.");
        return true;
    }
}
