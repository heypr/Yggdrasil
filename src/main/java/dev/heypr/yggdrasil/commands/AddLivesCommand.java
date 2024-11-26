package dev.heypr.yggdrasil.commands;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The admin command for adding/setting a certain number of lives to a player
 */
public class AddLivesCommand implements CommandExecutor {

    private final Yggdrasil plugin;

    public AddLivesCommand(Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(String.format("Usage: /%s <player> <amount>", label));
            return true;
        }

        Player target = sender.getServer().getPlayer(args[0]);
        boolean isSet = label.equalsIgnoreCase("setlives");

        if (target == null) {
            sender.sendMessage("Player not found");
            return true;
        }

        if (args.length == 2) {
            int amount = Integer.parseInt(args[1]);

            boolean tooLow = isSet ? amount < 0 : amount < 1;

            if (tooLow) {
                sender.sendMessage("Invalid amount.");
                return true;
            }

            Player player = (Player) sender;
            int targetLives = plugin.getPlayerData().get(target.getUniqueId()).getLives();
            boolean aboveMax = isSet ? amount > Yggdrasil.MAX_LIVES : targetLives + amount > Yggdrasil.MAX_LIVES;

            if (aboveMax) {
                sender.sendMessage(String.format("Player cannot have more than %s lives.", Yggdrasil.MAX_LIVES));
                return true;
            }

            if (!isSet) {
                player.sendMessage("You have given " + amount + " lives to " + target.getName());
                target.sendMessage("You have been given " + amount + " lives");
            } else {
                player.sendMessage("You have set the lives of " + target.getName() + " to " + amount);
                target.sendMessage("Your lives have been set to " + amount);
            }

            boolean currentlyDead = targetLives == 0;
            boolean revival = isSet ? currentlyDead && amount > 0 : currentlyDead;
            PlayerData targetData = plugin.getPlayerData().get(target.getUniqueId());

            if (revival)
                targetData.revive();

            if (isSet)
                targetData.setLives(amount);
            else
                targetData.addLives(amount);
            return true;
        }

        else {
            sender.sendMessage("Invalid amount.");
        }

        return true;
    }
}
