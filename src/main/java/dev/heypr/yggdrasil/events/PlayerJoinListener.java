package dev.heypr.yggdrasil.events;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.misc.ColorManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;

public class PlayerJoinListener implements Listener {

    private final Yggdrasil plugin;

    public PlayerJoinListener(Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("all")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!plugin.isSessionRunning) {
            player.sendTitle(ChatColor.RED + "Game not started", "Please wait for the game to start", 10, 70, 20);
            return;
        }
        if (!plugin.getPlayerData().containsKey(player.getUniqueId())) {
            player.sendTitle(ChatColor.RED + "Game in progress", ChatColor.RED + "You are not part of the game", 10, 70, 20);
            player.sendMessage(ChatColor.GREEN + "Game in progress. Request an admin to add you to the game using /addplayer <player>");

            plugin.getServer().getOperators().forEach((op) -> {
                if (!(op.isOnline())) return;
                op.getPlayer().sendMessage(ChatColor.DARK_RED + "[URGENT] Player " + player.getName() + " does not have game data. They will be treated as a dead player. Add them to the list of players using /addplayer <player>");
                return;
            });
            return;
        }

        final int lives = plugin.getPlayerData().get(player.getUniqueId()).getLives();
        final File skinFile = ColorManager.getSkinFile(plugin, player, lives);

        if (skinFile != null)
            plugin.skinManager.skin(player, skinFile);

        ColorManager.setTabListName(plugin, player, plugin.getPlayerData().get(player.getUniqueId()).getLives());

        plugin.getScheduler().runTaskLater(plugin, () -> ColorManager.setTabListName(plugin, player, plugin.getPlayerData().get(player.getUniqueId()).getLives()), 20L); // To fix it incase the skin thing removes it

        if (plugin.getPlayerData().get(player.getUniqueId()).getLives() == 0) {
            player.setGameMode(GameMode.ADVENTURE);

            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, PotionEffect.INFINITE_DURATION, 500));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 500));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 500));
        }
    }
}
