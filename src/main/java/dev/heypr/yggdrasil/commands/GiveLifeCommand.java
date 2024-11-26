package dev.heypr.yggdrasil.commands;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The player command for giving another player one of your lives
 */
public class GiveLifeCommand implements CommandExecutor {

    private final Yggdrasil plugin;

    public GiveLifeCommand(Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 2) {
            sender.sendMessage("Usage: /givelife <player> <amount>");
            return true;
        }

        Player target = sender.getServer().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage("Player not found");
            return true;
        }

        if (args.length == 2) {

            int amount = Integer.parseInt(args[1]);
            if (amount < 1) {
                sender.sendMessage("Invalid amount.");
                return true;
            }

            Player player = (Player) sender;

            if (target.equals(player)) {
                sender.sendMessage("You cannot give yourself lives. If you are an admin please use /addlives instead.");
                return true;
            }

            int targetLives = plugin.getPlayerData().get(target.getUniqueId()).getLives();
            int playerLives = plugin.getPlayerData().get(player.getUniqueId()).getLives();

            if (playerLives < amount) {
                sender.sendMessage("You do not have enough lives.");
                return true;
            }

            if (playerLives - amount < 2) {
                sender.sendMessage("You do not have enough lives or would die if you gave that many.");
                return true;
            }

            if (targetLives + amount > Yggdrasil.MAX_LIVES) {
                sender.sendMessage(String.format("Player cannot have more than %s lives.", Yggdrasil.MAX_LIVES));
                return true;
            }

            player.sendMessage("You have given " + amount + " lives to " + target.getName());
            target.sendMessage("You have been given " + amount + " lives");

            final PlayerData playerData = plugin.getPlayerData().get(player.getUniqueId());
            final PlayerData targetData = plugin.getPlayerData().get(player.getUniqueId());

            if (targetLives == 0)
                targetData.revive();

            playerData.decreaseLives(amount);
            targetData.addLives(amount);
            return true;
        }

        else {
            sender.sendMessage("Invalid amount.");
        }

        return true;
    }
}
